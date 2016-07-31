/**
 *
 */
package com.indago.io;

import java.io.File;

import ij.IJ;
import io.scif.img.ImgIOException;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;

/**
 * @author jug
 *
 */
public class IntTypeImgLoader {

	public static Img< IntType > loadTiff( final File file )
			throws ImgIOException {
//	    ALERT: THOSE FOLLOWING TWO LINES CAUSE THREAD LEAK!!!!
//		final ImgFactory< IntType > imgFactory = new ArrayImgFactory< IntType >();
//		final ImgOpener imageOpener = new ImgOpener();

		System.out.print( "\n >> Loading file '" + file.getName() + "' ..." );
//		final List< SCIFIOImgPlus< IntType >> imgs = imageOpener.openImgs( file.getAbsolutePath(), imgFactory, new IntType() );
//		final Img< IntType > img = imgs.get( 0 ).getImg();
		final Img< IntType > img =
				ImagePlusAdapter.wrapReal( IJ.openImage( file.getAbsolutePath() ) );

		return img;
	}

	public static RandomAccessibleInterval< IntType > loadTiffEnsureType( final File file )
			throws ImgIOException {
		final Img< IntType > img = loadTiff( file );

		final long dims[] = new long[ img.numDimensions() ];
		img.dimensions( dims );
		final RandomAccessibleInterval< IntType > ret =
				new ArrayImgFactory< IntType >().create( dims, new IntType() );
		final IterableInterval< IntType > iterRet = Views.iterable( ret );
		try {
			DataMover.convertAndCopy( Views.extendZero( img ), iterRet );
		} catch ( final Exception e ) {
			e.printStackTrace();
		}
		return ret;
	}

}
