/**
 *
 */
package com.indago.fkt;


/**
 * @author jug
 */
public class Sigmoid1D implements Function1D< Double > {

	private static final long serialVersionUID = -1911291533933734723L;

	private final double s;
	private final double height;
	private final double dx;
	private final double dy;

	public Sigmoid1D( final double t ) {
		this( t, 1, 0, 0 );
	}

	public Sigmoid1D( final double s, final double height, final double dx, final double dy ) {
		this.s = s;
		this.height = height;
		this.dx = dx;
		this.dy = dy;
	}

	/**
	 * @see com.indago.fkt.Function1D#evaluate(double)
	 */
	@Override
	public Double evaluate( final double x ) {
		return new Double( getHeight() / ( 1.0 + Math.exp( -( x - getDx() ) * getS() ) ) + getDy() );
	}

	/**
	 * @return the s
	 */
	public double getS() {
		return s;
	}

	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * @return the dx
	 */
	public double getDx() {
		return dx;
	}

	/**
	 * @return the dy
	 */
	public double getDy() {
		return dy;
	}

}
