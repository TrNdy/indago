package com.indago.brandnewfg;

import java.util.List;

public class Factors {

	public Factor unary( final Variable variable, final double... costs ) {
		assert variable.getDomain().getCardinality() == costs.length;
		return new Factor( Functions.unary( costs ), variable );
	}

	public Factor atMostOneConstraint( final Variable... variables )
	{
		return new Factor( Constraints.atMostOneConstraint( variables.length ), variables );
	}

	public Factor atMostOneConstraint( final List< Variable > variables )
	{
		return new Factor( Constraints.atMostOneConstraint( variables.size() ), variables );
	}
}
