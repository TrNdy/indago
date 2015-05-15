package com.indago.fg.function;

import com.indago.fg.domain.BooleanDomain;
import com.indago.fg.domain.BooleanFunctionDomain;
import com.indago.fg.function.WeightedIndexSumConstraint.Relation;
import com.indago.fg.value.Value;


public class BooleanWeightedIndexSumConstraint implements BooleanFunction {

	protected final BooleanFunctionDomain domain;
	protected final int[] coefficients;
	protected final Relation relation;
	protected final int value;

	public BooleanWeightedIndexSumConstraint(
			final int[] coefficients,
			final Relation le,
			final int value ) {
		this.domain = new BooleanFunctionDomain( coefficients.length );
		this.coefficients = coefficients;
		this.relation = le;
		this.value = value;
	}

	@Override
	public BooleanFunctionDomain getDomain() {
		return domain;
	}

	/**
	 * @see com.indago.fg.function.Function#evaluate(com.indago.fg.value.Value[])
	 */
	@Override
	public double evaluate( final Value< Boolean, BooleanDomain >... values ) {
		assert values.length == coefficients.length;
		int sum = 0;
		for ( int d = 0; d < coefficients.length; ++d )
			sum += values[ d ].getAsIndex() * coefficients[ d ];
		switch ( relation ) {
		default:
		case EQ:
			return sum == value ? 0 : Double.POSITIVE_INFINITY;
		case GE:
			return sum >= value ? 0 : Double.POSITIVE_INFINITY;
		case LE:
			return sum <= value ? 0 : Double.POSITIVE_INFINITY;
		}
	}

}
