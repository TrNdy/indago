/**
 *
 */
package com.indago.fkt;


/**
 * @author jug
 */
public class Gaussian1D implements Function1D< Double > {

	private static final long serialVersionUID = -4122559658436594819L;

	private final double height;
	private final double mean;
	private final double v2;

	public Gaussian1D( final double mean, final double sd ) {
		this.height = 1.0 / ( sd * Math.sqrt( 2 * Math.PI ) );
		this.mean = mean;
		this.v2 = sd * sd;
	}

	public Gaussian1D( final double height, final double mean, final double sd ) {
		this.height = height;
		this.mean = mean;
		this.v2 = sd * sd;
	}

	/**
	 * @see com.indago.fkt.Function1D#evaluate(double)
	 */
	@Override
	public Double evaluate( final double x ) {
		final double d = x - getMean();
		return new Double( getHeight() * Math.exp( -( d * d ) / ( 2.0 * v2 ) ) );
	}

	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * @return the mean
	 */
	public double getMean() {
		return mean;
	}

	/**
	 * @return the v2
	 */
	public double getSd() {
		return Math.sqrt( v2 );
	}

}
