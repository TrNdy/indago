/**
 *
 */
package com.indago.io;

import java.util.List;

import io.scif.img.ImgIOException;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * @author jug
 *
 */
public class DataMover {

	/**
	 * FROM: imglib Example 2c :)
	 * Copy from a source that is just
	 * RandomAccessible to an IterableInterval. Latter one defines size and
	 * location of the copy operation. It will query the same pixel locations of
	 * the IterableInterval in the RandomAccessible. It is up to the developer
	 * to ensure that these coordinates match.
	 *
	 * Note that both, input and output could be Views, Img or anything that
	 * implements those interfaces.
	 *
	 * @param source
	 *            - a RandomAccess as source that can be infinite
	 * @param target
	 *            - an IterableInterval as target
	 */
	public static < T extends Type< T >> void copy( final RandomAccessible< T > source, final IterableInterval< T > target ) {
		// create a cursor that automatically localizes itself on every move
		final Cursor< T > targetCursor = target.localizingCursor();
		final RandomAccess< T > sourceRandomAccess = source.randomAccess();

		// iterate over the input cursor
		while ( targetCursor.hasNext() ) {
			// move input cursor forward
			targetCursor.fwd();

			// set the output cursor to the position of the input cursor
			sourceRandomAccess.setPosition( targetCursor );

			// set the value of this pixel of the output image, every Type
			// supports T.set( T type )
			targetCursor.get().set( sourceRandomAccess.get() );
		}
	}

	public static < T1 extends Type< T1 >, T2 extends Type< T2 >> void copy( final RandomAccessible< T1 > source, final IterableInterval< T2 > target, final Converter< T1, T2 > converter ) {
		// create a cursor that automatically localizes itself on every move
		final Cursor< T2 > targetCursor = target.localizingCursor();
		final RandomAccess< T1 > sourceRandomAccess = source.randomAccess();

		// iterate over the input cursor
		while ( targetCursor.hasNext() ) {
			// move input cursor forward
			targetCursor.fwd();

			// set the output cursor to the position of the input cursor
			sourceRandomAccess.setPosition( targetCursor );

			// set converted value
			converter.convert( sourceRandomAccess.get(), targetCursor.get() );
		}
	}

	public static < T extends Type< T >> void copy( final RandomAccessible< T > source, final RandomAccessibleInterval< T > target ) {
		copy( source, Views.iterable( target ) );
	}

	public static < T extends NativeType< T >> Img< T > createEmptyArrayImgLike( final RandomAccessibleInterval< ? > blueprint, final T type ) {
		final long[] dims = new long[ blueprint.numDimensions() ];
		for ( int i = 0; i < blueprint.numDimensions(); i++ ) {
			dims[ i ] = blueprint.dimension( i );
		}
		final Img< T > ret = new ArrayImgFactory< T >().create( dims, type );
		return ret;
	}

	/**
	 * Add from a source that is just
	 * RandomAccessible to an IterableInterval.
	 *
	 * Note that both, input and output could be Views, Img or anything that
	 * implements those interfaces.
	 *
	 * @param source
	 *            - a RandomAccess as source that can be infinite
	 * @param target
	 *            - an IterableInterval as target
	 */
	public static < T extends RealType< T > > void add(
			final RandomAccessible< T > source,
			final IterableInterval< T > target ) {
		// create a cursor that automatically localizes itself on every move
		final Cursor< T > targetCursor = target.localizingCursor();
		final RandomAccess< T > sourceRandomAccess = source.randomAccess();

		// iterate over the input cursor
		while ( targetCursor.hasNext() ) {
			// move input cursor forward
			targetCursor.fwd();

			// set the output cursor to the position of the input cursor
			sourceRandomAccess.setPosition( targetCursor );

			// add the value of this pixel of the output image, every Type
			// supports T.set( T type )
			targetCursor.get().add( sourceRandomAccess.get() );
		}
	}

	public static < T extends RealType< T > > void add(
			final RandomAccessible< T > source,
			final RandomAccessibleInterval< T > target ) {
		add( source, Views.iterable( target ) );
	}

	/**
	 * Util function that copies one image into another.
	 * I cases where the native pixel types match, <code>convertAndCopy</code>
	 * will simply call <code>copy</code>.
	 *
	 * <b>General assumptions and rules:</b>
	 * User takes care that
	 * <code>source</code> is defines on all interval locations of
	 * <code>target</code>. (The source <i>exists</i> on all places the target
	 * has to be filled.)
	 *
	 * <b>Supported conversions are:</b>
	 * From <code>FloatType</code> to <code>ARGBType</code>; source range is
	 * expected to be subset or
	 * equal to <code>[0,1]</code>. If values smaller 0 or larger than 1 are
	 * found the source
	 * image will be scanned for maximal and minimal values and the converted
	 * image will be normalized, filling the entire target range
	 * <code>[0,255]</code> in all 3 color channels.
	 *
	 * From <code>ARGBType</code> to <code>FloatType</code>; conversion
	 * ignores A, and computed double value like this:
	 * <code>v = ( 0.2989R + 0.5870G + 0.1140B ) / 255</code>, which leads to a
	 * target <code>Img</code> that lies within <code>[0,1]</code>.
	 *
	 * @param source
	 * @param target
	 * @throws Exception
	 */

	@SuppressWarnings( "unchecked" )
	public static < ST extends RealType< ST >, TT extends NativeType< TT > > void convertAndCopy(
			final RandomAccessible< ST > source,
			final IterableInterval< TT > target ) {
		final ST sourceType = source.randomAccess().get();
		final TT targetType = target.firstElement();

		// if source and target are of same type -> use copy since convert is not needed...
		if ( sourceType.getClass().isInstance( targetType ) ) {
			DataMover.copy( source, ( IterableInterval ) target );
			return;
		}

		// implemented conversion cases follow here...

		if ( targetType instanceof DoubleType ) {
			// RealType --> DoubleType
			DataMover.copy( source, target, (in, out) -> ((DoubleType) out).set( in.getRealDouble() ) );
			return;
		}

		if ( targetType instanceof FloatType ) {
			// RealType -> FloatType
			DataMover.copy( source, target, (in, out) -> ((FloatType) out).set( in.getRealFloat() ) );
			return;
		}

		if ( targetType instanceof IntType ) {
			if ( sourceType instanceof IntegerType ) {
				DataMover.copy( source, target, (in, out) -> ((IntType) out).set( ((IntegerType) in).getInteger()));
			}
			else {
				DataMover.copy( source, target, (in, out) -> ((IntType) out).set( Math.round(in.getRealFloat())));
			}
			return;
		}

		if ( sourceType instanceof FloatType && targetType instanceof ARGBType ) {
			DataMover.copy( source, target, (in, out) -> {
				int v;
				try {
					v = Math.round(((FloatType) in).get() * 255);
				}
				catch (final ArrayIndexOutOfBoundsException e) {
					v = 255; // If image-sizes do not match we pad with white pixels...
				}
				if (v > 255) {
					v = 255;
				} else if (v < 0) {
					v = 0;
				}
				((ARGBType) out).set(ARGBType.rgba(v, v, v, 255));
			});
			return;
		}

		throw new RuntimeException( "Convertion from " + sourceType.getClass().toString() + " to " + targetType.getClass() + " not implemented!" );
	}

	public static < T extends RealType< T > & NativeType< T > > Img< T > stackThemAsFrames( final List< Img< T > > imageList )
			throws ImgIOException, IncompatibleTypeException, Exception {

		Img< T > stack = null;

		final long width = imageList.get( 0 ).dimension( 0 );
		final long height = imageList.get( 0 ).dimension( 1 );
		final long channels = imageList.get( 0 ).dimension( 2 );
		final long frames = imageList.size();

		stack = new ArrayImgFactory< T >().create( new long[] { width, height, channels, frames }, imageList.get( 0 ).firstElement() );

		// Add images to stack...
		int i = 0;
		for ( final RandomAccessible< T > image : imageList ) {
			final RandomAccessibleInterval< T > viewZSlize = Views.hyperSlice( stack, 3, i );

			for ( int c = 0; c < channels; c++ ) {
				final RandomAccessibleInterval< T > viewChannel = Views.hyperSlice( viewZSlize, 2, c );
				final IterableInterval< T > iterChannel = Views.iterable( viewChannel );

				if ( image.numDimensions() < 3 ) {
					if ( c > 0 ) { throw new ImgIOException( "Not all images to be loaded contain the same number of color channels!" ); }
					DataMover.copy( image, iterChannel );
				} else {
					DataMover.copy( Views.hyperSlice( image, 2, c ), iterChannel );
				}
			}
			i++;
		}

		return stack;
	}
}
