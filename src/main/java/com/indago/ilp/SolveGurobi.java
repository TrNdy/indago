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
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public class SolveGurobi {

	public static File defaultLogFileDirectory = new File( "." );

	private final File logFileDirectory;

	private GRBEnv env;

	public SolveGurobi() throws GRBException {
		this( defaultLogFileDirectory );
	}

	public SolveGurobi( final File logFileDirectory ) {
		this.logFileDirectory = logFileDirectory;
		env = null;
	}

	public static Assignment< Variable > staticSolve( final UnaryCostConstraintGraph fg ) throws GRBException {
		final SolveGurobi solver = new SolveGurobi();
		final Assignment< Variable > assignment = solver.solve( fg );
		solver.dispose();
		return assignment;
	}

	public Assignment< Variable > solve( final UnaryCostConstraintGraph fg ) throws GRBException {
		final Collection< Variable > variables = fg.getVariables();
		final Collection< Factor > unaries = fg.getUnaries();
		final Collection< Factor > constraints = fg.getConstraints();

		final TObjectIntMap< Variable > variableToIndex = new TObjectIntHashMap<>();
		int variableIndex = 0;
		for ( final Variable v : variables )
			variableToIndex.put( v, variableIndex++ );

		createEnvIfNecessary();
		final GRBModel model = new GRBModel( env );

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
		env.message( "Obj: " + model.get( GRB.DoubleAttr.ObjVal ) );

		final double[] dvals = model.get( GRB.DoubleAttr.X, vars );
		final int[] vals = new int[ dvals.length ];
		for ( int i = 0; i < vals.length; ++i )
			vals[ i ] = ( int ) Math.round( dvals[ i ] );

		// Dispose of model
		model.dispose();

		return new GurobiAssignment( variableToIndex, vals );
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
		final String fnFormat = ( ( logFileDirectory != null && logFileDirectory.exists() ) ? logFileDirectory.getAbsolutePath() : "." ) + "/grb_%04d.log";
		for ( int i = 1; true; ++i ) {
			final String fn = String.format( fnFormat, i );
			if ( !new File( fn ).exists() ) { return fn; }
		}
	}

	private static class GurobiAssignment implements Assignment< Variable > {

		private final TObjectIntMap< Variable > variableToIndex;

		private final int[] vals;

		public GurobiAssignment(
				final TObjectIntMap< Variable > variableToIndex,
				final int[] vals ) {
			this.variableToIndex = variableToIndex;
			this.vals = vals;
		}

		@Override
		public boolean isAssigned( final Variable var ) {
			return variableToIndex.containsKey( var );
		}

		@Override
		public int getAssignment( final Variable var ) {
			return vals[ variableToIndex.get( var ) ];
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
