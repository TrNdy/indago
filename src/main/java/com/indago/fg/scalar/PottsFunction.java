/**
 *
 */
package com.indago.fg.scalar;

import com.indago.fg.LabelValue;

/**
 * @author jug
 */
public class PottsFunction implements EnumeratedFunction {

	protected double cost;

	public PottsFunction( final double cost ) {
		this.cost = cost;
	}

	/**
	 * @see com.indago.fg.Function#evaluate(com.indago.fg.Value[])
	 */
	@Override
	public double evaluate( final LabelValue< ?, EnumeratedDomain >... values ) {
		if ( values.length != 2 ) { throw new IllegalArgumentException( "PottsFunction can only be evaluated on two inputs" ); }

		if ( values[ 0 ].getAsIndex() == values[ 1 ].getAsIndex() ) {
			return 0;
		} else {
			return cost;
		}
	}

	/**
	 * @see com.indago.fg.scalar.EnumeratedFunction#getDomain()
	 */
	@Override
	public EnumeratedFunctionDomain getDomain() {
		return EnumeratedFunctionDomain.getArbitrary( 2 );
	}

}
