package com.indago.fg.function;

import com.indago.fg.domain.BooleanDomain;
import com.indago.fg.domain.BooleanFunctionDomain;
import com.indago.fg.function.WeightedIndexSumConstraint.Relation;
import com.indago.fg.value.Value;


public class BooleanWeightedIndexSumConstraint implements BooleanFunction {

	protected final BooleanFunctionDomain domain;
	protected final double[] coefficients;
	private final Relation relation;
	private final double value;

	public BooleanWeightedIndexSumConstraint(
			final double[] coefficients,
			final Relation le,
			final double value ) {
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
		assert values.length == getCoefficients().length;
		int sum = 0;
		for ( int d = 0; d < getCoefficients().length; ++d )
			sum += values[ d ].getAsIndex() * getCoefficients()[ d ];
		switch ( getRelation() ) {
		default:
		case EQ:
			return sum == getRHS() ? 0 : Double.POSITIVE_INFINITY;
		case GE:
			return sum >= getRHS() ? 0 : Double.POSITIVE_INFINITY;
		case LE:
			return sum <= getRHS() ? 0 : Double.POSITIVE_INFINITY;
		}
	}

	/**
	 * @return the coefficients
	 */
	public double[] getCoefficients() {
		return coefficients;
	}

	/**
	 * @return the coefficient with index i
	 */
	public double getCoefficient( final int i ) {
		return coefficients[ i ];
	}

	/**
	 * @return the relation
	 */
	public Relation getRelation() {
		return relation;
	}

	/**
	 * @return the value
	 */
	public double getRHS() {
		return value;
	}

}
