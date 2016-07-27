package com.indago.fg;

import static com.indago.fg.Relation.EQ;
// import static com.indago.fg.Relation.GE;
import static com.indago.fg.Relation.LE;

import java.util.ArrayList;
import java.util.Arrays;

// TODO: not currently thread-safe
public class Constraints {

	private static final ArrayList< LinearConstraint > atMostOneConstraints = new ArrayList< >();
	private static final ArrayList< LinearConstraint > equalOneConstraints = new ArrayList< >();
	private static final ArrayList< LinearConstraint > equalZeroConstraints = new ArrayList< >();
	private static final ArrayList< LinearConstraint > firstExactlyWithOneOtherOrNoneConstraints = new ArrayList< >();
	private static final ArrayList< LinearConstraint > firstImpliesAtLeastOneOtherConstraints = new ArrayList< >();

	public static LinearConstraint atMostOneConstraint( final int arity ) {
		while ( arity > atMostOneConstraints.size() - 1 ) {
			final double[] coefficients = new double[ atMostOneConstraints.size() ];
			Arrays.fill( coefficients, 1 );
			final LinearConstraint c = new LinearConstraint(
					coefficients,
					LE,
					1,
					Domains.getDefaultBinaryArgumentDomains( arity ) );
			atMostOneConstraints.add( c );
		}
		return atMostOneConstraints.get( arity );
	}

	public static LinearConstraint equalOneConstraint( final int arity ) {
		while ( arity > equalOneConstraints.size() - 1 ) {
			final double[] coefficients = new double[ equalOneConstraints.size() ];
			Arrays.fill( coefficients, 1 );
			final LinearConstraint c = new LinearConstraint(
					coefficients,
					EQ,
					1,
					Domains.getDefaultBinaryArgumentDomains( arity ) );
			equalOneConstraints.add( c );
		}
		return equalOneConstraints.get( arity );
	}

	public static LinearConstraint equalZeroConstraint( final int arity ) {
		while ( arity > equalZeroConstraints.size() - 1 ) {
			final double[] coefficients = new double[ equalZeroConstraints.size() ];
			Arrays.fill( coefficients, 1 );
			final LinearConstraint c = new LinearConstraint(
					coefficients,
					EQ,
					0,
					Domains.getDefaultBinaryArgumentDomains( arity ) );
			equalZeroConstraints.add( c );
		}
		return equalZeroConstraints.get( arity );
	}

	public static LinearConstraint firstExactlyWithOneOtherOrNoneConstraint( final int arity ) {
		while ( arity > firstExactlyWithOneOtherOrNoneConstraints.size() - 1 ) {
			final double[] coefficients = new double[ firstExactlyWithOneOtherOrNoneConstraints.size() ];
			Arrays.fill( coefficients, -1 );
			if ( coefficients.length > 0 ) {
				coefficients[ 0 ] = 1;
			}
			final LinearConstraint c = new LinearConstraint(
					coefficients,
					EQ,
					0,
					Domains.getDefaultBinaryArgumentDomains( arity ) );
			firstExactlyWithOneOtherOrNoneConstraints.add( c );
		}
		return firstExactlyWithOneOtherOrNoneConstraints.get( arity );
	}

	public static Function firstImpliesAtLeastOneOtherConstraint( final int arity ) {
		while ( arity > firstImpliesAtLeastOneOtherConstraints.size() - 1 ) {
			final double[] coefficients = new double[ firstImpliesAtLeastOneOtherConstraints.size() ];
			Arrays.fill( coefficients, -1 );
			if ( coefficients.length > 0 ) {
				coefficients[ 0 ] = 1;
			}
			final LinearConstraint c = new LinearConstraint(
					coefficients,
					LE,
					0,
					Domains.getDefaultBinaryArgumentDomains( arity ) );
			firstImpliesAtLeastOneOtherConstraints.add( c );
		}
		return firstImpliesAtLeastOneOtherConstraints.get( arity );
	}
}
