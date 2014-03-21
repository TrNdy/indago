package com.indago.fg.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.indago.fg.FactorGraph;
import com.indago.fg.factor.IntLabelFactor;
import com.indago.fg.function.IntLabelFunction;
import com.indago.fg.function.IntLabelPottsFunction;
import com.indago.fg.function.IntLabelSumConstraint;
import com.indago.fg.function.IntLabelTensorTable;
import com.indago.fg.function.WeightedIndexSumConstraint.Relation;
import com.indago.fg.variable.IntLabel;

public class Scalar {

	public static void main( final String[] args ) throws IOException {
//		final String fn = "src/main/resources/min-gap.txt";
//		final String fn = "src/main/resources/sopnet-test-minimal.txt";
		final String fn = "src/main/resources/chain.txt";

		load( fn );
	}

	/**
	 * @param fn
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static FactorGraph load( final String fn ) throws FileNotFoundException, IOException {
		final BufferedReader input = new BufferedReader( new FileReader( fn ) );

		ArrayList< String > parts;

		parts = nextLine( input );
		if ( parts == null || parts.size() != 3 )
			throw new IllegalArgumentException( "couldn't parse preamble" );

		final int numVariables = Integer.parseInt( parts.get( 0 ) );
		final int numFunctions = Integer.parseInt( parts.get( 1 ) );
		final int numFactors = Integer.parseInt( parts.get( 2 ) );

		final ArrayList< IntLabel > variables = new ArrayList< IntLabel >();
		for ( int i = 0; i < numVariables; ++i ) {
			variables.add( readVariable( input ) );
		}

		final ArrayList< IntLabelFunction > functions = new ArrayList< IntLabelFunction >();
		for ( int i = 0; i < numFunctions; ++i ) {
			functions.add( readFunction( input ) );
		}

		final ArrayList< IntLabelFactor > factors = new ArrayList< IntLabelFactor >();
		for ( int i = 0; i < numFactors; ++i ) {
			factors.add( readFactor( input, functions, variables ) );
		}

		System.out.println( "Variables:" );
		for ( final IntLabel v : variables )
			System.out.println( v );
		System.out.println();

		System.out.println( "Functions:" );
		for ( final IntLabelFunction f : functions )
			System.out.println( f );
		System.out.println();

		System.out.println( "Factors:" );
		for ( final IntLabelFactor f : factors )
			System.out.println( f );

		return new FactorGraph( variables, factors, functions );
	}

	static int variableId = 0;
	static int functionId = 0;
	static int factorId = 0;

	private static IntLabelFactor readFactor( final BufferedReader input, final List< IntLabelFunction > functions, final List< IntLabel > variables ) throws IOException {
		final ArrayList< String > parts = nextLine( input );
		if ( parts != null && parts.size() >= 1 ) {
			int i = 0;

			final int functionId = Integer.parseInt( parts.get( i++ ) );
			final IntLabelFunction function = functions.get( functionId );

			final IntLabelFactor factor = new IntLabelFactor( function.getDomain(), factorId++ );
			factor.setFunction( function );

			final int numVariables = function.getDomain().numDimensions();
			if ( parts.size() >= 1 + numVariables ) {
				for ( int v = 0; v < numVariables; ++v ) {
					final int variableId = Integer.parseInt( parts.get( i++ ) );
					final IntLabel variable = variables.get( variableId );
					factor.setVariable( v, variable );
				}
				return factor;
			}
		}
		throw new IllegalArgumentException( "couldn't parse factor" );
	}

	private static IntLabelFunction readFunction( final BufferedReader input ) throws IOException {
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

				return new IntLabelTensorTable( numStatesForDim, entries, functionId++ );
			} else if ( "potts".equals( name ) ) {
				int i = 1;

				final int numDims = Integer.parseInt( parts.get( i++ ) );
				if ( numDims != 2 ) { throw new IllegalArgumentException( "potts-function must be of dimensionality 2" ); }

				final double cost = Double.parseDouble( parts.get( i++ ) );

				return new IntLabelPottsFunction( cost );

			} else if ( "constraint".equals( name ) ) {
				int i = 1;

				final int numDims = Integer.parseInt( parts.get( i++ ) );

				final int coefficients[] = new int[ numDims ];
				for ( int d = 0; d < numDims; ++d )
					coefficients[ d ] = Integer.parseInt( parts.get( i++ ) );

				final Relation relation = Relation.forSymbol( parts.get( i++ ) );
				final int value = Integer.parseInt( parts.get( i++ ) );

				return new IntLabelSumConstraint( coefficients, relation, value, functionId++ );
			}
		}
		throw new IllegalArgumentException( "couldn't parse function" );
	}

	private static IntLabel readVariable( final BufferedReader input ) throws IOException {
		final ArrayList< String > parts = nextLine( input );
		if ( parts == null || parts.size() != 1 )
			throw new IllegalArgumentException( "couldn't parse variable" );
		final int numStates = Integer.parseInt( parts.get( 0 ) );
		return new IntLabel( numStates, variableId++ );
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
