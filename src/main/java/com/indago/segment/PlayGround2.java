package com.indago.segment;

import gurobi.GRB;
import gurobi.GRB.DoubleAttr;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import io.scif.img.ImgOpener;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import net.imglib2.Dimensions;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
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
import com.indago.fg.gui.FgPanel;
import com.indago.fg.value.BooleanValue;
import com.indago.fg.variable.BooleanVariable;
import com.indago.fg.variable.Variable;
import com.indago.segment.fg.FactorGraphFactory;
import com.indago.segment.filteredcomponents.FilteredComponentTree;

public class PlayGround2 {

	public static void main( final String[] args ) throws Exception {
		final List< String > filenames = new ArrayList< String >();
		filenames.add( "src/main/resources/forest1.tif" );
		filenames.add( "src/main/resources/forest2.tif" );
		filenames.add( "src/main/resources/forest3.tif" );
		doIt( filenames, new UnsignedIntType() );
	}

	public static < T extends RealType< T > & NativeType< T > > void doIt( final List< String > filenames, final T type ) throws Exception {
		final int minComponentSize = 10;
		final int maxComponentSize = 10000;
		final int maxGrowthPerStep = 1;
		final boolean darkToBright = false;

		final List< Img< T > > imgs = new ArrayList< Img< T > >();
		final List< FilteredComponentTree< T > > fctrees = new ArrayList< FilteredComponentTree< T > >();
		final List< LabelingForest > labelingForests = new ArrayList< LabelingForest >();
		final List< SegmentForest > segmentForests = new ArrayList< SegmentForest >();

		for ( final String filename : filenames ) {
			final Img< T > img = new ImgOpener().openImg( filename, new ArrayImgFactory< T >(), type );
			final Dimensions dims = img;
			imgs.add( img );

			final FilteredComponentTree< T > newtree = FilteredComponentTree.buildComponentTree( img, type, minComponentSize, maxComponentSize, maxGrowthPerStep, darkToBright );
			fctrees.add( newtree );

			final LabelingForest newLF = LabelingForest.fromForest( newtree, dims );
			labelingForests.add( newLF );

			final SegmentForest newSF = SegmentForest.fromLabelingForest( newLF );
			segmentForests.add( newSF );
		}

		final SegmentMultiForest segmentMultiForest = new SegmentMultiForest( segmentForests );

		System.out.println( "\n>>>> Pairwise Constraints <<<<" );
		new ShowConflicts( segmentMultiForest, false );
		System.out.println( "\n>>>> Compact Clique Constraints <<<<" );
		new ShowConflicts( segmentMultiForest, true );

		// Assign random costs to segments in MultiForest (for testing purposes)
		final RandomSegmentCosts costs = new RandomSegmentCosts( segmentMultiForest, 815 );
		final FactorGraph fg = FactorGraphFactory.createFromSegmentMultiForest( segmentMultiForest, costs, false );

		buildILP( fg );

		final JFrame guiFrame = new JFrame( "FG from SegmentMultiForest" );
		// Set window-closing action...
		guiFrame.addWindowListener( new WindowAdapter() {

			@Override
			public void windowClosing( final WindowEvent we ) {
				System.exit( 0 );
			}
		} );

		guiFrame.getContentPane().setLayout( new BorderLayout() );
		guiFrame.getContentPane().add( new FgPanel( fg ), BorderLayout.CENTER );
		guiFrame.setSize( 800, 600 );
		guiFrame.setVisible( true );
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
		for ( final BooleanFactor factor : unaries )
		{
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
			for ( final BooleanVariable variable : constraint.getVariables() )
			{
				final int vi = variableToIndex.get( variable );
				lhsExprs.addTerm( 1.0, vars[ vi ] );
			}
			model.addConstr( lhsExprs, GRB.LESS_EQUAL, 1.0, null );
		}

		// Optimize model
		model.optimize();
		System.out.println( "Obj: " + model.get( GRB.DoubleAttr.ObjVal ) );

		// Build assignment
		final Assignment assignment = new Assignment( variables );
		for ( int i = 0; i < variables.size(); i++ ) {
			final BooleanVariable variable = variables.get( i );
			final BooleanValue value = vars[ i ].get( DoubleAttr.X ) > 0.5 ?
					BooleanValue.TRUE :
					BooleanValue.FALSE;
			assignment.assign( variable, value );

			System.out.println( variable + " = " + assignment.getAssignment( variable ) );
		}

		// Dispose of model and environment
		model.dispose();
		env.dispose();



	}

	static class ShowConflicts {

		private final HashMap< Segment, Integer > segmentToId = new HashMap< Segment, Integer >();

		private int idGenerator = 0;

		public ShowConflicts( final SegmentMultiForest segmentMultiForest, final boolean doCompactConstraints ) {
			for ( final Segment segment : segmentMultiForest.roots() )
				printSegment( "", segment );

			System.out.println();
			final Collection< ? extends Collection< Segment > > cliques;
			if ( doCompactConstraints ) {
				cliques = segmentMultiForest.getConflictGraphCliques();
			} else {
				cliques = segmentMultiForest.getConflictGraphEdges();
			}
			for ( final Collection< Segment > clique : cliques ) {
				System.out.print( "( " );
				for ( final Segment segment : clique )
					System.out.print( String.format( "%2d ", segmentToId.get( segment ) ) );
				System.out.println( ")" );
			}
		}

		private void printSegment( final String prefix, final Segment segment ) {
			Integer id = segmentToId.get( segment );
			if ( id == null ) {
				id = new Integer( idGenerator++ );
				segmentToId.put( segment, id );
			}
			System.out.println( prefix + id );
			for ( final Segment c : segment.getChildren() )
				printSegment( prefix + "  ", c );
		}

	}

	/**
	 * Assigns random costs to all segments in a SegmentMultiForest.
	 * This is useful for testing artificial setups...
	 * 
	 * @author jug
	 */
	static class RandomSegmentCosts implements SegmentCosts {

		private final Random rand;
		private final HashMap< Segment, Double > segmentToCost = new HashMap< Segment, Double >();

		public RandomSegmentCosts( final SegmentMultiForest segmentMultiForest, final int randomSeed ) {
			rand = new Random( randomSeed );
			for ( final Segment root : segmentMultiForest.roots() ) {
				recursivleyDrawRandomCosts( root );
			}
		}

		/**
		 * Recursively draws random costs for given segment all all its
		 * children.
		 * 
		 * @param segment
		 */
		private void recursivleyDrawRandomCosts( final Segment segment ) {

			segmentToCost.put( segment, - rand.nextDouble() );

			for ( final Segment child : segment.getChildren() ) {
				recursivleyDrawRandomCosts( child );
			}
		}

		/**
		 * @param segment
		 * @return the cost assigned to the given segment or Double.MAX_VALUE if
		 *         this segment is unknown.
		 */
		@Override
		public double getCost( final Segment segment ) {
			final Double muh = segmentToCost.get( segment );
			if ( muh != null ) { return muh.doubleValue(); }
			return Double.MAX_VALUE;
		}
	}
}
