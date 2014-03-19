package com.indago.fg.scalar;

import com.indago.fg.FunctionDomain;
import com.indago.fg.LabelDomain;
import com.indago.fg.LabelValue;
import com.indago.fg.Tensor;

public class SumConstraint< D extends LabelDomain< ? >, FD extends FunctionDomain< D >, V extends LabelValue< ?, D > > implements Tensor< D, V > {

	public static enum Relation {
		EQ( "==" ), GE( ">=" ), LE( "<=" );

		private final String symbol;

		private Relation( final String symbol ) {
			this.symbol = symbol;
		}

		@Override
		public String toString() {
			return symbol;
		}

		public static Relation forSymbol( final String symbol ) {
			for ( final Relation r : Relation.values() )
				if ( r.symbol.equals( symbol ) ) return r;
			throw new IllegalArgumentException();
		}
	}

	protected final FD domain;
	protected final int[] coefficients;
	protected final Relation relation;
	protected final int value;

	public SumConstraint( final FD domain, final int[] coefficients, final Relation relation, final int value ) {
		this.domain = domain;
		this.coefficients = coefficients;
		this.relation = relation;
		this.value = value;
	}

	@Override
	public FD getDomain() {
		return domain;
	}

	@Override
	public double evaluate( final V... values ) {
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
