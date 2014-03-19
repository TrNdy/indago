/**
 *
 */
package com.indago.fg.function;

import com.indago.fg.domain.Domain;
import com.indago.fg.domain.FunctionDomain;
import com.indago.fg.value.Value;

/**
 * @author jug
 */
public abstract class PottsFunction< T, D extends Domain< T >> implements Function< D, Value< T, D > > {

	protected double cost;

	public PottsFunction( final double cost ) {
		this.cost = cost;
	}

	/**
	 * @see com.indago.fg.function.Function#evaluate(com.indago.fg.value.Value[])
	 */
	@Override
	public double evaluate( final Value< T, D >... values ) {
		if ( values.length != 2 ) { throw new IllegalArgumentException( "PottsFunction can only be evaluated on two inputs" ); }

		if ( values[ 0 ].getAsIndex() == values[ 1 ].getAsIndex() ) {
			return 0;
		} else {
			return cost;
		}
	}

	/**
	 * @see com.indago.fg.function.Function#getDomain()
	 */
	@Override
	public abstract FunctionDomain< D > getDomain();

}
