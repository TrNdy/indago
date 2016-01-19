package com.indago.brandnewfg;

import java.util.List;

public class LinearConstraint implements Constraint {

	private final int[] coefficients;

	private final Relation relation;

	private final int rhs;

	private final List< Domain > argumentDomains;

	public LinearConstraint(
			final int[] coefficients,
			final Relation relation,
			final int rhs,
			final List< Domain > argumentDomains ) {
		this.coefficients = coefficients;
		this.relation = relation;
		this.rhs = rhs;
		this.argumentDomains = argumentDomains;
	}

	public int getArity() {
		return argumentDomains.size();
	}

	@Override
	public List< Domain > getArgumentDomains() {
		return argumentDomains;
	}

	public int[] getCoefficients() {
		return coefficients;
	}

	public Relation getRelation() {
		return relation;
	}

	public int getRhs() {
		return rhs;
	}

	@Override
	public double evaluate( final int... arguments ) {
		assert arguments.length == getArity();

		int sum = 0;
		for ( int i = 0; i < getArity(); ++i )
			sum += coefficients[ i ] * arguments[ i ];
		return Relation.holds( sum, relation, rhs ) ? 0 : Double.POSITIVE_INFINITY;
	}
}
