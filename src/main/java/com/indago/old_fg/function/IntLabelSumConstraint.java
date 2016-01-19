package com.indago.old_fg.function;

import com.indago.old_fg.domain.IntLabelDomain;
import com.indago.old_fg.domain.IntLabelFunctionDomain;
import com.indago.old_fg.value.Value;

public class IntLabelSumConstraint extends WeightedIndexSumConstraint< IntLabelDomain, IntLabelFunctionDomain, Value< Integer, IntLabelDomain > > implements IntLabelFunction {

	private final int id;

	public IntLabelSumConstraint( final int[] coefficients, final com.indago.old_fg.function.WeightedIndexSumConstraint.Relation relation, final int value, final int id ) {
		super( IntLabelFunctionDomain.getArbitrary( coefficients.length ), coefficients, relation, value );
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
