/**
 *
 */
package com.indago.fkt;


/**
 * @author jug
 */
public class Constant1D implements Function1D< Double > {

	private static final long serialVersionUID = 6380867594312182550L;

	private final double value;

	public Constant1D( final double value ) {
		this.value = value;
	}

	/**
	 * @see com.indago.fkt.Function1D#evaluate(double)
	 */
	@Override
	public Double evaluate( final double x ) {
		return new Double( getValue() );
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

}
