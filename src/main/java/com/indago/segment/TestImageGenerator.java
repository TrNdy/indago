package com.indago.segment;

import ij.IJ;
import io.scif.img.ImgOpener;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import net.imglib2.Dimensions;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.real.DoubleType;
import view.component.IddeaComponent;

import com.indago.fg.FactorGraph;
import com.indago.fg.gui.FgPanel;
import com.indago.segment.fg.FactorGraphFactory;
import com.indago.segment.filteredcomponents.FilteredComponentTree;

public class TestImageGenerator {

	static JFrame frame = null;
	static List< Img< UnsignedIntType >> imgs = null;

	private static class Actions implements ActionListener {

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed( final ActionEvent arg0 ) {
			// so far the only action is the save action... so... SAVE!

			final String strPath = JOptionPane.showInputDialog( "Path to save to: " );
			if ( strPath == null ) return;

			final File path = new File( strPath );
			if ( path.isDirectory() && path.canWrite() ) {
				int i = 0;
				for ( final Img< UnsignedIntType > img : imgs ) {
					final String name = String.format( "Image_%03d", i );
					IJ.save( ImageJFunctions.wrap( img, name ), path + "/" + name + ".tiff" );
					i++;
				}
				JOptionPane.showMessageDialog( frame, "Done! Yay!" );
			} else {
				JOptionPane.showMessageDialog( frame, "Given path does not exist or can not be written to!" );
			}
		}

	}

	public static void main( final String[] args ) throws Exception {
//		final List< String > filenames = new ArrayList< String >();
//		filenames.add( "src/main/resources/forest1.tif" );
//		filenames.add( "src/main/resources/forest2.tif" );
//		filenames.add( "src/main/resources/forest3.tif" );
//		doIt( filenames, new UnsignedIntType() );

		final int width = 1024;
		final int height = 1024;
		final int numSeedPixels = 150;
		final double maxRadius = 50.;
		final double minDeltaR = .5;
		final double maxDeltaR = 6.;
		final double meanDeltaR = .5;
		final double sdDeltaR = 0;//1.5;
		final int minIntensity = 0;
		final int maxIntensity = 100;

		imgs = new ArrayList< Img< UnsignedIntType >>();
		for ( int f = 0; f < 10; f++ ) {
			imgs.add( RandomForestFactory.getForestImg( width, height, numSeedPixels, maxRadius, minDeltaR, maxDeltaR, meanDeltaR, sdDeltaR, minIntensity, maxIntensity, f ) );
		}
		final Img< UnsignedIntType > stack = DataMover.stackThemAsFrames( imgs );

		final IddeaComponent iddeaComponent = new IddeaComponent( Converters.convert( ( RandomAccessibleInterval< UnsignedIntType > ) stack, new RealDoubleNormalizeConverter< UnsignedIntType >( maxIntensity ), new DoubleType() ) );
		iddeaComponent.showMenu( true );
		final JMenuItem menuItemSave = new JMenuItem( "Save images..." );
		menuItemSave.addActionListener( new Actions() );
		iddeaComponent.getMenuBar().getMenu( 0 ).add( menuItemSave );
		iddeaComponent.installDefaultToolBar();
		iddeaComponent.setToolBarLocation( BorderLayout.WEST );
		iddeaComponent.setToolBarVisible( true );

		frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setBounds( 300, 300, width, height );
		frame.getContentPane().add( iddeaComponent );
		frame.setVisible( true );
	}

	public static < T extends RealType< T > & NativeType< T > > void doIt( final List< String > filenames, final T type ) throws Exception {
		final int minComponentSize = 10;
		final int maxComponentSize = 10000;
		final int maxGrowthPerStep = 1;
		final boolean darkToBright = false;

		final List< Img< T > > imgs = new ArrayList< Img< T > >();
		final List< FilteredComponentTree< T > > fctrees = new ArrayList< FilteredComponentTree< T > >();
		final List< LabelingForest > labelingForests = new ArrayList< LabelingForest >();
		final List< SegmentForest > segmentForests = new ArrayList< SegmentForest >();

		for ( final String filename : filenames ) {
			final Img< T > img = new ImgOpener().openImg( filename, new ArrayImgFactory< T >(), type );
			final Dimensions dims = img;
			imgs.add( img );

			final FilteredComponentTree< T > newtree = FilteredComponentTree.buildComponentTree( img, type, minComponentSize, maxComponentSize, maxGrowthPerStep, darkToBright );
			fctrees.add( newtree );

			final LabelingForest newLF = LabelingForest.fromForest( newtree, dims );
			labelingForests.add( newLF );

			final SegmentForest newSF = SegmentForest.fromLabelingForest( newLF );
			segmentForests.add( newSF );
		}

		final SegmentMultiForest segmentMultiForest = new SegmentMultiForest( segmentForests );

		System.out.println( "\n>>>> Pairwise Constraints <<<<" );
		new ShowConflicts( segmentMultiForest, false );
		System.out.println( "\n>>>> Compact Clique Constraints <<<<" );
		new ShowConflicts( segmentMultiForest, true );

		// Assign random costs to segments in MultiForest (for testing purposes)
		final RandomSegmentCosts costs = new RandomSegmentCosts( segmentMultiForest, 815 );
		final FactorGraph fg = FactorGraphFactory.createFromSegmentMultiForest( segmentMultiForest, costs, false );

		final JFrame guiFrame = new JFrame( "FG from SegmentMultiForest" );
		// Set window-closing action...
		guiFrame.addWindowListener( new WindowAdapter() {

			@Override
			public void windowClosing( final WindowEvent we ) {
				System.exit( 0 );
			}
		} );

		guiFrame.getContentPane().setLayout( new BorderLayout() );
		guiFrame.getContentPane().add( new FgPanel( fg ), BorderLayout.CENTER );
		guiFrame.setSize( 800, 600 );
		guiFrame.setVisible( true );
	}

	static class ShowConflicts {

		private final HashMap< Segment, Integer > segmentToId = new HashMap< Segment, Integer >();

		private int idGenerator = 0;

		public ShowConflicts( final SegmentMultiForest segmentMultiForest, final boolean doCompactConstraints ) {
			for ( final Segment segment : segmentMultiForest.roots() )
				printSegment( "", segment );

			System.out.println();
			final Collection< ? extends Collection< Segment > > cliques;
			if ( doCompactConstraints ) {
				cliques = segmentMultiForest.getConflictGraphCliques();
			} else {
				cliques = segmentMultiForest.getConflictGraphEdges();
			}
			for ( final Collection< Segment > clique : cliques ) {
				System.out.print( "( " );
				for ( final Segment segment : clique )
					System.out.print( String.format( "%2d ", segmentToId.get( segment ) ) );
				System.out.println( ")" );
			}
		}

		private void printSegment( final String prefix, final Segment segment ) {
			Integer id = segmentToId.get( segment );
			if ( id == null ) {
				id = new Integer( idGenerator++ );
				segmentToId.put( segment, id );
			}
			System.out.println( prefix + id );
			for ( final Segment c : segment.getChildren() )
				printSegment( prefix + "  ", c );
		}

	}

	/**
	 * Assigns random costs to all segments in a SegmentMultiForest.
	 * This is useful for testing artificial setups...
	 * 
	 * @author jug
	 */
	static class RandomSegmentCosts implements SegmentCosts {

		private final Random rand;
		private final HashMap< Segment, Double > segmentToCost = new HashMap< Segment, Double >();

		public RandomSegmentCosts( final SegmentMultiForest segmentMultiForest, final int randomSeed ) {
			rand = new Random( randomSeed );
			for ( final Segment root : segmentMultiForest.roots() ) {
				recursivleyDrawRandomCosts( root );
			}
		}

		/**
		 * Recursively draws random costs for given segment all all its
		 * children.
		 * 
		 * @param segment
		 */
		private void recursivleyDrawRandomCosts( final Segment segment ) {

			segmentToCost.put( segment, rand.nextDouble() );

			for ( final Segment child : segment.getChildren() ) {
				recursivleyDrawRandomCosts( child );
			}
		}

		/**
		 * @param segment
		 * @return the cost assigned to the given segment or Double.MAX_VALUE if
		 *         this segment is unknown.
		 */
		@Override
		public double getCost( final Segment segment ) {
			final Double muh = segmentToCost.get( segment );
			if ( muh != null ) { return muh.doubleValue(); }
			return Double.MAX_VALUE;
		}
	}
}
