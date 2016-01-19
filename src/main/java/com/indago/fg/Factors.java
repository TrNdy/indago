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
}
