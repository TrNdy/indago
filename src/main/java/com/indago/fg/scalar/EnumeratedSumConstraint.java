package com.indago.fg.scalar;

import com.indago.fg.LabelValue;

public class EnumeratedSumConstraint extends SumConstraint< EnumeratedDomain, EnumeratedFunctionDomain, LabelValue< ?, EnumeratedDomain > > implements EnumeratedFunction {

	private final int id;

	public EnumeratedSumConstraint( final int[] coefficients, final com.indago.fg.scalar.SumConstraint.Relation relation, final int value, final int id ) {
		super( EnumeratedFunctionDomain.getArbitrary( coefficients.length ), coefficients, relation, value );
		this.id = id;
	}

	private static String coefficientToString( final int coefficient, final int i ) {
		if ( coefficient == 0 )
			return "0";
		else if ( coefficient == 1 )
			return "x" + i;
		else if ( coefficient == -1 )
			return "(-x" + i + ")";
		else
			return "(" + coefficient + " * x" + i + ")";
	}

	@Override
	public String toString() {
		String f = coefficientToString( coefficients[ 0 ], 0 );
		for ( int i = 1; i < coefficients.length; ++i )
			f += " + " + coefficientToString( coefficients[ i ], i );
		f += " " + relation + " " + value;
		return getClass().getSimpleName() + "(" + id + ") : " + domain + " -> {0,inf} : " + f;
	}
}
