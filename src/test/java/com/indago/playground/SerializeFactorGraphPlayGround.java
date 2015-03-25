package com.indago.playground;

import gnu.trove.impl.Constants;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.TIntHashSet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.indago.fg.Assignment;
import com.indago.fg.FactorGraph;
import com.indago.fg.domain.Domain;
import com.indago.fg.domain.FunctionDomain;
import com.indago.fg.factor.Factor;
import com.indago.fg.function.BooleanConflictConstraint;
import com.indago.fg.function.Function;
import com.indago.fg.function.TensorTable;
import com.indago.fg.variable.Variable;
import com.indago.ilp.SolveBooleanFGGurobi;
import com.indago.segment.HypothesisPrinter;
import com.indago.segment.LabelingForest;
import com.indago.segment.LabelingPlus;
import com.indago.segment.LabelingSegment;
import com.indago.segment.MinimalOverlapConflictGraph;
import com.indago.segment.RandomSegmentCosts;
import com.indago.segment.XmlIoLabelingPlus;
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

		XmlIoFactorGraph.save( fg, fgDataFilename );

		final SolveBooleanFGGurobi solver = new SolveBooleanFGGurobi();
		final Assignment assignment = solver.solve( fg );
		System.out.println( assignment );
	}

	public static class XmlIoFactorGraph {

		public static void save( final FactorGraph fg, final String fn ) throws IOException {
			final BufferedWriter output = new BufferedWriter( new FileWriter( fn ) );

			output.append( "# indago boolean" );
			output.newLine();

			// preamble
			final List< ? extends Variable< ? > > variables = fg.getVariables();
			final List< ? extends Function< ?, ? > > functions = fg.getFunctions();
			final List< ? extends Factor< ?, ?, ? > > factors = fg.getFactors();
			final int numVariables = variables.size();
			final int numFunctions = countFunctions( functions, factors );
			final int numFactors = factors.size();
			output.write( String.format( "%d %d %d", numVariables, numFunctions, numFactors ) );
			output.newLine();

			// variables
			final TObjectIntHashMap< Variable< ? > > varToIndexMap = new TObjectIntHashMap< Variable<?> >( numVariables );
			int i = 0;
			for ( final Variable< ? > var : variables ) {
				final int numStates = var.getDomain().size();
				output.write( String.format( "%d", numStates ) );
				output.newLine();
				varToIndexMap.put( var, i );
				++i;
			}

			// functions
			final TObjectIntHashMap< Function< ?, ? > > tensorTableToIndexMap = new TObjectIntHashMap< Function< ?, ? > >( numFunctions );
			i = 0;
			// handle TensorTable
			for ( final Function< ?, ? > func : functions ) {
				if ( func instanceof BooleanConflictConstraint ) {
					// do nothing now, handled separately below
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
					tensorTableToIndexMap.put( func, i++ );
				} else {
					throw new UnsupportedOperationException( "factor graph contains function fow which serialisation is not implemented yet. Currently only BooleanConflictConstraint and TensorTable are supported" );
				}
			}
			// handle BooleanConflictConstraint of different dimensions
			final TIntIntHashMap conflictNumDimsToIndexMap = new TIntIntHashMap( Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, -1, -1 );
			for ( final Factor< ?, ?, ? > factor : factors ) {
				if ( factor.getFunction() instanceof BooleanConflictConstraint ) {
					final int numDims = factor.getDomain().numDimensions();
					if ( ! conflictNumDimsToIndexMap.containsKey( numDims ) ) {
						final StringBuilder sb = new StringBuilder();
						sb.append( "constraint " );
						sb.append( numDims );
						for ( int d = 0; d < numDims; ++d )
							sb.append( " 1" );
						sb.append( " <= 1" );
						output.append( sb );
						output.newLine();
						conflictNumDimsToIndexMap.put( numDims, i++ );
					}
				}
			}

			// factors
			for ( final Factor< ?, ?, ? > factor : factors ) {
				final Function< ?, ? > function = factor.getFunction();
				final int numDims = factor.getDomain().numDimensions();
				final int functionIndex;
				if ( function instanceof BooleanConflictConstraint )
					functionIndex = conflictNumDimsToIndexMap.get( numDims );
				else if ( function instanceof TensorTable )
					functionIndex = tensorTableToIndexMap.get( function );
				else
					throw new IllegalArgumentException();

				final StringBuilder sb = new StringBuilder();
				sb.append( functionIndex );
				for ( int d = 0; d < numDims; ++d ) {
					sb.append( " " );
					sb.append( varToIndexMap.get( factor.getVariable( d ) ) );
				}
				output.append( sb );
				output.newLine();
			}

			output.close();
		}

		private static int countFunctions( final List< ? extends Function< ?, ? >> functions, final List< ? extends Factor< ?, ?, ? >> factors ) {
			int n = 0;
			// handle TensorTable
			for ( final Function< ?, ? > func : functions )
				if ( func instanceof TensorTable )
					++n;

			// handle BooleanConflictConstraint of different dimensions
			final TIntHashSet dims = new TIntHashSet( Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, -1 );
			for ( final Factor< ?, ?, ? > factor : factors )
				if ( factor.getFunction() instanceof BooleanConflictConstraint )
					dims.add( factor.getDomain().numDimensions() );
			n += dims.size();

			return n;
		}
	}
}
