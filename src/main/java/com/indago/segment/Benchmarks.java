package com.indago.segment;

import gurobi.GRB;
import gurobi.GRB.DoubleAttr;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.imglib2.Dimensions;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedIntType;

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
import com.indago.segment.fg.FactorGraphFactory;
import com.indago.segment.filteredcomponents.FilteredComponentTree;

public class Benchmarks {

	public static void main( final String[] args ) throws Exception {
		System.out.print( "Generating random synthetic data... " );
		final ArrayList< Img< UnsignedIntType >> imgs = generateRandomSyntheticImgs( 100, 4711 );
		System.out.println( "done!" );

		System.out.println( "Starting benchmarks now!" );
		runBenchmarkOnImgs( imgs, new UnsignedIntType() );
	}

	public static ArrayList< Img< UnsignedIntType >> generateRandomSyntheticImgs( final int numImgs, final int seed ) {
		final int width = 512;
		final int height = 512;
		final int numSeedPixels = 10;
		final double maxRadius = 50.;
		final double minDeltaR = .5;
		final double maxDeltaR = 6.;
		final double meanDeltaR = .5;
		final double sdDeltaR = 0;// 1.5;
		final int minIntensity = 0;
		final int maxIntensity = 100;

		final ArrayList< Img< UnsignedIntType >> imgs = new ArrayList< Img< UnsignedIntType >>();
		for ( int f = 0; f < numImgs; f++ ) {
			imgs.add( RandomForestFactory.getForestImg( width, height, numSeedPixels, maxRadius, minDeltaR, maxDeltaR, meanDeltaR, sdDeltaR, minIntensity, maxIntensity, seed + f ) );
		}

		return imgs;
	}

	public static < T extends RealType< T > & NativeType< T > > void runBenchmarkOnImgs( final List< Img< T > > imgs, final T type ) throws Exception {
		final int minComponentSize = 10;
		final int maxComponentSize = 10000 - 1;
		final int maxGrowthPerStep = 200;
		final boolean darkToBright = false;

		final Dimensions dims = imgs.get( 0 );

		final List< FilteredComponentTree< T > > fctrees = new ArrayList<>();
		final LabelingBuilder labelingBuilder = new LabelingBuilder( dims );

		for ( final Img< T > img : imgs ) {
			final FilteredComponentTree< T > newtree = FilteredComponentTree.buildComponentTree( img, type, minComponentSize, maxComponentSize, maxGrowthPerStep, darkToBright );
			fctrees.add( newtree );
		}

		System.out.print( "Building labeling forest... " );
		long t0 = System.currentTimeMillis();

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

//		*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***
//		*** *** *** *** *** *** *** *** CONFLICT GRAPH SECTION  *** *** *** *** *** *** *** *** *** *** *** *** 
//		*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***		

		System.out.print( "Constructing PairwiseConflictGraph... " );
		t0 = System.currentTimeMillis();

		final PairwiseConflictGraph conflictGraph1 = new PairwiseConflictGraph( labelingBuilder );
		conflictGraph1.getConflictGraphCliques();

		t1 = System.currentTimeMillis();
		System.out.println( String.format( "completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );

		System.out.print( "Constructing MultiForestConflictGraph... " );
		t0 = System.currentTimeMillis();

		final MultiForestConflictGraph conflictGraph2 = new MultiForestConflictGraph( labelingForests );
		conflictGraph2.getConflictGraphCliques();

		t1 = System.currentTimeMillis();
		System.out.println( String.format( "completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );

		System.out.print( "Constructing MinimalOverlapConflictGraph... " );
		t0 = System.currentTimeMillis();

		final MinimalOverlapConflictGraph conflictGraph3 = new MinimalOverlapConflictGraph( labelingBuilder );
		conflictGraph3.getConflictGraphCliques();

		t1 = System.currentTimeMillis();
		System.out.println( String.format( "completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );

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
		System.out.print( "Setting random segment costs... " );
		final ArrayList< LabelingSegment > segments = labelingBuilder.getSegments();
		// assign random costs for testing purposes
		final RandomSegmentCosts costs = new RandomSegmentCosts( segments, 815 );
		System.out.println( "done!" );

//		*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***
//		*** *** *** *** *** *** *** *** ***  FG and ILP SECTION *** *** *** *** *** *** *** *** *** *** *** *** 
//		*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***		

		System.out.print( "Constructing FG1 from PairwiseConflictGraph... " );
		t0 = System.currentTimeMillis();

		final FactorGraph fg1 = FactorGraphFactory.createFromConflictGraph( segments, conflictGraph1, costs );

		t1 = System.currentTimeMillis();
		System.out.println( String.format( "completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );

		System.out.print( "Constructing FG2 from MultiForestConflictGraph... " );
		t0 = System.currentTimeMillis();

		final FactorGraph fg2 = FactorGraphFactory.createFromConflictGraph( segments, conflictGraph2, costs );

		t1 = System.currentTimeMillis();
		System.out.println( String.format( "completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );

		System.out.print( "Constructing FG3 from MinimalOverlapConflictGraph... " );
		t0 = System.currentTimeMillis();

		final FactorGraph fg3 = FactorGraphFactory.createFromConflictGraph( segments, conflictGraph3, costs );

		t1 = System.currentTimeMillis();
		System.out.println( String.format( "completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );

		System.out.print( "Solving FG1... " );
		t0 = System.currentTimeMillis();

		buildILP( fg1 );

		t1 = System.currentTimeMillis();
		System.out.println( String.format( "completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );

		System.out.print( "Solving FG2... " );
		t0 = System.currentTimeMillis();

		buildILP( fg2 );

		t1 = System.currentTimeMillis();
		System.out.println( String.format( "completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );

		System.out.print( "Solving FG3... " );
		t0 = System.currentTimeMillis();

		buildILP( fg3 );

		t1 = System.currentTimeMillis();
		System.out.println( String.format( "completed in %.2f seconds!", ( t1 - t0 ) / 1000. ) );

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
	}

	private static void buildILP( final FactorGraph fg ) throws GRBException {
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
	}
}
