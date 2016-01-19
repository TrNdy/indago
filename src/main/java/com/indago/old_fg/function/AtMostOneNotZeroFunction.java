/**
 *
 */
package com.indago.old_fg.function;

import com.indago.old_fg.domain.Domain;
import com.indago.old_fg.domain.FunctionDomain;
import com.indago.old_fg.value.Value;

/**
 * AtMostOneNotZero returns 0 cost if at most one of the arguments is unequal to
 * <code>zeroValue</code>. Otherwise it returns the cost it is initialized with.
 * 
 * @author jug
 */
public abstract class AtMostOneNotZeroFunction< T, D extends Domain< T >> implements Function< D, Value< T, D > > {

	protected double cost;
	private final Value< T, D > zeroValue;

	public AtMostOneNotZeroFunction( final Value< T, D > zeroValue, final double cost ) {
		this.zeroValue = zeroValue;
		this.cost = cost;
	}

	/**
	 * @see com.indago.old_fg.function.Function#evaluate(com.indago.old_fg.value.Value[])
	 */
	@Override
	public double evaluate( final Value< T, D >... values ) {
		boolean foundOneAlready = false;

		for ( final Value< T, D > value : values ) {
			if ( value.getAsIndex() == zeroValue.getAsIndex() ) {
				if ( foundOneAlready ) { return cost; }
				foundOneAlready = true;
			}
		}
		return 0;
	}

	/**
	 * @see com.indago.old_fg.function.Function#getDomain()
	 */
	@Override
	public abstract FunctionDomain< D > getDomain();

}
