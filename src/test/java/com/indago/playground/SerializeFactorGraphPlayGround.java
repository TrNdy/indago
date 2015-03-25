package com.indago.playground;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.indago.fg.Assignment;
import com.indago.fg.FactorGraph;
import com.indago.fg.domain.Domain;
import com.indago.fg.domain.FunctionDomain;
import com.indago.fg.factor.BooleanFactor;
import com.indago.fg.factor.Factor;
import com.indago.fg.factor.IntLabelFactor;
import com.indago.fg.function.BooleanConflictConstraint;
import com.indago.fg.function.BooleanFunction;
import com.indago.fg.function.BooleanTensorTable;
import com.indago.fg.function.Function;
import com.indago.fg.function.IntLabelFunction;
import com.indago.fg.function.IntLabelPottsFunction;
import com.indago.fg.function.IntLabelSumConstraint;
import com.indago.fg.function.IntLabelTensorTable;
import com.indago.fg.function.TensorTable;
import com.indago.fg.function.WeightedIndexSumConstraint.Relation;
import com.indago.fg.variable.BooleanVariable;
import com.indago.fg.variable.IntLabel;
import com.indago.fg.variable.Variable;
import com.indago.ilp.SolveBooleanFGGurobi;
import com.indago.segment.HypothesisPrinter;
import com.indago.segment.LabelingForest;
import com.indago.segment.LabelingPlus;
import com.indago.segment.LabelingSegment;
import com.indago.segment.MinimalOverlapConflictGraph;
import com.indago.segment.RandomSegmentCosts;
import com.indago.segment.XmlIoLabelingPlus;
import com.indago.segment.fg.BooleanVariablePlus;
import com.indago.segment.fg.FactorGraphFactory;

public class SerializeFactorGraphPlayGround {

	public static void main( final String[] args ) throws Exception {
		final String labelingDataFilename = "/Users/pietzsch/Desktop/labeling.xml";
		final String fgDataFilename = "/Users/pietzsch/Desktop/factorgraph.txt";

		final LabelingPlus labelingPlus = new XmlIoLabelingPlus().load( labelingDataFilename );
		final List< LabelingForest > labelingForestsLoaded = labelingPlus.getLabelingForests();

		final MinimalOverlapConflictGraph conflictGraph = new MinimalOverlapConflictGraph( labelingPlus );
		conflictGraph.getConflictGraphCliques();

		final HypothesisPrinter hp = new HypothesisPrinter();
		for ( final LabelingForest labelingForest : labelingForestsLoaded ) {
			hp.assignIds( labelingForest );
			hp.printHypothesisForest( labelingForest );
		}
		System.out.println();
		hp.printConflictGraphCliques( conflictGraph );

		final ArrayList< LabelingSegment > segments = labelingPlus.getSegments();
		final RandomSegmentCosts costs = new RandomSegmentCosts( segments, 815 ); // assign random costs to segments in MultiForest (for testing purposes)
		final FactorGraph fg = FactorGraphFactory.createFromConflictGraph( segments, conflictGraph, costs ).getFactorGraph();

		final VariableSerializer variableSerializer = new VariableSerializer() {

			private final HashMap< Integer, LabelingSegment > variableIndexToSegment = new HashMap< Integer, LabelingSegment >();

			@Override
			public BooleanVariable loadVariable( final int variableIndex, final int numStates ) {
				final LabelingSegment segment = variableIndexToSegment.get( variableIndex );
				if ( segment != null )
					return new BooleanVariablePlus< LabelingSegment >( segment );
				else
					return null;
			}

			@Override
			public boolean rememberVariable( final Variable< ? > var, final int variableIndex ) {
				if ( var instanceof BooleanVariablePlus ) {
					final BooleanVariablePlus< ? > b = ( BooleanVariablePlus< ? > ) var;
					if ( b.getInterpretation() instanceof LabelingSegment ) {
						final LabelingSegment segment = ( LabelingSegment ) b.getInterpretation();
						variableIndexToSegment.put( variableIndex, segment );
						return true;
					}
				}
				return false;
			}
		};

		XmlIoFactorGraph.save( fg, fgDataFilename, Arrays.asList( variableSerializer ) );
		final FactorGraph fgLoaded = XmlIoFactorGraph.load( fgDataFilename, Arrays.asList( variableSerializer ) );

		final SolveBooleanFGGurobi solver = new SolveBooleanFGGurobi();
		final Assignment assignment = solver.solve( fgLoaded );
		System.out.println( assignment );
	}

	public static interface VariableSerializer {

		public boolean rememberVariable( Variable< ? > var, int variableIndex );

		public Variable< ? > loadVariable( int variableIndex, int numStates );
	}

	public static class XmlIoFactorGraph {

		public static void save( final FactorGraph fg, final String fn, final List< VariableSerializer > variableSerializers ) throws IOException {
			final BufferedWriter output = new BufferedWriter( new FileWriter( fn ) );

			output.append( "# indago boolean" );
			output.newLine();

			// preamble
			final List< ? extends Variable< ? > > variables = fg.getVariables();
			final List< ? extends Function< ?, ? > > functions = fg.getFunctions();
			final List< ? extends Factor< ?, ?, ? > > factors = fg.getFactors();
			final int numVariables = variables.size();
			final int numFunctions = functions.size();
			final int numFactors = factors.size();
			output.write( String.format( "%d %d %d", numVariables, numFunctions, numFactors ) );
			output.newLine();

			// variables
			final TObjectIntHashMap< Variable< ? > > varToIndexMap = new TObjectIntHashMap< Variable< ? > >( numVariables );
			int i = 0;
			for ( final Variable< ? > var : variables ) {
				for ( final VariableSerializer serializer : variableSerializers )
					if ( serializer.rememberVariable( var, i ) ) break;
				final int numStates = var.getDomain().size();
				output.write( String.format( "%d", numStates ) );
				output.newLine();
				varToIndexMap.put( var, i );
				++i;
			}

			// functions
			final TObjectIntHashMap< Function< ?, ? > > functionToIndexMap = new TObjectIntHashMap< Function< ?, ? > >( numFunctions );
			i = 0;
			for ( final Function< ?, ? > func : functions ) {
				if ( func instanceof BooleanConflictConstraint ) {
					final BooleanConflictConstraint constraint = ( BooleanConflictConstraint ) func;
					final StringBuilder sb = new StringBuilder();
					sb.append( "constraint " );
					final int numDims = constraint.getDomain().numDimensions();
					sb.append( numDims );
					for ( int d = 0; d < numDims; ++d )
						sb.append( " 1" );
					sb.append( " <= 1" );
					output.append( sb );
					output.newLine();
				} else if ( func instanceof TensorTable ) {
					final TensorTable< ?, ?, ? > table = ( TensorTable< ?, ?, ? > ) func;
					final FunctionDomain< ? > fdom = table.getDomain();
					final double[] entries = table.getEntries();
					final StringBuilder sb = new StringBuilder();
					sb.append( "table " );
					sb.append( fdom.numDimensions() );
					sb.append( " " );
					for ( final Domain< ? > argdom : fdom.argumentDomains() ) {
						sb.append( argdom.size() );
						sb.append( " " );
					}
					for ( int j = 0; j < entries.length; ++j ) {
						sb.append( entries[ j ] );
						if ( j != entries.length - 1 ) ;
						sb.append( " " );
					}
					output.append( sb );
					output.newLine();
				} else {
					throw new UnsupportedOperationException( "factor graph contains function fow which serialisation is not implemented yet. Currently only BooleanConflictConstraint and TensorTable are supported" );
				}
				functionToIndexMap.put( func, i++ );
			}

			// factors
			for ( final Factor< ?, ?, ? > factor : factors ) {
				final Function< ?, ? > function = factor.getFunction();
				final int numDims = factor.getDomain().numDimensions();
				final StringBuilder sb = new StringBuilder();
				sb.append( functionToIndexMap.get( function ) );
				for ( int d = 0; d < numDims; ++d ) {
					sb.append( " " );
					sb.append( varToIndexMap.get( factor.getVariable( d ) ) );
				}
				output.append( sb );
				output.newLine();
			}

			output.close();
		}

		public static FactorGraph load( final String fn, final List< VariableSerializer > variableSerializers ) throws FileNotFoundException, IOException {
			return new XmlIoFactorGraph().loadInternal( fn, variableSerializers );
		}

		private FactorGraph loadInternal( final String fn, final List< VariableSerializer > variableSerializers ) throws FileNotFoundException, IOException {
			final BufferedReader input = new BufferedReader( new FileReader( fn ) );

			ArrayList< String > parts;

			parts = nextLine( input );
			if ( parts == null || parts.size() != 3 )
				throw new IllegalArgumentException( "couldn't parse preamble" );

			final int numVariables = Integer.parseInt( parts.get( 0 ) );
			final int numFunctions = Integer.parseInt( parts.get( 1 ) );
			final int numFactors = Integer.parseInt( parts.get( 2 ) );

			final ArrayList< Variable< ? > > variables = new ArrayList< Variable< ? > >();
			for ( int i = 0; i < numVariables; ++i ) {
				variables.add( readVariable( input, variableSerializers ) );
			}

			final ArrayList< Function< ?, ? > > functions = new ArrayList< Function< ?, ? > >();
			for ( int i = 0; i < numFunctions; ++i ) {
				functions.add( readFunction( input ) );
			}

			final ArrayList< Factor< ?, ?, ? > > factors = new ArrayList< Factor< ?, ?, ? > >();
			for ( int i = 0; i < numFactors; ++i ) {
				factors.add( readFactor( input, functions, variables ) );
			}

			return new FactorGraph( variables, factors, functions );
		}

		private int variableId = 0;
		private int functionId = 0;
		private int factorId = 0;

		private Factor< ?, ?, ? > readFactor( final BufferedReader input, final List< Function< ?, ? > > functions, final List< Variable< ? > > variables ) throws IOException {
			final ArrayList< String > parts = nextLine( input );
			if ( parts != null && parts.size() >= 1 ) {
				int i = 0;

				final int functionId = Integer.parseInt( parts.get( i++ ) );
				final Function< ?, ? > function = functions.get( functionId );

				if ( function instanceof BooleanFunction ) {
					final BooleanFactor factor = new BooleanFactor( ( ( BooleanFunction ) function ).getDomain(), factorId++ );
					factor.setFunction( ( BooleanFunction ) function );
					final int numVariables = function.getDomain().numDimensions();
					if ( parts.size() >= 1 + numVariables ) {
						for ( int v = 0; v < numVariables; ++v ) {
							final int variableId = Integer.parseInt( parts.get( i++ ) );
							final BooleanVariable variable = ( BooleanVariable ) variables.get( variableId );
							factor.setVariable( v, variable );
						}
						return factor;
					}
				} else if ( function instanceof IntLabelFunction ) {
					final IntLabelFactor factor = new IntLabelFactor( ( ( IntLabelFunction ) function ).getDomain(), factorId++ );
					factor.setFunction( ( IntLabelFunction ) function );

					final int numVariables = function.getDomain().numDimensions();
					if ( parts.size() >= 1 + numVariables ) {
						for ( int v = 0; v < numVariables; ++v ) {
							final int variableId = Integer.parseInt( parts.get( i++ ) );
							final IntLabel variable = ( IntLabel ) variables.get( variableId );
							factor.setVariable( v, variable );
						}
						return factor;
					}
				}
			}
			throw new IllegalArgumentException( "couldn't parse factor" );
		}

		private Function< ?, ? > readFunction( final BufferedReader input ) throws IOException {
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

					boolean allStatesBinary = true;
					for ( int d = 0; d < numDims; ++d )
						if ( numStatesForDim[ d ] != 2 ) {
							allStatesBinary = false;
							break;
						}

					if ( allStatesBinary )
						return new BooleanTensorTable( numDims, entries, functionId++ );
					else
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

					// check for special case: BooleanConflictConstraint
					if ( value == 1 && relation == Relation.LE ) {
						boolean allCoefficientsOne = true;
						for ( int d = 0; d < numDims; ++d )
							if ( coefficients[ d ] != 1 ) {
								allCoefficientsOne = false;
								break;
							}
						if ( allCoefficientsOne )
							return BooleanConflictConstraint.getForNumDimensions( numDims );
					}

					return new IntLabelSumConstraint( coefficients, relation, value, functionId++ );
				}
			}
			throw new IllegalArgumentException( "couldn't parse function" );
		}

		private Variable< ? > readVariable( final BufferedReader input, final List< VariableSerializer > serializers ) throws IOException {
			final ArrayList< String > parts = nextLine( input );
			if ( parts == null || parts.size() != 1 )
				throw new IllegalArgumentException( "couldn't parse variable" );
			final int numStates = Integer.parseInt( parts.get( 0 ) );
			final int id = variableId++;
			for ( final VariableSerializer serializer : serializers ) {
				final Variable< ? > v = serializer.loadVariable( id, numStates );
				if ( v != null ) return v;
			}
			if ( numStates == 2 )
				return new BooleanVariable();
			else
				return new IntLabel( numStates, id );
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
}
