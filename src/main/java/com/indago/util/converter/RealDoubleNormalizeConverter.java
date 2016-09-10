/**
 *
 */
package com.indago.util.converter;

import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * Converts any {@link RealType} to a {@link DoubleType} and divides by the
 * given number.
 *
 * If the input type is complex, it loses the imaginary part without complaining
 * further.
 *
 * @author Jug
 */
public class RealDoubleNormalizeConverter< R extends RealType< R > > implements Converter< R, DoubleType > {

	double max;

	public RealDoubleNormalizeConverter() {
		this( 1.0 );
	}

	public RealDoubleNormalizeConverter( final double num ) {
		this.max = num;
	}

	@Override
	public void convert( final R input, final DoubleType output ) {
		output.set( input.getRealDouble() / max );
	}
}
