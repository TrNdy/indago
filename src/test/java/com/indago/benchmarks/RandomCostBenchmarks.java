package com.indago.benchmarks;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.indago.fg.Assignment;
import com.indago.fg.FactorGraph;
import com.indago.fg.factor.BooleanFactor;
import com.indago.fg.factor.Factor;
import com.indago.fg.function.BooleanConflictConstraint;
import com.indago.fg.function.BooleanFunction;
import com.indago.fg.function.BooleanTensorTable;
import com.indago.fg.value.BooleanValue;
import com.indago.fg.variable.BooleanVariable;
import com.indago.fg.variable.Variable;
import com.indago.segment.LabelingBuilder;
import com.indago.segment.LabelingForest;
import com.indago.segment.LabelingSegment;
import com.indago.segment.MinimalOverlapConflictGraph;
import com.indago.segment.MultiForestConflictGraph;
import com.indago.segment.PairwiseConflictGraph;
import com.indago.segment.RandomForestFactory;
import com.indago.segment.RandomSegmentCosts;
import com.indago.segment.fg.FactorGraphFactory;
import com.indago.segment.filteredcomponents.FilteredComponentTree;
import com.indago.segment.filteredcomponents.FilteredComponentTree.Filter;
import com.indago.segment.filteredcomponents.FilteredComponentTree.MaxGrowthPerStep;

import gurobi.GRB;
import gurobi.GRB.DoubleAttr;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import ij.IJ;
import net.imglib2.Dimensions;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedIntType;

public class RandomCostBenchmarks {

	private static int baseSeed = 4711;
	private static int numImgsPerParameterSet = 10;
	private static int numInnerLoopsPerSet = 100;

	private static int minComponentSize = 10;
	private static int maxComponentSize = 10000 - 1;
	private static Filter maxGrowthPerStep = new MaxGrowthPerStep( 200 );
	private static boolean darkToBright = false;

	private static boolean exportImgSets = true;
	private static String exportPath = "/Users/jug/MPI/temp";

	public static class GurobiReadouts {

		public int numIterations;
		public double runtime;
		public double objval;

		public GurobiReadouts( final int numIterations, final double runtime, final double objval ) {
			this.numIterations = numIterations;
			this.runtime = runtime;
			this.objval = objval;
		}
	}

	public static class Parameters {

		public int width;
		public int height;
		public int numSeedPixels;
		public double maxRadius;
		public double minDeltaR;
		public double maxDeltaR;
		public double meanDeltaR;
		public double sdDeltaR;
		public int minIntensity;
		public int maxIntensity;

		public Parameters( final int width, final int height, final int numSeedPixels, final double maxRadius, final double minDeltaR, final double maxDeltaR, final double meanDeltaR, final double sdDeltaR, final int minIntensity, final int maxIntensity ) {
			this.width = width;
			this.height = height;
			this.numSeedPixels = numSeedPixels;
			this.maxRadius = maxRadius;
			this.minDeltaR = minDeltaR;
			this.maxDeltaR = maxDeltaR;
			this.meanDeltaR = meanDeltaR;
			this.sdDeltaR = sdDeltaR;
			this.minIntensity = minIntensity;
			this.maxIntensity = maxIntensity;
		}

		@Override
		public String toString() {
			return String.format( "(w,h,numS,maxR,minD,maxD,meanD,sdD,minI,maxI) = (%d,%d,%d,%.2f,%.2f,%.2f,%.2f,%.2f,%d,%d)", width, height, numSeedPixels, maxRadius, minDeltaR, maxDeltaR, meanDeltaR, sdDeltaR, minIntensity, maxIntensity );
		}
	}

	public static void main( final String[] args ) throws Exception {

		final ArrayList< Parameters > parameterSets = new ArrayList< Parameters >();
		parameterSets.add( new Parameters( 256, 256, 10, 20.0, 0.5, 6.0, 1.0, 0.0, 0, 25 ) ); // first one we used
		parameterSets.add( new Parameters( 256, 256, 25, 20.0, 0.5, 6.0, 1.0, 0.0, 0, 25 ) ); // denser one

		System.out.println( "Starting benchmarks now!" );

		String summaryCollection = "\n\n\nSUMMARY:\n";
		summaryCollection += "========\n\n";
		summaryCollection += String.format( "baseSeed:              \t%d\n", baseSeed );
		summaryCollection += String.format( "numImgsPerParameterSet:\t%d\n", numImgsPerParameterSet );
		summaryCollection += String.format( "numInnerLoopsPerSet:   \t%d\n", numInnerLoopsPerSet );
		summaryCollection += "\n";
		summaryCollection += String.format( "minComponentSize:      \t%d\n", minComponentSize );
		summaryCollection += String.format( "maxComponentSize:      \t%d\n", maxComponentSize );
		summaryCollection += String.format( "filter:                \t%s\n", maxGrowthPerStep.toString() );
		summaryCollection += String.format( "darkToBright?          \t%s\n", ( darkToBright ) ? "true" : "false" );

		for ( int i = 0; i < parameterSets.size(); i++ ) {
			System.out.print( String.format( "\n\n\n\nGenerating random synthetic dataset %d of %d... ", i + 1, parameterSets.size() ) );
			final ArrayList< Img< UnsignedIntType >> imgs = generateRandomSyntheticImgs( numImgsPerParameterSet, baseSeed + i, parameterSets.get( i ) );
			System.out.println( "done!" );

			if ( exportImgSets ) {
				final File path = new File( exportPath );
				if ( path.isDirectory() && path.canWrite() ) {
					int j = 0;
					for ( final Img< UnsignedIntType > img : imgs ) {
						final String name = String.format( "ImageSet_%02d_%03d", i, j );
						IJ.save( ImageJFunctions.wrap( img, name ), path + "/" + name + ".tif" );
						j++;
					}

				} else {
					System.out.println( "ERROR -- Cannot write to export path '" + exportPath + "'" );
				}
			}

			summaryCollection += String.format( "\nParameter-set %3d:\n", i );
			summaryCollection += String.format( "------------------\n", i );
			summaryCollection += parameterSets.get( i ).toString() + "\n\n";
			summaryCollection += runBenchmarkOnImgs( imgs, new UnsignedIntType() );
		}

		System.out.println( summaryCollection );
	}

	public static ArrayList< Img< UnsignedIntType >> generateRandomSyntheticImgs( final int numImgs, final int seed, final Parameters p ) {

		final ArrayList< Img< UnsignedIntType >> imgs = new ArrayList< Img< UnsignedIntType >>();
		for ( int f = 0; f < numImgs; f++ ) {
			imgs.add( RandomForestFactory.getForestImg( p.width, p.height, p.numSeedPixels, p.maxRadius, p.minDeltaR, p.maxDeltaR, p.meanDeltaR, p.sdDeltaR, p.minIntensity, p.maxIntensity, seed + f ) );
		}

		return imgs;
	}

	public static < T extends RealType< T > & NativeType< T > > String runBenchmarkOnImgs( final List< Img< T > > imgs, final T type ) throws Exception {
		final Dimensions dims = imgs.get( 0 );

		final List< FilteredComponentTree< T > > fctrees = new ArrayList<>();
		final LabelingBuilder labelingBuilder = new LabelingBuilder( dims );

		for ( final Img< T > img : imgs ) {
			final FilteredComponentTree< T > newtree = FilteredComponentTree.buildComponentTree( img, type, minComponentSize, maxComponentSize, maxGrowthPerStep, darkToBright );
			fctrees.add( newtree );
		}

		System.out.print( "Building labeling forest... " );
		long t0 = System.currentTimeMillis();

		String summaryCollection = "";

//		int i = 1;
		final List< LabelingForest > labelingForests = new ArrayList<>();
		for ( final FilteredComponentTree< T > fctree : fctrees ) {
			labelingForests.add( labelingBuilder.buildLabelingForest( fctree ) );
//			final Img< ARGBType > labels = ArrayImgs.argbs( dims.dimension( 0 ), dims.dimension( 1 ) );
//			VisualizeLabeling.colorLabels( labelingBuilder.getLabeling(), ColorStream.iterator(), labels );
//			ImageJFunctions.show( labels, "Labels " + i );
//			++i;
		}

		long t1 = System.currentTimeMillis();
		System.out.println( String.format( "completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );
		summaryCollection += String.format( "LabelingForest:           \t%6.2f\n", ( t1 - t0 ) / 1000. );

//		*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***
//		*** *** *** *** *** *** *** *** CONFLICT GRAPH SECTION  *** *** *** *** *** *** *** *** *** *** *** ***
//		*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***

		System.out.print( "Constructing PairwiseConflictGraph... " );
		t0 = System.currentTimeMillis();
		final PairwiseConflictGraph conflictGraph1 = new PairwiseConflictGraph( labelingBuilder );
		conflictGraph1.getConflictCliques();
		t1 = System.currentTimeMillis();
		System.out.println( String.format( "completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );
		summaryCollection += String.format( "PairwiseConflictGraph:      \t%6.2f\n", ( t1 - t0 ) / 1000. );

		System.out.print( "Constructing MultiForestConflictGraph... " );
		t0 = System.currentTimeMillis();
		final MultiForestConflictGraph conflictGraph2 = new MultiForestConflictGraph( labelingForests );
		conflictGraph2.getConflictCliques();
		t1 = System.currentTimeMillis();
		System.out.println( String.format( "completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );
		summaryCollection += String.format( "MultiForestConflictGraph:   \t%6.2f\n", ( t1 - t0 ) / 1000. );

		System.out.print( "Constructing MinimalOverlapConflictGraph... " );
		t0 = System.currentTimeMillis();
		final MinimalOverlapConflictGraph conflictGraph3 = new MinimalOverlapConflictGraph( labelingBuilder );
		conflictGraph3.getConflictCliques();
		t1 = System.currentTimeMillis();
		System.out.println( String.format( "completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );
		summaryCollection += String.format( "MinimalOverlapConflictGraph:\t%6.2f\n", ( t1 - t0 ) / 1000. );

//		*** *** *** *** *** *** *** ***  CONFLICT GRAPH OUTPUT  *** *** *** *** *** *** *** *** *** *** *** ***
//		final HypothesisPrinter hp = new HypothesisPrinter();
//		for ( final LabelingForest labelingForest : labelingForests )
//		{
//			hp.assignIds( labelingForest );
//			hp.printHypothesisForest( labelingForest );
//		}
//		System.out.println();
//		hp.printConflictGraphCliques( conflictGraph1 );

//		*** *** *** *** *** *** *** *** SETTING RANDOM COSTS *** *** *** *** *** *** *** *** *** *** *** *** **
		final ArrayList< LabelingSegment > segments = labelingBuilder.getSegments();
		// assign random costs for testing purposes

		GurobiReadouts gurobiStats;
		final double[] fgBuildTimeTotal = new double[ 3 ];
		final double[] fgSolveTimeTotal = new double[ 3 ];
		final int[] fgSolveIterGurobi = new int[ 3 ];
		final double[] fgSolveTimeGurobi = new double[ 3 ];
		final double[] fgDeltaSolution = new double[ 3 ];

		for ( int ci = 0; ci < numInnerLoopsPerSet; ci++ ) {

			final double[] fgFoundSolution = new double[ 3 ];

			System.out.print( "Setting random segment costs... " );
			final RandomSegmentCosts costs = new RandomSegmentCosts( segments, 815 + ci );
			System.out.println( "done!" );

			//		*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***
			//		*** *** *** *** *** *** *** *** ***  FG and ILP SECTION *** *** *** *** *** *** *** *** *** *** *** ***
			//		*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***

			System.out.print( ">>\t Constructing FG1 from PairwiseConflictGraph... " );
			t0 = System.currentTimeMillis();
			final FactorGraph fg1 = FactorGraphFactory.createFromConflictGraph( segments, conflictGraph1, costs ).getFactorGraph();
			t1 = System.currentTimeMillis();
			System.out.println( String.format( ">>\t completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );
			fgBuildTimeTotal[ 0 ] += ( t1 - t0 ) / 1000.;

			System.out.print( ">>\t Constructing FG2 from MultiForestConflictGraph... " );
			t0 = System.currentTimeMillis();
			final FactorGraph fg2 = FactorGraphFactory.createFromConflictGraph( segments, conflictGraph2, costs ).getFactorGraph();
			t1 = System.currentTimeMillis();
			System.out.println( String.format( ">>\t completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );
			fgBuildTimeTotal[ 1 ] += ( t1 - t0 ) / 1000.;

			System.out.print( ">>\t Constructing FG3 from MinimalOverlapConflictGraph... " );
			t0 = System.currentTimeMillis();
			final FactorGraph fg3 = FactorGraphFactory.createFromConflictGraph( segments, conflictGraph3, costs ).getFactorGraph();
			t1 = System.currentTimeMillis();
			System.out.println( String.format( ">>\t completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );
			fgBuildTimeTotal[ 2 ] += ( t1 - t0 ) / 1000.;

			// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

			System.out.print( ">>\t Solving FG1... " );
			t0 = System.currentTimeMillis();
			gurobiStats = buildAndRunILP( fg1 );
			t1 = System.currentTimeMillis();
			System.out.println( String.format( ">>\t completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );
			fgSolveTimeTotal[ 0 ] += ( t1 - t0 ) / 1000.;
			fgSolveIterGurobi[ 0 ] += gurobiStats.numIterations;
			fgSolveTimeGurobi[ 0 ] += gurobiStats.runtime;
			fgFoundSolution[ 0 ] += gurobiStats.objval;

			System.out.print( ">>\t Solving FG2... " );
			t0 = System.currentTimeMillis();
			gurobiStats = buildAndRunILP( fg2 );
			t1 = System.currentTimeMillis();
			System.out.println( String.format( ">>\t completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );
			fgSolveTimeTotal[ 1 ] += ( t1 - t0 ) / 1000.;
			fgSolveIterGurobi[ 1 ] += gurobiStats.numIterations;
			fgSolveTimeGurobi[ 1 ] += gurobiStats.runtime;
			fgFoundSolution[ 1 ] += gurobiStats.objval;

			System.out.print( ">>\t Solving FG3... " );
			t0 = System.currentTimeMillis();
			gurobiStats = buildAndRunILP( fg3 );
			t1 = System.currentTimeMillis();
			System.out.println( String.format( ">>\t completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );
			fgSolveTimeTotal[ 2 ] += ( t1 - t0 ) / 1000.;
			fgSolveIterGurobi[ 2 ] += gurobiStats.numIterations;
			fgSolveTimeGurobi[ 2 ] += gurobiStats.runtime;
			fgFoundSolution[ 2 ] += gurobiStats.objval;

			// We do only want to accumulate the difference to the best solution found...
			final double minimalSolution = Math.min( Math.min( fgFoundSolution[ 0 ], fgFoundSolution[ 1 ] ), fgFoundSolution[ 2 ] );
			fgDeltaSolution[ 0 ] += fgFoundSolution[ 0 ] - minimalSolution;
			fgDeltaSolution[ 1 ] += fgFoundSolution[ 1 ] - minimalSolution;
			fgDeltaSolution[ 2 ] += fgFoundSolution[ 2 ] - minimalSolution;
		}

		summaryCollection += String.format( "Cumulated FG build time:       \t%7.2f\t%7.2f\t%7.2f\n", fgBuildTimeTotal[ 0 ], fgBuildTimeTotal[ 1 ], fgBuildTimeTotal[ 2 ] );
		summaryCollection += String.format( "Cumulated delta_optimum:       \t%7.2f\t%7.2f\t%7.2f\n", fgDeltaSolution[ 0 ], fgDeltaSolution[ 1 ], fgDeltaSolution[ 2 ] );
		summaryCollection += String.format( "Cumulated gurobi solving iter: \t%7d\t%7d\t%7d\n", fgSolveIterGurobi[ 0 ], fgSolveIterGurobi[ 1 ], fgSolveIterGurobi[ 2 ] );
		summaryCollection += String.format( "Cumulated gurobi solving time: \t%7.2f\t%7.2f\t%7.2f\n", fgSolveTimeGurobi[ 0 ], fgSolveTimeGurobi[ 1 ], fgSolveTimeGurobi[ 2 ] );
		summaryCollection += String.format( "Cumulated total solving time:  \t%7.2f\t%7.2f\t%7.2f\n", fgSolveTimeTotal[ 0 ], fgSolveTimeTotal[ 1 ], fgSolveTimeTotal[ 2 ] );

//		*** *** *** *** *** *** *** ***  FACTOR GRAPH OUTPUT  *** *** *** *** *** *** *** *** *** *** *** ***
//		final JFrame guiFrame = new JFrame( "FG from SegmentMultiForest" );
//		// Set window-closing action...
//		guiFrame.addWindowListener( new WindowAdapter() {
//
//			@Override
//			public void windowClosing( final WindowEvent we ) {
//				System.exit( 0 );
//			}
//		} );
//
//		guiFrame.getContentPane().setLayout( new BorderLayout() );
//		guiFrame.getContentPane().add( new FgPanel( fg1 ), BorderLayout.CENTER );
//		guiFrame.setSize( 800, 600 );
//		guiFrame.setVisible( true );

		return summaryCollection;
	}

	private static GurobiReadouts buildAndRunILP( final FactorGraph fg ) throws GRBException {
		for ( final Variable< ? > variable : fg.getVariables() ) {
			if ( !( variable instanceof BooleanVariable ) )
				throw new IllegalArgumentException();
		}
		final ArrayList< BooleanFactor > constraints = new ArrayList< BooleanFactor >();
		final ArrayList< BooleanFactor > unaries = new ArrayList< BooleanFactor >();
		for ( final Factor< ?, ?, ? > f : fg.getFactors() ) {
			if ( f instanceof BooleanFactor ) {
				final BooleanFactor factor = ( BooleanFactor ) f;
				final BooleanFunction function = factor.getFunction();
				if ( function instanceof BooleanConflictConstraint )
					constraints.add( factor );
				else if ( function instanceof BooleanTensorTable )
					unaries.add( factor );
				else
					throw new IllegalArgumentException();
			} else
				throw new IllegalArgumentException();
		}

		final List< BooleanVariable > variables = ( List< BooleanVariable > ) fg.getVariables();
		final HashMap< BooleanVariable, Integer > variableToIndex = new HashMap<>();
		int variableIndex = 0;
		for ( final BooleanVariable v : variables )
			variableToIndex.put( v, variableIndex++ );

		final GRBEnv env = new GRBEnv( "mip1.log" );
		final GRBModel model = new GRBModel( env );

		// Create variables
		final GRBVar[] vars = model.addVars( variables.size(), GRB.BINARY );

		// Integrate new variables
		model.update();

		// Set objective: minimize costs
		final double[] coeffs = new double[ variables.size() ];
		for ( final BooleanFactor factor : unaries ) {
			final int i = variableToIndex.get( factor.getVariable( 0 ) );
			final BooleanTensorTable costs = ( BooleanTensorTable ) factor.getFunction();
			coeffs[ i ] = costs.evaluate( BooleanValue.TRUE ) - costs.evaluate( BooleanValue.FALSE );
		}
		final GRBLinExpr expr = new GRBLinExpr();
		expr.addTerms( coeffs, vars );
		model.setObjective( expr, GRB.MINIMIZE );

		// Add constraints.
		for ( int i = 0; i < constraints.size(); i++ ) {
			final BooleanFactor constraint = constraints.get( i );
			final GRBLinExpr lhsExprs = new GRBLinExpr();
			for ( final BooleanVariable variable : constraint.getVariables() ) {
				final int vi = variableToIndex.get( variable );
				lhsExprs.addTerm( 1.0, vars[ vi ] );
			}
			model.addConstr( lhsExprs, GRB.LESS_EQUAL, 1.0, null );
		}

		// Optimize model
		model.optimize();
		final int iterCount = ( int ) Math.round( model.get( GRB.DoubleAttr.IterCount ) );
		final double solvingTime = model.get( GRB.DoubleAttr.Runtime );
		final double objval = model.get( GRB.DoubleAttr.ObjVal );
//		System.out.println( "Obj: " + model.get( GRB.DoubleAttr.ObjVal ) );

		// Build assignment
		final Assignment assignment = new Assignment( variables );
		for ( int i = 0; i < variables.size(); i++ ) {
			final BooleanVariable variable = variables.get( i );
			final BooleanValue value = vars[ i ].get( DoubleAttr.X ) > 0.5 ? BooleanValue.TRUE : BooleanValue.FALSE;
			assignment.assign( variable, value );

//			System.out.println( variable + " = " + assignment.getAssignment( variable ) );
		}

		// Dispose of model and environment
		model.dispose();
		env.dispose();

		return new GurobiReadouts( iterCount, solvingTime, objval );
	}
}
