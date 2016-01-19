/**
 *
 */
package com.indago.old_fg.function;

import com.indago.old_fg.domain.Domain;
import com.indago.old_fg.domain.FunctionDomain;
import com.indago.old_fg.value.Value;

/**
 * NandFunction is a generalized boolean NAND.
 * It is a pairwise function that returns 0 cost if at most one of the two
 * arguments is equal to <code>zeroValue</code>. Otherwise it returns the
 * cost it is initialized with.
 * 
 * @author jug
 */
public abstract class NandFunction< T, D extends Domain< T >> implements Function< D, Value< T, D > > {

	protected double cost;
	private final Value< T, D > zeroValue;

	public NandFunction( final Value< T, D > zeroValue, final double cost ) {
		this.zeroValue = zeroValue;
		this.cost = cost;
	}

	/**
	 * @see com.indago.old_fg.function.Function#evaluate(com.indago.old_fg.value.Value[])
	 */
	@Override
	public double evaluate( final Value< T, D >... values ) {
		if ( values.length != 2 ) { throw new IllegalArgumentException( "NandFunction can only be evaluated on two inputs" ); }

		if ( values[ 0 ].getAsIndex() == zeroValue.getAsIndex() && values[ 1 ].getAsIndex() == zeroValue.getAsIndex() ) {
			return cost;
		} else {
			return 0;
		}
	}

	/**
	 * @see com.indago.old_fg.function.Function#getDomain()
	 */
	@Override
	public abstract FunctionDomain< D > getDomain();

}
