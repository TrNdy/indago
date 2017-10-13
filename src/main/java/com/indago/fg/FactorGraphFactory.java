/**
 *
 */
package com.indago.fg;

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
import com.indago.util.BimapOneToMany;
import indago.ui.progress.ProgressListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Tobias Pietzsch
 * @author Florian Jug
 */
public class FactorGraphFactory {

	public static MappedFactorGraph createFactorGraph( final SegmentationProblem segmentationModel ) {

		final BimapOneToMany< IndicatorNode, Variable > varmap = new BimapOneToMany<>();

		final ArrayList< Factor > unaries = new ArrayList<>();
		final ArrayList< Factor > constraints = new ArrayList<>();

		for ( final SegmentNode segNode : segmentationModel.getSegments() ) {
			for ( int delta = 0; delta <= segNode.getMaxDelta(); ++delta ) {
				final Variable var = Variables.binary();
				varmap.add( segNode, var );
				unaries.add( Factors.unary( var, 0.0, segNode.getCost() ) );
			}
		}

		// UNIQUE DELTA CONSTRAINTS
		for ( final SegmentNode segNode : segmentationModel.getSegments() ) {
			constraints.add( Factors.atMostOneConstraint( varmap.getBs( segNode ) ) );
		}

		// CONFLICT CONSTRAINTS
		for ( final ConflictSet conflictSet : segmentationModel.getConflictSets() ) {
			final ArrayList< Variable > vars = new ArrayList<>();
			for ( final SegmentNode segvar : conflictSet )
				vars.addAll( varmap.getBs( segvar ) );
			constraints.add( Factors.atMostOneConstraint( vars ) );
		}

		// FORCED AND AVOIDED SEGMENT NODES
		for ( final SegmentNode forcedNode : segmentationModel.getForcedNodes() ) {
			IndagoLog.log.info( "Consider forced node: " + forcedNode.toString() );
			final ArrayList< Variable > vars = new ArrayList<>();
			vars.addAll( varmap.getBs( forcedNode ) );
			constraints.add( Factors.equalOneConstraint( vars ) );
		}
		for ( final SegmentNode avoidedNode : segmentationModel.getAvoidedNodes() ) {
			IndagoLog.log.info( "Consider avoided node: " + avoidedNode.toString() );
			final ArrayList< Variable > vars = new ArrayList<>();
			vars.addAll( varmap.getBs( avoidedNode ) );
			constraints.add( Factors.equalZeroConstraint( vars ) );
		}

		final Collection< Variable > variables = varmap.valuesBs();
		final UnaryCostConstraintGraph fg = new UnaryCostConstraintGraph( variables, unaries, constraints );
		final AssignmentMapper< Variable, IndicatorNode > mapper =
				new AssignmentMapper< Variable, IndicatorNode >() {
			@Override
			public Assignment< IndicatorNode > map( final Assignment< ? super Variable > assignment ) {
						return new Assignment< IndicatorNode >() {
					@Override
					public boolean isAssigned( final IndicatorNode variable ) {
								return assignment.isAssigned( varmap.getBs( variable ) );
					}

					@Override
					public int getAssignment( final IndicatorNode variable ) {
								return assignment.getAssignment( varmap.getB( variable, 0 ) );
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

		final BimapOneToMany< IndicatorNode, Variable > varmap = new BimapOneToMany<>();

		final ArrayList< Variable > variables = new ArrayList< >();
		final ArrayList< Factor > unaries = new ArrayList< >();
		final ArrayList< Factor > constraints = new ArrayList< >();

		// Create FGs for SegmentationModels (timepoints)
		for ( final SegmentationProblem frameModel : trackingModel.getTimepoints() ) {
			final MappedFactorGraph frameMFG = FactorGraphFactory.createFactorGraph( frameModel );

			IndagoLog.log.info(
					"#vars/#unaries/#constraints = " +
							frameMFG.getVarmap().valuesAs().size() + "/" +
							frameMFG.getFg().getUnaries().size() + "/" +
							frameMFG.getFg().getConstraints().size() );

			// copy generated FG components here
			// note: variables get collected below, after connecting frames
			for ( final IndicatorNode segvar : frameMFG.getVarmap().valuesAs() ) {
				varmap.addAll( segvar, frameMFG.getVarmap().getBs( segvar ) );
			}
			unaries.addAll( frameMFG.getFg().getUnaries() );
			constraints.addAll( frameMFG.getFg().getConstraints() );

			for ( final ProgressListener progressListener : progressListeners ) {
				progressListener.hasProgressed();
			}
		}

		// Connect timepoints as described in given model graph
		for ( int frameId = 0; frameId < trackingModel.getTimepoints().size(); frameId++ ) {
			final SegmentationProblem frameModel = trackingModel.getTimepoints().get( frameId );

			for ( final SegmentNode segNode : frameModel.getSegments() ) {

				for ( final AppearanceHypothesis app : segNode.getInAssignments().getAppearances() ) {
					final Variable appvar = Variables.binary();
					varmap.add( app, appvar );
					unaries.add( Factors.unary( appvar, 0.0, app.getCost() ) );
				}

				for ( final MovementHypothesis move : segNode.getOutAssignments().getMoves() ) {
					for ( int delta = 0; delta <= segNode.getMaxDelta(); ++delta ) {
						final Variable movevar = Variables.binary();
						varmap.add( move, movevar );
						unaries.add( Factors.unary( movevar, 0.0, move.getCost() ) );
					}
				}

				for ( final DivisionHypothesis div : segNode.getOutAssignments().getDivisions() ) {
					final Variable divvar = Variables.binary();
					varmap.add( div, divvar );
					unaries.add( Factors.unary( divvar, 0.0, div.getCost() ) );
				}

				for ( final DisappearanceHypothesis disapp : segNode.getOutAssignments().getDisappearances() ) {
					for ( int delta = 0; delta <= segNode.getMaxDelta(); ++delta ) {
						final Variable disappvar = Variables.binary();
						varmap.add( disapp, disappvar );
						unaries.add( Factors.unary( disappvar, 0.0, disapp.getCost() ) );
					}
				}
			}

			for ( final ProgressListener progressListener : progressListeners ) {
				progressListener.hasProgressed();
			}
		}

		// Add continuation constraints (sum left NH = sum right NH = value segNode)
		for ( int frameId = 0; frameId < trackingModel.getTimepoints().size(); frameId++ ) {
			final SegmentationProblem frameModel = trackingModel.getTimepoints().get( frameId );

			for ( final SegmentNode segNode : frameModel.getSegments() ) {
				int maxDelta = segNode.getMaxDelta();

				// -- left neighborhood --
				for ( int delta = 0; delta <= maxDelta; ++delta ) {
					final List< Variable > varsLeft = new ArrayList<>();
					varsLeft.add( varmap.getB( segNode, delta ) );

					// -- Appearance --
					if ( delta == maxDelta ) {
						for ( final AssignmentNode assNode : segNode.getInAssignments().getAppearances() ) {
							varsLeft.add( varmap.getB( assNode ) );
						}
					}

					// -- Move --
					if ( delta > 0 && delta < maxDelta ) {
						for ( MovementHypothesis move : segNode.getInAssignments().getMoves() ) {
							int fromMaxDelta = move.getSrc().getMaxDelta();
							if ( delta - 1 < fromMaxDelta )
								varsLeft.add( varmap.getB( move, delta - 1 ) );
						}
					}
					if ( delta == maxDelta ) {
						for ( MovementHypothesis move : segNode.getInAssignments().getMoves() ) {
							int fromMaxDelta = move.getSrc().getMaxDelta();
							int fromMinDelta = Math.min( Math.max( 0, delta - 1 ), fromMaxDelta );
							for ( int fromDelta = fromMinDelta; fromDelta <= fromMaxDelta; ++fromDelta )
								varsLeft.add( varmap.getB( move, fromDelta ) );
						}
					}

					// -- Division --
					if ( delta == 0 ) {
						for ( final AssignmentNode assNode : segNode.getInAssignments().getDivisions() ) {
							varsLeft.add( varmap.getB( assNode ) );
						}
					}

					// -- Disappearance --
					// do not exist in left neighborhood

					constraints.add( Factors.firstEqualsSumOfOthersConstraint( varsLeft ) );
				}

				// -- right neighborhood --
				for ( int delta = 0; delta <= maxDelta; ++delta ) {
					final List< Variable > varsRight = new ArrayList< Variable >();
					varsRight.add( varmap.getB( segNode, delta ) );

					// -- Appearance --
					// does not exist in right neighborhood

					// -- Move --
					for ( final AssignmentNode assNode : segNode.getOutAssignments().getMoves() ) {
						varsRight.add( varmap.getB( assNode, delta ) );
					}

					// -- Division --
					if ( delta == maxDelta ) {
						for ( final AssignmentNode assNode : segNode.getOutAssignments().getDivisions() ) {
							varsRight.add( varmap.getB( assNode ) );
						}
					}

					// -- Disappearance --
					for ( final AssignmentNode assNode : segNode.getOutAssignments().getDisappearances() ) {
						varsRight.add( varmap.getB( assNode, delta ) );
					}

					constraints.add( Factors.firstEqualsSumOfOthersConstraint( varsRight ) );
				}
			}

			for ( final ProgressListener progressListener : progressListeners ) {
				progressListener.hasProgressed();
			}
		}

		// Collect FG variables
		for ( final Variable var : varmap.valuesBs() ) {
			variables.add( var );
		}

		// ADD USER CONSTRAINTS (segments forced by specific assignment types)
		for ( int frameId = 0; frameId < trackingModel.getTimepoints().size(); frameId++ ) {
			final SegmentationProblem frameModel = trackingModel.getTimepoints().get( frameId );

			for ( final SegmentNode forcedNode : frameModel.getForcedByAppearanceNodes() ) {
				IndagoLog.log.info( "Consider appearance force for: " + forcedNode.toString() );
				final ArrayList< Variable > vars = new ArrayList<>();
				for ( final AppearanceHypothesis app : forcedNode.getInAssignments().getAppearances() ) {
					vars.add( varmap.getB( app ) );
				}
				if ( vars.size() > 0 ) constraints.add( Factors.equalOneConstraint( vars ) );
				else
					IndagoLog.log.warn( "Appearance cannot be forced. (Missing assignments in assignment pool?)" );
			}
			for ( final SegmentNode forcedNode : frameModel.getForcedByDisappearanceNodes() ) {
				IndagoLog.log.info( "Consider disppearance force for: " + forcedNode.toString() );
				final ArrayList< Variable > vars = new ArrayList<>();
				for ( final DisappearanceHypothesis disapp : forcedNode.getOutAssignments().getDisappearances() ) {
					vars.add( varmap.getB( disapp ) );
				}
				if ( vars.size() > 0 ) constraints.add( Factors.equalOneConstraint( vars ) );
				else
					IndagoLog.log.warn( "Disappearance cannot be forced. (Missing assignments in assignment pool?)" );
			}
			for ( final SegmentNode forcedNode : frameModel.getForcedSegmentNodeMovesTo() ) {
				IndagoLog.log.info( "Consider incoming movement force for node: " + forcedNode.toString() );
				final ArrayList< Variable > vars = new ArrayList<>();
				for ( final MovementHypothesis move : forcedNode.getInAssignments().getMoves() ) {
					vars.add( varmap.getB( move ) );
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
					vars.add( varmap.getB( division ) );
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
					vars.add( varmap.getB( move ) );
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
					vars.add( varmap.getB( division ) );
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
						vars.add( varmap.getB( move ) );
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
						vars.add( varmap.getB( move ) );
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
						vars.add( varmap.getB( div ) );
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
						vars.add( varmap.getB( div ) );
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
						return assignment.isAssigned( varmap.getB( variable ) );
					}

					@Override
					public int getAssignment( final IndicatorNode variable ) {
						return assignment.getAssignment( varmap.getBs( variable ) );
					}
				};
			}
		};

		return new MappedFactorGraph( fg, varmap, mapper );
	}
}
