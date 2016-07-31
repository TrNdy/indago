/**
 *
 */
package com.indago.io;

import java.io.File;

import ij.IJ;
import ij.ImagePlus;
import io.scif.img.ImgIOException;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;

/**
 * @author jug
 *
 */
public class DoubleTypeImgLoader {

	public static Img< DoubleType > loadTiff( final File file )
			throws ImgIOException {
//	    ALERT: THOSE FOLLOWING TWO LINES CAUSE THREAD LEAK!!!!
//		final ImgFactory< DoubleType > imgFactory = new ArrayImgFactory< DoubleType >();
//		final ImgOpener imageOpener = new ImgOpener();

		System.out.print( "\n >> Loading file '" + file.getName() + "' ..." );
//		final List< SCIFIOImgPlus< FloatType >> imgs = imageOpener.openImgs( file.getAbsolutePath(), imgFactory, new DoubleType() );
//		final Img< RealType > img = imgs.get( 0 ).getImg();
		final Img< DoubleType > img =
				ImagePlusAdapter.wrapReal( IJ.openImage( file.getAbsolutePath() ) );

		return img;
	}

	public static RandomAccessibleInterval< DoubleType > loadTiffEnsureType( final File file )
			throws ImgIOException {
		final Img< DoubleType > img = loadTiff( file );

		final long dims[] = new long[ img.numDimensions() ];
		img.dimensions( dims );
		final RandomAccessibleInterval< DoubleType > ret =
				new ArrayImgFactory< DoubleType >().create( dims, new DoubleType() );
		final IterableInterval< DoubleType > iterRet = Views.iterable( ret );
		try {
			DataMover.convertAndCopy( Views.extendZero( img ), iterRet );
		} catch ( final Exception e ) {
			e.printStackTrace();
		}
		return ret;
	}

	public static RandomAccessibleInterval< DoubleType > wrapEnsureType( final ImagePlus imagePlus ) {
		final Img< DoubleType > img =
				ImagePlusAdapter.wrapReal( imagePlus );

		final long dims[] = new long[ img.numDimensions() ];
		img.dimensions( dims );
		final RandomAccessibleInterval< DoubleType > ret =
				new ArrayImgFactory< DoubleType >().create( dims, new DoubleType() );
		final IterableInterval< DoubleType > iterRet = Views.iterable( ret );
		try {
			DataMover.convertAndCopy( Views.extendZero( img ), iterRet );
		} catch ( final Exception e ) {
			e.printStackTrace();
		}
		return ret;
	}

}
