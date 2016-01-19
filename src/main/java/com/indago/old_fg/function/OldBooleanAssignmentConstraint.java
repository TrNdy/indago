/**
 *
 */
package com.indago.old_fg.function;

import org.apache.commons.lang.NotImplementedException;

import com.indago.old_fg.domain.BooleanDomain;
import com.indago.old_fg.domain.BooleanFunctionDomain;
import com.indago.old_fg.value.BooleanValue;

/**
 * @author jug
 */
public class OldBooleanAssignmentConstraint extends AllEqualWithSwitchFunction< Boolean, BooleanDomain >
		implements
		BooleanFunction {

	/**
	 * Instantiates a BooleanConflictConstraint, which is an
	 * AtMostOneNotZeroFunction with a FALSE BooleanValue as zero-element and
	 * POSITIVE_INFINITY as cost (since it is a constraint).
	 */
	public OldBooleanAssignmentConstraint() {
		super( new BooleanValue( Boolean.FALSE ), Double.POSITIVE_INFINITY );
	}

	/**
	 * @see com.indago.old_fg.function.AllEqualWithSwitchFunction#getDomain()
	 */
	@Override
	public BooleanFunctionDomain getDomain() {
		throw new NotImplementedException( "BooleanAssignmentConstraint::getDomain() not yet implemented!" );
	}
}
