/**
 *
 */
package com.indago.util.converter;

import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;

/**
 * Converts any {@link RealType} to a {@link IntType} and divides by the
 * given number.
 *
 * If the input type is complex, it loses the imaginary part without complaining
 * further.
 *
 * @author Jug
 */
public class IntTypeThresholdConverter< R extends RealType< R > > implements Converter< R, IntType > {

	double threshold;

	public IntTypeThresholdConverter() {
		this( .5 );
	}

	public IntTypeThresholdConverter( final double threshold ) {
		this.threshold = threshold;
	}

	@Override
	public void convert( final R input, final IntType output ) {
		output.set( ( input.getRealDouble() > threshold ) ? 1 : 0 );
	}
}
