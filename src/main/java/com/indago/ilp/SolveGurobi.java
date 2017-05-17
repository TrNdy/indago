package com.indago.ilp;

import java.io.File;
import java.util.Collection;
import java.util.List;

import com.indago.fg.Assignment;
import com.indago.fg.Factor;
import com.indago.fg.LinearConstraint;
import com.indago.fg.Relation;
import com.indago.fg.UnaryCostConstraintGraph;
import com.indago.fg.Variable;

import gnu.trove.impl.Constants;
import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gurobi.GRB;
import gurobi.GRBCallback;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public class SolveGurobi {

	public static File defaultLogFileDirectory = new File( "." );

	public static int GRB_PRESOLVE = -1; // values are -1 (auto); 0 (off), 1 (moderate), and 2 (aggressive)

	private final File logFileDirectory;

	private GRBEnv env;

	private GRBModel model;

	public SolveGurobi() throws GRBException {
		this( defaultLogFileDirectory );
	}

	public SolveGurobi( final File logFileDirectory ) {
		this.logFileDirectory = logFileDirectory;
		env = null;
	}

	public static Assignment< Variable > staticSolve( final UnaryCostConstraintGraph fg ) throws GRBException {
		return staticSolve( fg, null );
	}

	public static Assignment staticSolve( final UnaryCostConstraintGraph fg, final GRBCallback callback ) throws GRBException {
		final SolveGurobi solver = new SolveGurobi();
		final Assignment< Variable > assignment = solver.solve( fg, callback );
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
	public Assignment< Variable > solve( final UnaryCostConstraintGraph fg, final GRBCallback callback ) throws GRBException {
		createEnvIfNecessary();

		if ( model != null ) {
			// Dispose of model
			model.dispose();
		}

		model = new GRBModel( env );

		final Collection< Variable > variables = fg.getVariables();
		final Collection< Factor > unaries = fg.getUnaries();
		final Collection< Factor > constraints = fg.getConstraints();

		final TObjectIntMap< Variable > variableToIndex = new TObjectIntHashMap<>();
		int variableIndex = 0;
		for ( final Variable v : variables )
			variableToIndex.put( v, variableIndex++ );

		// Hook in callback
		if ( callback != null ) {
			model.setCallback( callback );
			model.getEnv().set( "LogToConsole", "0" );
		}

		// Set model parameters
		model.getEnv().set( GRB.IntParam.Presolve, GRB_PRESOLVE );

		// Create variables
		final GRBVar[] vars = model.addVars( variables.size(), GRB.BINARY );

		// Integrate new variables
		model.update();

		// Set objective: minimize costs
		double constantTerm = 0;
		final TObjectDoubleMap< Variable > variableToCoeff = new TObjectDoubleHashMap<>(
				variables.size(), Constants.DEFAULT_LOAD_FACTOR, 0 );
		for ( final Factor factor : unaries ) {
			final Variable variable = factor.getVariables().get( 0 );
			final double cost0 = factor.getFunction().evaluate( 0 );
			final double cost1 = factor.getFunction().evaluate( 1 );
			constantTerm += cost0;
			final double coeff = ( cost1 - cost0 ) + variableToCoeff.get( variable );
			variableToCoeff.put( variable, coeff );
		}
		final double[] objectiveCoeffs = new double[ variableToCoeff.size() ];
		final GRBVar[] objectiveVars = new GRBVar[ variableToCoeff.size() ];
		final TObjectDoubleIterator< Variable > iterator = variableToCoeff.iterator();
		for ( int i = 0; iterator.hasNext(); ++i ) {
			iterator.advance();
			objectiveVars[ i ] = vars[ variableToIndex.get( iterator.key() ) ];
			objectiveCoeffs[ i ] = iterator.value();
		}
		final GRBLinExpr expr = new GRBLinExpr();
		expr.addTerms( objectiveCoeffs, objectiveVars );
		expr.addConstant( constantTerm );
		model.setObjective( expr, GRB.MINIMIZE );

		// Add constraints.
		for ( final Factor factor : constraints ) {
			final int arity = factor.getArity();
			final LinearConstraint constr = ( LinearConstraint ) factor.getFunction();

			final double[] constrCoeffs = constr.getCoefficients();
			final GRBVar[] constrVars = new GRBVar[ arity ];
			final List< Variable > fv = factor.getVariables();
			for ( int i = 0; i < arity; ++i ) {
				constrVars[ i ] = vars[ variableToIndex.get( fv.get( i ) ) ];
			}

			final GRBLinExpr lhsExprs = new GRBLinExpr();
			lhsExprs.addTerms( constrCoeffs, constrVars );
			model.addConstr( lhsExprs, relationToGRB( constr.getRelation() ), constr.getRhs(), null );
		}

		// Optimize model
		model.optimize();

		final int optimstatus = model.get( GRB.IntAttr.Status );
		double objval = -1;
		if ( optimstatus == GRB.Status.OPTIMAL ) {
			objval = model.get( GRB.DoubleAttr.ObjVal );
			env.message( "Optimal objective value: " + objval );
		} else if ( optimstatus == GRB.Status.INFEASIBLE ) {
			throw new IllegalStateException( "Model is INFEASIBLE! (Did the latest Leveraged Edit cause this?)" );
		}

		final double[] dvals = model.get( GRB.DoubleAttr.X, vars );
		final int[] vals = new int[ dvals.length ];
		for ( int i = 0; i < vals.length; ++i )
			vals[ i ] = ( int ) Math.round( dvals[ i ] );

		// Relaxation run-test for Paul and Bogdan
		// - - - - - - - - - - - - - - - - - - - -
//		System.out.println( ">> Relaxing problem..." );
//		final GRBModel r = model.relax();
//		System.out.println( ">> Solving relaxed problem..." );
//		r.optimize();
//		System.out.println( ">> Counting integral variables..." );
//		int integral = 0;
//		int matching = 0;
//		final int numvars = r.getVars().length;
//		for ( int idx = 0; idx < r.getVars().length; idx++ ) {
//			final GRBVar var = model.getVars()[ idx ];
//			final GRBVar varRelaxed = r.getVars()[ idx ];
//			final double x = var.get( GRB.DoubleAttr.X );
//			final double xRelaxed = varRelaxed.get( GRB.DoubleAttr.X );
//			if ( xRelaxed == 0.0 || xRelaxed == 1.0 ) integral++;
//			if ( x == xRelaxed ) matching++;
//		}
//		System.out.println( String.format( ">> %d, %d, %d", numvars, integral, matching ) );

		return new GurobiResult( variableToIndex, vals, model );
	}

	/**
	 * Calls gurobi's write method with the given filename.
	 * Note: you must have solved a model prior to calling this function.
	 *
	 * @param filename
	 */
	public void saveLatestModel( final String filename ) {
		saveModel( model, filename );
	}

	public static void saveModel( final GRBModel model, final String filename ) {
		try {
			if ( model != null ) model.write( filename );
		} catch ( final GRBException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the energy corresponding to the latest computed solution.
	 *
	 * @return returns latest computed energy, or <code>Double.NaN</code> if not
	 *         applicable.
	 */
	public double getLatestEnergy() {
		try {
			return model.get( GRB.DoubleAttr.ObjVal );
		} catch ( final GRBException e ) {
			return Double.NaN;
		}
	}

	public void dispose() throws GRBException {
		if ( env == null ) return;
		env.dispose();
		env = null;
	}

	private void createEnvIfNecessary() throws GRBException {
		if ( env == null ) env = new GRBEnv( getNewLogFilename() );
	}

	private String getNewLogFilename() {
//		final String fnFormat = ( ( logFileDirectory != null && logFileDirectory.exists() ) ? logFileDirectory.getAbsolutePath() : "." ) + "/grb_%04d.log";
//		for ( int i = 1; true; ++i ) {
//			final String fn = String.format( fnFormat, i );
//			if ( !new File( fn ).exists() ) { return fn; }
//		}
		return ( ( logFileDirectory != null && logFileDirectory.exists() ) ? logFileDirectory.getAbsolutePath() : "." ) + "/indago_gurobi.log";
	}

	public static class GurobiResult implements Assignment< Variable > {

		private final TObjectIntMap< Variable > variableToIndex;

		private final int[] vals;

		private final GRBModel GRBModel;

		public GurobiResult(
				final TObjectIntMap< Variable > variableToIndex,
				final int[] vals,
				final GRBModel model ) {
			this.variableToIndex = variableToIndex;
			this.vals = vals;
			this.GRBModel = model;
		}

		@Override
		public boolean isAssigned( final Variable var ) {
			return variableToIndex.containsKey( var );
		}

		@Override
		public int getAssignment( final Variable var ) {
			return vals[ variableToIndex.get( var ) ];
		}

		/**
		 * @return
		 */
		public GRBModel getModel() {
			return GRBModel;
		}

		/**
		 * @return the variableToIndex
		 */
		public TObjectIntMap< Variable > getVariableToIndex() {
			return variableToIndex;
		}
	}

	/*
	 * Helpers.
	 */

	/**
	 * Get the Gurobi constant corresponding to the given
	 * {@link Relation}.
	 *
	 * @param relation
	 * @return Gurobi constant corresponding to {@code relation}
	 */
	public static char relationToGRB( final Relation relation )
	{
		switch( relation )
		{
		default:
		case EQ:
			return GRB.EQUAL;
		case GE:
			return GRB.GREATER_EQUAL;
		case LE:
			return GRB.LESS_EQUAL;
		}
	}
}
