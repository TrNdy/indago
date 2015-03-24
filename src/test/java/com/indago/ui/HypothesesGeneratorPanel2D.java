/**
 *
 */
package com.indago.ui;

import io.scif.img.IO;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.RealARGBConverter;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.ui.viewer.InteractiveViewer2D;
import net.imglib2.view.Views;


/**
 * @author jug
 */
public class HypothesesGeneratorPanel2D< T extends RealType< T > & NativeType< T > > extends JPanel {

	private static final long serialVersionUID = -6647954366225155440L;

	private final RandomAccessibleInterval< T > img;

	public HypothesesGeneratorPanel2D( final RandomAccessibleInterval< T > img ) {
		this.img = img;
		new InteractiveViewer2D< T >( 800, 600, Views.extendZero( img ), new RealARGBConverter< T >( 0, 256 ) );
	}

	public static void main( final String[] args ) {
		final String pathprefix = "src/main/resources/synthetic/0001_z63";

		final ArrayImgFactory< DoubleType > factory = new ArrayImgFactory< DoubleType >();
		final Img< DoubleType > image =
				IO.openImgs( pathprefix + "/image-final_0001-z63.tif", factory, new DoubleType() ).get(
						0 );

		final JFrame frame = new JFrame( "HypothesesGeneratorPanel2D demo..." );
		frame.setBounds( 500, 500, 1024, 900 );
		frame.getContentPane().add( new HypothesesGeneratorPanel2D< DoubleType >( image ) );
		frame.setVisible( true );
	}
}
