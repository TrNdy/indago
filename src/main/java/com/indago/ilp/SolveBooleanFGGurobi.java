package com.indago.ilp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.indago.old_fg.Assignment;
import com.indago.old_fg.FactorGraph;
import com.indago.old_fg.factor.BooleanFactor;
import com.indago.old_fg.factor.Factor;
import com.indago.old_fg.function.BooleanConflictConstraint;
import com.indago.old_fg.function.BooleanFunction;
import com.indago.old_fg.function.BooleanTensorTable;
import com.indago.old_fg.function.BooleanWeightedIndexSumConstraint;
import com.indago.old_fg.function.OldBooleanAssignmentConstraint;
import com.indago.old_fg.value.BooleanValue;
import com.indago.old_fg.variable.BooleanVariable;
import com.indago.old_fg.variable.Variable;

import gurobi.GRB;
import gurobi.GRB.DoubleAttr;
import gurobi.GRBCallback;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

/**
 * A solver for boolean factor graphs that uses Gurobi for the optimization
 * step.
 *
 * @author pietzsch, jug
 */
@SuppressWarnings( "restriction" )
public class SolveBooleanFGGurobi {

	public static File defaultLogFileDirectory = new File( "." );

	private final File logFileDirectory;

	private GRBEnv env;

	public SolveBooleanFGGurobi() throws GRBException {
		this( defaultLogFileDirectory );
	}

	public SolveBooleanFGGurobi( final File logFileDirectory ) {
		this.logFileDirectory = logFileDirectory;
		env = null;
	}

	public static Assignment staticSolve( final FactorGraph fg ) throws GRBException {
		return staticSolve( fg, null );
	}

	public static Assignment staticSolve( final FactorGraph fg, final GRBCallback callback ) throws GRBException {
		final SolveBooleanFGGurobi solver = new SolveBooleanFGGurobi();
		final Assignment assignment = solver.solve( fg, callback );
		solver.dispose();
		return assignment;
	}

	/**
	 * Solves a given factor graph.
	 *
	 * @param fg
	 *            the factor graph to be solved.
	 * @param callback
	 *            a Gurobi callback class (or <code>null</code> if you do not
	 *            want to use a callback handler).
	 * @return an <code>Assignment</code> containing the solution.
	 * @throws GRBException
	 */
	@SuppressWarnings( "unchecked" )
	public Assignment solve( final FactorGraph fg, final GRBCallback callback ) throws GRBException {
		for ( final Variable< ? > variable : fg.getVariables() ) {
			if ( !( variable instanceof BooleanVariable ) )
				throw new IllegalArgumentException( "Only variables of type BooleanVariable are currently supported." );
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
				else if ( function instanceof OldBooleanAssignmentConstraint )
					constraints.add( factor );
				else if ( function instanceof BooleanWeightedIndexSumConstraint )
					constraints.add( factor );
				else
					throw new IllegalArgumentException( "Only factors of type BooleanAssignmentConstraint, BooleanConflictConstraint, BooleanTensorTable, and BooleanWeightedIndexSumConstraint are currently supported." );
			} else
				throw new IllegalArgumentException( "Only factors of type BooleanFactor are currently supported." );
		}

		final List< BooleanVariable > variables = ( List< BooleanVariable > ) fg.getVariables();
		final HashMap< BooleanVariable, Integer > variableToIndex = new HashMap<>();
		int variableIndex = 0;
		for ( final BooleanVariable v : variables )
			variableToIndex.put( v, variableIndex++ );

		createEnvIfNecessary();
		final GRBModel model = new GRBModel( env );

		// Hook in callback
		if ( callback != null ) {
			model.setCallback( callback );
			model.getEnv().set( "LogToConsole", "0" );
		}

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
		env.message( "Obj: " + model.get( GRB.DoubleAttr.ObjVal ) );

		// Build assignment
		final Assignment assignment = new Assignment( variables );
		for ( int i = 0; i < variables.size(); i++ ) {
			final BooleanVariable variable = variables.get( i );
			final BooleanValue value = vars[ i ].get( DoubleAttr.X ) > 0.5 ? BooleanValue.TRUE : BooleanValue.FALSE;
			assignment.assign( variable, value );
//			env.message( variable + " = " + assignment.getAssignment( variable ) );
		}

		// Dispose of model
		model.dispose();

		return assignment;
	}

	public void dispose() throws GRBException {
		if ( env == null ) return;
		env.dispose();
		env = null;
	}

	private void createEnvIfNecessary() throws GRBException {
		if ( env == null ) env = new GRBEnv( getLogFilename() );
	}

	private String getLogFilename() {
		return ( ( logFileDirectory != null && logFileDirectory.exists() ) ? logFileDirectory.getAbsolutePath() : "." ) + "/gurobi_indago.log";
	}
}
