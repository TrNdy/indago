/**
 *
 */
package com.indago.segment.fg;

import com.indago.fg.variable.BooleanVariable;

/**
 * @author jug, tpietzsch
 */
public class BooleanVariablePlus< T > extends BooleanVariable {

	private final T interpretation;

	/**
	 * @param interpretation
	 *            the property represented by this {@link BooleanVariable}.
	 */
	public BooleanVariablePlus( final T interpretation ) {
		this.interpretation = interpretation;
	}

	public T getInterpretation() {
		return this.interpretation;
	}
}
