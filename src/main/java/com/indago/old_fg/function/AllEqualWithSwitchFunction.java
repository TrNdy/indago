/**
 *
 */
package com.indago.old_fg.function;

import com.indago.old_fg.domain.Domain;
import com.indago.old_fg.domain.FunctionDomain;
import com.indago.old_fg.value.Value;

/**
 * This function checks if all incoming values are equal (cost==0).
 * If this is not the case the cost is given by parameter
 * <code>validationCost</code>.
 *
 * If the first value is equal to <code>switchValue</code>, the cost is 0
 * independent of the other variable values.
 *
 * Note: this function is e.g. useful for tracking assignments.
 *
 * @author jug
 */
public abstract class AllEqualWithSwitchFunction< T, D extends Domain< T >> implements Function< D, Value< T, D > > {

	protected double violationCost;
	private final Value< T, D > switchValue;

	public AllEqualWithSwitchFunction( final Value< T, D > switchValue, final double violationCost ) {
		this.switchValue = switchValue;
		this.violationCost = violationCost;
	}

	/**
	 * @see com.indago.old_fg.function.Function#evaluate(com.indago.old_fg.value.Value[])
	 */
	@Override
	public double evaluate( final Value< T, D >... values ) {
		final boolean first = true;
		Value< T, D > firstValue = null;

		for ( final Value< T, D > value : values ) {
			if ( first ) {
				if ( value.getAsIndex() == switchValue.getAsIndex() ) {
					return 0;
				} else {
					firstValue = value;
				}
			} else {
				if ( value.getAsIndex() != firstValue.getAsIndex() ) { return violationCost; }
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
