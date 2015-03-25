/**
 *
 */
package com.indago.fg.function;

import com.indago.fg.domain.BooleanDomain;
import com.indago.fg.domain.BooleanFunctionDomain;
import com.indago.fg.value.BooleanValue;

/**
 * @author jug
 */
public class BooleanConflictConstraint extends AtMostOneNotZeroFunction< Boolean, BooleanDomain > implements BooleanFunction {

	/**
	 * Instantiates a BooleanConflictConstraint, which is an
	 * AtMostOneNotZeroFunction with a FALSE BooleanValue as zero-element and
	 * POSITIVE_INFINITY as cost (since it is a constraint).
	 */
	public BooleanConflictConstraint() {
		super( new BooleanValue( Boolean.FALSE ), Double.POSITIVE_INFINITY );
	}

	@Override
	public BooleanFunctionDomain getDomain() {
		return null;
	}
}
