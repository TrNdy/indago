/**
 *
 */
package com.indago.fg.function;

import java.util.HashMap;

import com.indago.fg.domain.BooleanDomain;
import com.indago.fg.domain.BooleanFunctionDomain;
import com.indago.fg.value.BooleanValue;

/**
 * @author jug
 */
public class BooleanConflictConstraint extends AtMostOneNotZeroFunction< Boolean, BooleanDomain > implements BooleanFunction {

	private final int numDimensions;

	/**
	 * Instantiates a BooleanConflictConstraint, which is an
	 * AtMostOneNotZeroFunction with a FALSE BooleanValue as zero-element and
	 * POSITIVE_INFINITY as cost (since it is a constraint).
	 */
	protected BooleanConflictConstraint( final int numDimensions ) {
		super( new BooleanValue( Boolean.FALSE ), Double.POSITIVE_INFINITY );
		this.numDimensions = numDimensions;
	}

	@Override
	public BooleanFunctionDomain getDomain() {
		return BooleanFunctionDomain.getForNumDimensions( numDimensions );
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( obj instanceof BooleanConflictConstraint )
			return ( ( BooleanConflictConstraint ) obj ).numDimensions == numDimensions;
		else
			return false;
	}

	@Override
	public int hashCode() {
		return getDomain().hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " (" + numDimensions + ")";
	}

	protected static final HashMap< Integer, BooleanConflictConstraint > booleanConflicConstraints = new HashMap< Integer, BooleanConflictConstraint >();

	public static BooleanConflictConstraint getForNumDimensions( final int numDimensions ) {
		BooleanConflictConstraint cached = booleanConflicConstraints.get( numDimensions );
		if ( cached == null ) {
			cached = new BooleanConflictConstraint( numDimensions );
			booleanConflicConstraints.put( numDimensions, cached );
		}
		return cached;
	}

}
