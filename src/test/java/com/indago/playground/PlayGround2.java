package com.indago.playground;

import ij.ImageJ;
import io.scif.img.ImgOpener;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import net.imglib2.Dimensions;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedIntType;

import com.indago.fg.Assignment;
import com.indago.fg.FactorGraph;
import com.indago.fg.gui.FgPanel;
import com.indago.ilp.SolveBooleanFGGurobi;
import com.indago.segment.HypothesisPrinter;
import com.indago.segment.LabelingBuilder;
import com.indago.segment.LabelingForest;
import com.indago.segment.LabelingSegment;
import com.indago.segment.MinimalOverlapConflictGraph;
import com.indago.segment.RandomSegmentCosts;
import com.indago.segment.fg.FactorGraphFactory;
import com.indago.segment.filteredcomponents.FilteredComponentTree;
import com.indago.segment.filteredcomponents.FilteredComponentTree.Filter;
import com.indago.segment.filteredcomponents.FilteredComponentTree.MaxGrowthPerStep;

public class PlayGround2 {

	public static void main( final String[] args ) throws Exception {
		final List< String > filenames = new ArrayList< String >();
		filenames.add( "src/main/resources/forest1.tif" );
		filenames.add( "src/main/resources/forest2.tif" );
		filenames.add( "src/main/resources/forest3.tif" );
		new ImageJ();
		doIt( filenames, new UnsignedIntType() );
	}

	public static < T extends RealType< T > & NativeType< T > > void doIt( final List< String > filenames, final T type ) throws Exception {
		final int minComponentSize = 10;
		final int maxComponentSize = 10000 - 1;
		final Filter maxGrowthPerStep = new MaxGrowthPerStep( 1 );
		final boolean darkToBright = false;

		final List< Img< T > > imgs = new ArrayList<>();
		for ( final String filename : filenames ) {
			final Img< T > img = new ImgOpener().openImg( filename, new ArrayImgFactory< T >(), type );
			imgs.add( img );
		}
		final Dimensions dims = imgs.get( 0 );

		final List< FilteredComponentTree< T > > fctrees = new ArrayList<>();
		final LabelingBuilder labelingBuilder = new LabelingBuilder( dims );

		for ( final Img< T > img : imgs ) {
			final FilteredComponentTree< T > newtree = FilteredComponentTree.buildComponentTree( img, type, minComponentSize, maxComponentSize, maxGrowthPerStep, darkToBright );
			fctrees.add( newtree );
		}

		final long t0 = System.currentTimeMillis();

//		int i = 1;
		final List< LabelingForest > labelingForests = new ArrayList<>();
		for ( final FilteredComponentTree< T > fctree : fctrees ) {
			labelingForests.add( labelingBuilder.buildLabelingForest( fctree ) );
//			final Img< ARGBType > labels = ArrayImgs.argbs( dims.dimension( 0 ), dims.dimension( 1 ) );
//			VisualizeLabeling.colorLabels( labelingBuilder.getLabeling(), ColorStream.iterator(), labels );
//			ImageJFunctions.show( labels, "Labels " + i );
//			++i;
		}

		final MinimalOverlapConflictGraph conflictGraph = new MinimalOverlapConflictGraph( labelingBuilder );
//		final MultiForestConflictGraph conflictGraph = new MultiForestConflictGraph( labelingForests );
//		final PairwiseConflictGraph conflictGraph = new PairwiseConflictGraph( labelingBuilder );
		conflictGraph.getConflictGraphCliques();
		final long t1 = System.currentTimeMillis();
		System.out.println( ( t1 - t0 ) + " ms" );

		final HypothesisPrinter hp = new HypothesisPrinter();
		for ( final LabelingForest labelingForest : labelingForests ) {
			hp.assignIds( labelingForest );
			hp.printHypothesisForest( labelingForest );
		}
		System.out.println();
		hp.printConflictGraphCliques( conflictGraph );

		final ArrayList< LabelingSegment > segments = labelingBuilder.getSegments();
		final RandomSegmentCosts costs = new RandomSegmentCosts( segments, 815 ); // assign random costs to segments in MultiForest (for testing purposes)
		final FactorGraph fg = FactorGraphFactory.createFromConflictGraph( segments, conflictGraph, costs ).getFactorGraph();

		final SolveBooleanFGGurobi solver = new SolveBooleanFGGurobi();
		final Assignment assignment = solver.solve( fg );

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
}
