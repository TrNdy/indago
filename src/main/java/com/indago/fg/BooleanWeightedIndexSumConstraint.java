package com.indago.fg;

import java.util.ArrayList;
import java.util.List;

public class BooleanWeightedIndexSumConstraint implements Function {

	protected final Domain domain = Domains.getDefaultBinaryDomain();
	protected final double[] coefficients;
	private final Relation relation;
	private final double value;

	public BooleanWeightedIndexSumConstraint(
			final double[] coefficients,
			final Relation le,
			final double value ) {
		this.coefficients = coefficients;
		this.relation = le;
		this.value = value;
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

	/**
	 * @see com.indago.fg.Function#getArgumentDomains()
	 */
	@Override
	public List< Domain > getArgumentDomains() {
		final List< Domain > ret = new ArrayList<>();
		ret.add( domain );
		return ret;
	}

	/**
	 * @see com.indago.fg.Function#evaluate(int[])
	 */
	@Override
	public double evaluate( final int... arguments ) {
		assert arguments.length == getCoefficients().length;
		int sum = 0;
		for ( int d = 0; d < getCoefficients().length; ++d )
			sum += arguments[ d ] * coefficients[ d ];
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

}
