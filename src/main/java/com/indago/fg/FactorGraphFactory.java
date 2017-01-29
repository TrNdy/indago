/**
 *
 */
package com.indago.fg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.indago.IndagoLog;
import com.indago.pg.IndicatorNode;
import com.indago.pg.SegmentationProblem;
import com.indago.pg.TrackingProblem;
import com.indago.pg.assignments.AppearanceHypothesis;
import com.indago.pg.assignments.AssignmentNode;
import com.indago.pg.assignments.DisappearanceHypothesis;
import com.indago.pg.assignments.DivisionHypothesis;
import com.indago.pg.assignments.MovementHypothesis;
import com.indago.pg.segments.ConflictSet;
import com.indago.pg.segments.SegmentNode;

import indago.ui.progress.ProgressListener;

/**
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 * @author Florian Jug &lt;jug@mpi-cbg.de&gt;
 */
public class FactorGraphFactory {

	public static MappedFactorGraph createFactorGraph( final SegmentationProblem segmentationModel ) {

		final Map< IndicatorNode, Variable > varmap = new HashMap< >();

		final ArrayList< Factor > unaries = new ArrayList<>();
		final ArrayList< Factor > constraints = new ArrayList<>();

		for ( final SegmentNode segvar : segmentationModel.getSegments() ) {
			final Variable var = Variables.binary();
			varmap.put( segvar, var );
			unaries.add( Factors.unary( var, 0.0, segvar.getCost() ) );
		}

		// CONFLICT CONSTRAINTS
		for ( final ConflictSet conflictSet : segmentationModel.getConflictSets() ) {
			final ArrayList< Variable > vars = new ArrayList<>();
			for ( final SegmentNode segvar : conflictSet )
				vars.add( varmap.get( segvar ) );
			constraints.add( Factors.atMostOneConstraint( vars ) );
		}

		// FORCED AND AVOIDED SEGMENT NODES
		for ( final SegmentNode forcedNode : segmentationModel.getForcedNodes() ) {
			IndagoLog.log.info( "Consider forced node: " + forcedNode.toString() );
			final ArrayList< Variable > vars = new ArrayList<>();
			vars.add( varmap.get( forcedNode ) );
			constraints.add( Factors.equalOneConstraint( vars ) );
		}
		for ( final SegmentNode avoidedNode : segmentationModel.getAvoidedNodes() ) {
			IndagoLog.log.info( "Consider avoided node: " + avoidedNode.toString() );
			final ArrayList< Variable > vars = new ArrayList<>();
			vars.add( varmap.get( avoidedNode ) );
			constraints.add( Factors.equalZeroConstraint( vars ) );
		}

		final Collection< Variable > variables = varmap.values();
		final UnaryCostConstraintGraph fg = new UnaryCostConstraintGraph( variables, unaries, constraints );
		final AssignmentMapper< Variable, IndicatorNode > mapper =
				new AssignmentMapper< Variable, IndicatorNode >() {
			@Override
			public Assignment< IndicatorNode > map( final Assignment< ? super Variable > assignment ) {
						return new Assignment< IndicatorNode >() {
					@Override
					public boolean isAssigned( final IndicatorNode variable ) {
						return assignment.isAssigned( varmap.get( variable ) );
					}

					@Override
					public int getAssignment( final IndicatorNode variable ) {
						return assignment.getAssignment( varmap.get( variable ) );
					}
				};
			}
		};

		return new MappedFactorGraph( fg, varmap, mapper );
	}

	public static MappedFactorGraph createFactorGraph( final TrackingProblem trackingModel, final List< ProgressListener > progressListeners ) {

		for ( final ProgressListener progressListener : progressListeners ) {
			progressListener.resetProgress( "Building tracking factor graph (FG)...", 4 * trackingModel.getTimepoints().size() );
		}

		final Map< IndicatorNode, Variable > varmap = new HashMap< >();

		final ArrayList< Variable > variables = new ArrayList< >();
		final ArrayList< Factor > unaries = new ArrayList< >();
		final ArrayList< Factor > constraints = new ArrayList< >();

		// Create FGs for SegmentationModels (timepoints)
		for ( final SegmentationProblem frameModel : trackingModel.getTimepoints() ) {
			final MappedFactorGraph frameMFG = FactorGraphFactory.createFactorGraph( frameModel );

			IndagoLog.log.info(
					"#vars/#unaries/#constraints = " +
							frameMFG.getVarmap().keySet().size() + "/" +
							frameMFG.getFg().getUnaries().size() + "/" +
							frameMFG.getFg().getConstraints().size() );

			// copy generated FG components here
			// note: variables get collected below, after connecting frames
			for ( final IndicatorNode segvar : frameMFG.getVarmap().keySet() ) {
				varmap.put( segvar, frameMFG.getVarmap().get( segvar ) );
			}
			unaries.addAll( frameMFG.getFg().getUnaries() );
			constraints.addAll( frameMFG.getFg().getConstraints() );

			for ( final ProgressListener progressListener : progressListeners ) {
				progressListener.hasProgressed();
			}
		}

		// Connect timepoints as described in given model graph
		for ( int frameId = 0; frameId < trackingModel.getTimepoints().size(); frameId++ ) {
			final SegmentationProblem frameOneSegModel = trackingModel.getTimepoints().get( frameId );

			for ( final SegmentNode segVar : frameOneSegModel.getSegments() ) {

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

			for ( final ProgressListener progressListener : progressListeners ) {
				progressListener.hasProgressed();
			}
		}

		// Add continuation constraints (sum left NH = sum right NH = value segVar)
		for ( int frameId = 0; frameId < trackingModel.getTimepoints().size(); frameId++ ) {
			final SegmentationProblem frameModel = trackingModel.getTimepoints().get( frameId );

			for ( final SegmentNode segVar : frameModel.getSegments() ) {
				final List< Variable > varsToConnect1 = new ArrayList< Variable >();
				varsToConnect1.add( varmap.get( segVar ) );
				for ( final AssignmentNode assVar : segVar.getInAssignments().getAllAssignments() ) {
					varsToConnect1.add( varmap.get( assVar ) );
				}
				constraints.add( Factors.firstExactlyWithOneOtherOrNoneConstraint( varsToConnect1 ) );

				final List< Variable > varsToConnect2 = new ArrayList< Variable >();
				varsToConnect2.add( varmap.get( segVar ) );
				for ( final AssignmentNode assVar : segVar.getOutAssignments().getAllAssignments() ) {
					varsToConnect2.add( varmap.get( assVar ) );
				}
				constraints.add( Factors.firstExactlyWithOneOtherOrNoneConstraint( varsToConnect2 ) );
			}

			for ( final ProgressListener progressListener : progressListeners ) {
				progressListener.hasProgressed();
			}
		}

		// Collect FG variables
		for ( final Variable var : varmap.values() ) {
			variables.add( var );
		}

		// ADD USER CONSTRAINTS (segments forced by specific assignment types)
		for ( int frameId = 0; frameId < trackingModel.getTimepoints().size(); frameId++ ) {
			final SegmentationProblem frameModel = trackingModel.getTimepoints().get( frameId );

			for ( final SegmentNode forcedNode : frameModel.getForcedByAppearanceNodes() ) {
				IndagoLog.log.info( "Consider appearance force for: " + forcedNode.toString() );
				final ArrayList< Variable > vars = new ArrayList<>();
				for ( final AppearanceHypothesis app : forcedNode.getInAssignments().getAppearances() ) {
					vars.add( varmap.get( app ) );
				}
				if ( vars.size() > 0 ) constraints.add( Factors.equalOneConstraint( vars ) );
				else
					IndagoLog.log.warn( "Appearance cannot be forced. (Missing assignments in assignment pool?)" );
			}
			for ( final SegmentNode forcedNode : frameModel.getForcedByDisappearanceNodes() ) {
				IndagoLog.log.info( "Consider disppearance force for: " + forcedNode.toString() );
				final ArrayList< Variable > vars = new ArrayList<>();
				for ( final DisappearanceHypothesis disapp : forcedNode.getOutAssignments().getDisappearances() ) {
					vars.add( varmap.get( disapp ) );
				}
				if ( vars.size() > 0 ) constraints.add( Factors.equalOneConstraint( vars ) );
				else
					IndagoLog.log.warn( "Disappearance cannot be forced. (Missing assignments in assignment pool?)" );
			}
			for ( final SegmentNode forcedNode : frameModel.getForcedSegmentNodeMovesTo() ) {
				IndagoLog.log.info( "Consider incoming movement force for node: " + forcedNode.toString() );
				final ArrayList< Variable > vars = new ArrayList<>();
				for ( final MovementHypothesis move : forcedNode.getInAssignments().getMoves() ) {
					vars.add( varmap.get( move ) );
				}
				if ( vars.size() > 0 ) {
					constraints.add( Factors.equalOneConstraint( vars ) );
				} else {
					IndagoLog.log.warn( ">> Movement cannot be forced. (Missing assignments in assignment pool?)" );
				}
			}
			for ( final SegmentNode forcedNode : frameModel.getForcedSegmentNodeDivisionsTo() ) {
				IndagoLog.log.info( "Consider incoming division force for node: " + forcedNode.toString() );
				final ArrayList< Variable > vars = new ArrayList<>();
				for ( final DivisionHypothesis division : forcedNode.getInAssignments().getDivisions() ) {
					vars.add( varmap.get( division ) );
				}
				if ( vars.size() > 0 ) {
					constraints.add( Factors.equalOneConstraint( vars ) );
				} else {
					IndagoLog.log.warn( ">> Division cannot be forced. (Missing assignments in assignment pool?)" );
				}
			}
			for ( final SegmentNode forcedNode : frameModel.getForcedSegmentNodeMovesFrom() ) {
				IndagoLog.log.info( "Consider outgoing movement force for node: " + forcedNode.toString() );
				final ArrayList< Variable > vars = new ArrayList<>();
				for ( final MovementHypothesis move : forcedNode.getOutAssignments().getMoves() ) {
					vars.add( varmap.get( move ) );
				}
				if ( vars.size() > 0 ) {
					constraints.add( Factors.equalOneConstraint( vars ) );
				} else {
					IndagoLog.log.warn( ">> Movement cannot be forced. (Missing assignments in assignment pool?)" );
				}
			}
			for ( final SegmentNode forcedNode : frameModel.getForcedSegmentNodeDivisionsFrom() ) {
				IndagoLog.log.info( "Consider outgoing division force for node: " + forcedNode.toString() );
				final ArrayList< Variable > vars = new ArrayList<>();
				for ( final DivisionHypothesis division : forcedNode.getOutAssignments().getDivisions() ) {
					vars.add( varmap.get( division ) );
				}
				if ( vars.size() > 0 ) {
					constraints.add( Factors.equalOneConstraint( vars ) );
				} else {
					IndagoLog.log.warn( ">> Division cannot be forced. (Missing assignments in assignment pool?)" );
				}
			}
			for ( final ConflictSet set : frameModel.getForcedConflictSetMovesTo() ) {
				IndagoLog.log.info( "Consider incoming movement force for set: " + set.toString() );
				final ArrayList< Variable > vars = new ArrayList<>();
				for ( final SegmentNode node : set ) {
					for ( final MovementHypothesis move : node.getInAssignments().getMoves() ) {
						vars.add( varmap.get( move ) );
					}
				}
				if ( vars.size() > 0 ) {
					constraints.add( Factors.equalOneConstraint( vars ) );
				} else {
					IndagoLog.log.warn( ">> Movement for set cannot be forced. (Missing assignments in assignment pool?)" );
				}
			}
			for ( final ConflictSet set : frameModel.getForcedConflictSetMovesFrom() ) {
				IndagoLog.log.info( "Consider outgoing movement force for set: " + set.toString() );
				final ArrayList< Variable > vars = new ArrayList<>();
				for ( final SegmentNode node : set ) {
					for ( final MovementHypothesis move : node.getOutAssignments().getMoves() ) {
						vars.add( varmap.get( move ) );
					}
				}
				if ( vars.size() > 0 ) {
					constraints.add( Factors.equalOneConstraint( vars ) );
				} else {
					IndagoLog.log.warn( ">> Movement for set cannot be forced. (Missing assignments in assignment pool?)" );
				}
			}
			for ( final ConflictSet set : frameModel.getForcedConflictSetDivisionsTo() ) {
				IndagoLog.log.info( "Consider incoming division force for set: " + set.toString() );
				final ArrayList< Variable > vars = new ArrayList<>();
				for ( final SegmentNode node : set ) {
					for ( final DivisionHypothesis div : node.getInAssignments().getDivisions() ) {
						vars.add( varmap.get( div ) );
					}
				}
				if ( vars.size() > 0 ) {
					constraints.add( Factors.equalOneConstraint( vars ) );
				} else {
					IndagoLog.log.warn( ">> Movement for set cannot be forced. (Missing assignments in assignment pool?)" );
				}
			}
			for ( final ConflictSet set : frameModel.getForcedConflictSetDivisionsFrom() ) {
				IndagoLog.log.info( "Consider outgoing division force for set: " + set.toString() );
				final ArrayList< Variable > vars = new ArrayList<>();
				for ( final SegmentNode node : set ) {
					for ( final DivisionHypothesis div : node.getOutAssignments().getDivisions() ) {
						vars.add( varmap.get( div ) );
					}
				}
				if ( vars.size() > 0 ) {
					constraints.add( Factors.equalOneConstraint( vars ) );
				} else {
					IndagoLog.log.warn( ">> Movement for set cannot be forced. (Missing assignments in assignment pool?)" );
				}
			}

			for ( final ProgressListener progressListener : progressListeners ) {
				progressListener.hasProgressed();
			}
		}

		final UnaryCostConstraintGraph fg = new UnaryCostConstraintGraph( variables, unaries, constraints );
		final AssignmentMapper< Variable, IndicatorNode > mapper = new AssignmentMapper< Variable, IndicatorNode >() {
			@Override
			public Assignment< IndicatorNode > map( final Assignment< ? super Variable > assignment ) {
				return new Assignment< IndicatorNode >() {

					@Override
					public boolean isAssigned( final IndicatorNode variable ) {
						return assignment.isAssigned( varmap.get( variable ) );
					}

					@Override
					public int getAssignment( final IndicatorNode variable ) {
						return assignment.getAssignment( varmap.get( variable ) );
					}
				};
			}
		};

		return new MappedFactorGraph( fg, varmap, mapper );
	}
}
