package com.indago.fg.scalar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.indago.fg.scalar.SumConstraint.Relation;

public class LoadScalar {

	public static void main( final String[] args ) throws IOException {
//		final String fn = "src/main/resources/min-gap.txt";
		final String fn = "src/main/resources/sopnet-test-minimal.txt";

		final BufferedReader input = new BufferedReader( new FileReader( fn ) );

		ArrayList< String > parts;

		parts = nextLine( input );
		if ( parts == null || parts.size() != 3 )
			throw new IllegalArgumentException( "couldn't parse preamble" );

		final int numVariables = Integer.parseInt( parts.get( 0 ) );
		final int numFunctions = Integer.parseInt( parts.get( 1 ) );
		final int numFactors = Integer.parseInt( parts.get( 2 ) );

		final ArrayList< EnumeratedVariable > variables = new ArrayList< EnumeratedVariable >();
		for ( int i = 0; i < numVariables; ++i ) {
			variables.add( readVariable( input ) );
		}

		final ArrayList< EnumeratedFunction > functions = new ArrayList< EnumeratedFunction >();
		for ( int i = 0; i < numFunctions; ++i ) {
			functions.add( readFunction( input ) );
		}

		final ArrayList< EnumeratedFactor > factors = new ArrayList< EnumeratedFactor >();
		for ( int i = 0; i < numFactors; ++i ) {
			factors.add( readFactor( input, functions, variables ) );
		}

		System.out.println( "Variables:" );
		for ( final EnumeratedVariable v : variables )
			System.out.println( v );
		System.out.println();

		System.out.println( "Functions:" );
		for ( final EnumeratedFunction f : functions )
			System.out.println( f );
		System.out.println();

		System.out.println( "Factors:" );
		for ( final EnumeratedFactor f : factors )
			System.out.println( f );
	}

	static int variableId = 0;
	static int functionId = 0;
	static int factorId = 0;

	private static EnumeratedFactor readFactor( final BufferedReader input, final List< EnumeratedFunction > functions, final List< EnumeratedVariable > variables ) throws IOException {
		final ArrayList< String > parts = nextLine( input );
		if ( parts != null && parts.size() >= 1 ) {
			int i = 0;

			final int functionId = Integer.parseInt( parts.get( i++ ) );
			final EnumeratedFunction function = functions.get( functionId );

			final EnumeratedFactor factor = new EnumeratedFactor( function.getDomain(), factorId++ );
			factor.setFunction( function );

			final int numVariables = function.getDomain().numDimensions();
			if ( parts.size() >= 1 + numVariables ) {
				for ( int v = 0; v < numVariables; ++v ) {
					final int variableId = Integer.parseInt( parts.get( i++ ) );
					final EnumeratedVariable variable = variables.get( variableId );
					factor.setVariable( v, variable );
				}
				return factor;
			}
		}
		throw new IllegalArgumentException( "couldn't parse factor" );
	}

	private static EnumeratedFunction readFunction( final BufferedReader input ) throws IOException {
		final ArrayList< String > parts = nextLine( input );
		if ( parts != null ) {
			final String name = parts.get( 0 );
			if ( "table".equals( name ) ) {
				int i = 1;

				final int numDims = Integer.parseInt( parts.get( i++ ) );

				final int numStatesForDim[] = new int[ numDims ];
				int numEntries = 1;
				for ( int d = 0; d < numDims; ++d ) {
					numStatesForDim[ d ] = Integer.parseInt( parts.get( i++ ) );
					numEntries *= numStatesForDim[ d ];
				}

				final double entries[] = new double[ numEntries ];
				for ( int e = 0; e < numEntries; ++e ) {
					entries[ e ] = Double.parseDouble( parts.get( i++ ) );
				}

				return new EnumeratedTensorTable( numStatesForDim, entries, functionId++ );
			} else if ( "potts".equals( name ) ) {
				throw new UnsupportedOperationException( "not implemented" );
			} else if ( "constraint".equals( name ) ) {
				int i = 1;

				final int numDims = Integer.parseInt( parts.get( i++ ) );

				final int coefficients[] = new int[ numDims ];
				for ( int d = 0; d < numDims; ++d )
					coefficients[ d ] = Integer.parseInt( parts.get( i++ ) );

				final Relation relation = Relation.forSymbol( parts.get( i++ ) );
				final int value = Integer.parseInt( parts.get( i++ ) );

				return new EnumeratedSumConstraint( coefficients, relation, value, functionId++ );
			}
		}
		throw new IllegalArgumentException( "couldn't parse function" );
	}

	private static EnumeratedVariable readVariable( final BufferedReader input ) throws IOException {
		final ArrayList< String > parts = nextLine( input );
		if ( parts == null || parts.size() != 1 )
			throw new IllegalArgumentException( "couldn't parse variable" );
		final int numStates = Integer.parseInt( parts.get( 0 ) );
		return new EnumeratedVariable( numStates, variableId++ );
	}

	private static ArrayList< String > nextLine( final BufferedReader input ) throws IOException {
		final ArrayList< String > parts = new ArrayList< String >();
		while ( parts.isEmpty() ) {
			final String line = input.readLine();
			if ( line == null ) return null;
			for ( final String s : line.split( "\\s+" ) ) {
				if ( s.startsWith( "#" ) ) break;
				if ( !s.isEmpty() ) parts.add( s );
			}
		}
		return parts;
	}
}
