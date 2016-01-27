/**
 *
 */
package com.indago.fg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.indago.models.IndicatorVar;
import com.indago.models.SegmentationModel;
import com.indago.models.TrackingModel;
import com.indago.models.assignments.AppearanceHypothesis;
import com.indago.models.assignments.AssignmentVar;
import com.indago.models.assignments.DisappearanceHypothesis;
import com.indago.models.assignments.DivisionHypothesis;
import com.indago.models.assignments.MovementHypothesis;
import com.indago.models.segments.ConflictSet;
import com.indago.models.segments.SegmentVar;

/**
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class FactorGraphFactory {

	public static MappedFactorGraph createFactorGraph( final SegmentationModel segmentationModel ) {

		final Map< IndicatorVar, Variable > varmap = new HashMap< >();

		final Collection< Variable > variables = varmap.values();
		final ArrayList< Factor > unaries = new ArrayList<>();
		final ArrayList< Factor > constraints = new ArrayList<>();

		for ( final SegmentVar segvar : segmentationModel.getSegments() ) {
			final Variable var = Variables.binary();
			varmap.put( segvar, var );
			unaries.add( Factors.unary( var, 0.0, segvar.getCost() ) );
		}

		for ( final ConflictSet conflictSet : segmentationModel.getConflictSets() ) {
			final ArrayList< Variable > vars = new ArrayList<>();
			for ( final SegmentVar segvar : conflictSet )
				vars.add( varmap.get( segvar ) );
			constraints.add( Factors.atMostOneConstraint( vars ) );
		}

		final UnaryCostConstraintGraph fg = new UnaryCostConstraintGraph( variables, unaries, constraints );
		final AssignmentMapper< Variable, IndicatorVar > mapper =
				new AssignmentMapper< Variable, IndicatorVar >() {
			@Override
			public Assignment< IndicatorVar > map( final Assignment< ? super Variable > assignment ) {
						return new Assignment< IndicatorVar >() {
					@Override
					public boolean isAssigned( final IndicatorVar variable ) {
						return assignment.isAssigned( varmap.get( variable ) );
					}

					@Override
					public int getAssignment( final IndicatorVar variable ) {
						return assignment.getAssignment( varmap.get( variable ) );
					}
				};
			}
		};

		return new MappedFactorGraph( fg, varmap, mapper );
	}

	public static MappedFactorGraph createFactorGraph( final TrackingModel trackingModel ) {

		final Map< IndicatorVar, Variable > varmap = new HashMap< >();

		final ArrayList< Variable > variables = new ArrayList< >();
		final ArrayList< Factor > unaries = new ArrayList< >();
		final ArrayList< Factor > constraints = new ArrayList< >();

		// Create FGs for SegmentationModels (timepoints)
		for ( final SegmentationModel frameModel : trackingModel.getTimepoints() ) {
			final MappedFactorGraph frameMFG = FactorGraphFactory.createFactorGraph( frameModel );

			// copy generated FG components here
			// note: variables get collected below, after connections frames
			for ( final IndicatorVar segvar : frameMFG.getVarmap().keySet() ) {
				varmap.put( segvar, frameMFG.getVarmap().get( segvar ) );
			}
			unaries.addAll( frameMFG.getFg().getUnaries() );
			constraints.addAll( frameMFG.getFg().getConstraints() );
		}

		// Connect timepoints as described in given model graph
		for ( int frameId = 0; frameId < trackingModel.getTimepoints().size(); frameId++ ) {
			final SegmentationModel frameOneSegModel = trackingModel.getTimepoints().get( frameId );

			for ( final SegmentVar segVar : frameOneSegModel.getSegments() ) {

				for ( final AppearanceHypothesis app : segVar.getInAssignments().getAppearances() ) {
					final Variable toFgVar = varmap.get( app.getDest() );
					final Variable appvar = Variables.binary();
					varmap.put( app, appvar );
					unaries.add( Factors.unary( appvar, 0.0, app.getCost() ) );
					constraints.add( Factors.firstImpliesAtLeastOneOtherConstraint( appvar, toFgVar ) );
				}

				for ( final DisappearanceHypothesis disapp : segVar.getOutAssignments().getDisappearances() ) {
					final Variable fromFgVar = varmap.get( disapp.getSrc() );
					final Variable disappvar = Variables.binary();
					varmap.put( disapp, disappvar );
					unaries.add( Factors.unary( disappvar, 0.0, disapp.getCost() ) );
					constraints.add( Factors.firstImpliesAtLeastOneOtherConstraint( disappvar, fromFgVar ) );
				}

				for ( final MovementHypothesis move : segVar.getInAssignments().getMoves() ) {
					final Variable fromFgVar = varmap.get( move.getSrc() );
					final Variable toFgVar = varmap.get( move.getDest() );
					final Variable movevar = Variables.binary();
					varmap.put( move, movevar );
					unaries.add( Factors.unary( movevar, 0.0, move.getCost() ) );
					constraints.add( Factors.firstImpliesAtLeastOneOtherConstraint( movevar, fromFgVar, toFgVar ) );
				}

				for ( final DivisionHypothesis div : segVar.getInAssignments().getDivisions() ) {
					final Variable fromFgVar = varmap.get( div.getSrc() );
					final Variable toFgVar1 = varmap.get( div.getDest1() );
					final Variable toFgVar2 = varmap.get( div.getDest2() );
					final Variable divvar = Variables.binary();
					varmap.put( div, divvar );
					unaries.add( Factors.unary( divvar, 0.0, div.getCost() ) );
					constraints.add( Factors.firstImpliesAtLeastOneOtherConstraint( divvar, fromFgVar, toFgVar1, toFgVar2 ) );
				}
			}
			// System.out.println( "FRAME " + frameId + " WORKED THROUGH" );
		}

		// Add continuation constraints (sum left NH = sum right NH = value segVar)
		for ( int frameId = 0; frameId < trackingModel.getTimepoints().size(); frameId++ ) {
			final SegmentationModel frameModel = trackingModel.getTimepoints().get( frameId );

			for ( final SegmentVar segVar : frameModel.getSegments() ) {
				final List< Variable > varsToConnect1 = new ArrayList< Variable >();
				for ( final AssignmentVar assVar : segVar.getInAssignments().getAllAssignments() ) {
					varsToConnect1.add( varmap.get( assVar ) );
				}
				constraints.add( Factors.firstExactlyWithOneOtherOrNoneConstraint( varsToConnect1 ) );

				final List< Variable > varsToConnect2 = new ArrayList< Variable >();
				for ( final AssignmentVar assVar : segVar.getOutAssignments().getAllAssignments() ) {
					varsToConnect2.add( varmap.get( assVar ) );
				}
				constraints.add( Factors.firstExactlyWithOneOtherOrNoneConstraint( varsToConnect2 ) );
			}
		}

		// Collect FG variables
		for ( final Variable var : varmap.values() ) {
			variables.add( var );
		}

		final UnaryCostConstraintGraph fg = new UnaryCostConstraintGraph( variables, unaries, constraints );
		final AssignmentMapper< Variable, IndicatorVar > mapper = new AssignmentMapper< Variable, IndicatorVar >() {
			@Override
			public Assignment< IndicatorVar > map( final Assignment< ? super Variable > assignment ) {
				return new Assignment< IndicatorVar >() {

					@Override
					public boolean isAssigned( final IndicatorVar variable ) {
						return assignment.isAssigned( varmap.get( variable ) );
					}

					@Override
					public int getAssignment( final IndicatorVar variable ) {
						return assignment.getAssignment( varmap.get( variable ) );
					}
				};
			}
		};

		return new MappedFactorGraph( fg, varmap, mapper );
	}
}
