package com.indago.fg;

import java.util.List;

public class Factors {

	public static Factor unary( final Variable variable, final double... costs ) {
		assert variable.getDomain().getCardinality() == costs.length;
		return new Factor( Functions.unary( costs ), variable );
	}

	public static Factor atMostOneConstraint( final Variable... variables ) {
		return new Factor( Constraints.atMostOneConstraint( variables.length ), variables );
	}
	public static Factor atMostOneConstraint( final List< Variable > variables ) {
		return new Factor( Constraints.atMostOneConstraint( variables.size() ), variables );
	}
	// - - - - - - - - - - - - - - - - - - - - - - -
	public static Factor equalOneConstraint( final Variable... variables ) {
		return new Factor( Constraints.equalOneConstraint( variables.length ), variables );
	}
	public static Factor equalOneConstraint( final List< Variable > variables ) {
		return new Factor( Constraints.equalOneConstraint( variables.size() ), variables );
	}
	// - - - - - - - - - - - - - - - - - - - - - - -
	public static Factor equalZeroConstraint( final Variable... variables ) {
		return new Factor( Constraints.equalZeroConstraint( variables.length ), variables );
	}
	public static Factor equalZeroConstraint( final List< Variable > variables ) {
		return new Factor( Constraints.equalZeroConstraint( variables.size() ), variables );
	}
	// - - - - - - - - - - - - - - - - - - - - - - -
	public static Factor firstExactlyWithOneOtherOrNoneConstraint( final Variable... variables ) {
		return new Factor( Constraints
				.firstExactlyWithOneOtherOrNoneConstraint( variables.length ), variables );
	}
	public static Factor firstExactlyWithOneOtherOrNoneConstraint( final List< Variable > variables ) {
		return new Factor( Constraints
				.firstExactlyWithOneOtherOrNoneConstraint( variables.size() ), variables );
	}
	// - - - - - - - - - - - - - - - - - - - - - - -
	public static Factor firstImpliesAtLeastOneOtherConstraint( final Variable... variables ) {
		return new Factor( Constraints
				.firstImpliesAtLeastOneOtherConstraint( variables.length ), variables );
	}
	public static Factor firstImpliesAtLeastOneOtherConstraint( final List< Variable > variables ) {
		return new Factor( Constraints
				.firstImpliesAtLeastOneOtherConstraint( variables.size() ), variables );
	}
}
