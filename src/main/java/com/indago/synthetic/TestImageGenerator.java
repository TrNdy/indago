package com.indago.synthetic;

import ij.IJ;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.indago.segment.DataMover;
import com.indago.segment.RandomForestFactory;
import com.indago.segment.RealDoubleNormalizeConverter;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.real.DoubleType;
import view.component.IddeaComponent;

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
}
