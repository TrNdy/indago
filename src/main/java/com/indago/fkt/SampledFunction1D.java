/**
 *
 */
package com.indago.fkt;

import java.util.List;

/**
 * @author jug
 */
public class SampledFunction1D implements Function1D< Double > {

	private static final long serialVersionUID = 3831229532343640677L;

	private final List< Double > x;
	private final List< Double > y;

	public SampledFunction1D( final List< Double > x, final List< Double > y ) {
		assert x.size() == y.size();
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns a linear interpolation between the two function samples enclosing
	 * the given value x.
	 * 
	 * @return If this function contains zero x/y-samples we return zero,
	 *         otherwise a linear interpolation between the two closest
	 *         (enclosing) data-points. If the given parameter x is at the left
	 *         of all x/y-samples this function does also return 0.
	 * @see com.indago.fkt.Function1D#evaluate(double)
	 */
	@Override
	public Double evaluate( final double x ) {
		if ( this.x.size() == 0 ) return new Double( 0 );

		int i;
		for ( i = 0; i < this.x.size(); i++ ) {
			if ( this.x.get( i ) > x ) break;
		}

		if ( i == 0 || i == this.x.size() ) {
			return new Double( 0 );
		} else {
			final double left = this.x.get( i - 1 );
			final double right = this.x.get( i );
			final double ratio = ( x - left ) / ( right - left );

			return new Double( ( 1 - ratio ) * this.y.get( i - 1 ) + ratio * this.y.get( i ) );
		}
	}

	/**
	 * @return a list of x-axis coordinates
	 */
	public List< Double > getXVals() {
		return x;
	}

	/**
	 * @return a list of y-axis coordinates
	 */
	public List< Double > getYVals() {
		return y;
	}

	/**
	 * Normalizes this function to that the sum of all give y-values sums to 1.
	 */
	public void normalizeToDiscreteDistribution() {
		double sum = 0.0;
		for ( final double d : y ) {
			sum += d;
		}
		if ( sum != 0.0 ) mult( 1.0 / sum );
	}

	/**
	 * Normalizes this function so that the maximal value is going to be 1.
	 */
	public void normalizeMax() {
		final double max = getMax();
		if ( max != 0.0 ) mult( 1.0 / max );
	}

	/**
	 * Adds a given constant to all sample locations.
	 * 
	 * @param c
	 *            The famous constant.
	 */
	public void add( final double c ) {
		for ( int i = 0; i < y.size(); i++ ) {
			y.set( i, y.get( i ) + c );
		}
	}

	/**
	 * Adds fkt to <code>this</code>.
	 * Note: <code>fkt</code> will be evaluated at all sample location of
	 * <code>this</code> and those values will be directly added to current
	 * samples. This means also that f1.add(f2) != f2.add(f1).
	 * 
	 * @param fkt
	 *            The function to add
	 */
	public void add( final Function1D< Double > fkt ) {
		for ( int i = 0; i < y.size(); i++ ) {
			y.set( i, y.get( i ) + fkt.evaluate( x.get( i ) ) );
		}
	}

	/**
	 * Multiplies a given constant to all sample locations.
	 * 
	 * @param c
	 *            The famous constant.
	 */
	public void mult( final double c ) {
		for ( int i = 0; i < y.size(); i++ ) {
			y.set( i, y.get( i ) * c );
		}
	}

	/**
	 * Multiplies <code>this</code> element (sample) wise by fkt.
	 * Note: <code>fkt</code> will be evaluated at all sample location of
	 * <code>this</code> and those values will be use to multiply.
	 * 
	 * @param fkt
	 *            The function to use for element-wise division.
	 */
	public void mult( final Function1D< Double > fkt ) {
		for ( int i = 0; i < y.size(); i++ ) {
			y.set( i, y.get( i ) * fkt.evaluate( x.get( i ) ) );
		}
	}

	/**
	 * Divides <code>this</code> element (sample) wise by fkt.
	 * Note: <code>fkt</code> will be evaluated at all sample location of
	 * <code>this</code> and those values will be use to divide.
	 * 
	 * @param fkt
	 *            The function to use for element-wise division.
	 */
	public void div( final Function1D< Double > fkt ) {
		for ( int i = 0; i < y.size(); i++ ) {
			double val = fkt.evaluate( x.get( i ) );
			if ( Math.abs( val ) == 0.0 ) val = 1.0;
			y.set( i, y.get( i ) / val );
		}
	}

	/**
	 * @return An array of double-values.
	 */
	public double[] getXValsArray() {
		final double[] ret = new double[ x.size() ];
		for ( int i = 0; i < x.size(); i++ ) {
			ret[ i ] = x.get( i );
		}
		return ret;
	}

	/**
	 * @return An array of double-values.
	 */
	public double[] getYValsArray() {
		final double[] ret = new double[ y.size() ];
		for ( int i = 0; i < y.size(); i++ ) {
			ret[ i ] = y.get( i );
		}
		return ret;
	}

	/**
	 * @return The smallest sample value.
	 */
	public double getMin() {
		double min = Double.MAX_VALUE;
		for ( final double d : y ) {
			if ( d < min ) min = d;
		}
		return min;
	}

	/**
	 * @return The largest sample value.
	 */
	public double getMax() {
		double max = Double.MIN_VALUE;
		for ( final double d : y ) {
			if ( d > max ) max = d;
		}
		return max;
	}

	/**
	 * Convolve with a given kernel. The kernel will be applied to plus/minus
	 * <code>indexRadiusRange</code> many sample-values for the convolution.
	 * 
	 * @param kernel
	 *            A <code>Function1D</code> defining the convolution kernel.
	 * @param indexRadiusRange
	 */
	public void convolve( final Function1D kernel, final int indexRadiusRange ) {

	}
}
