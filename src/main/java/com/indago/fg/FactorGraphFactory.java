/**
 *
 */
package com.indago.fg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.indago.models.IndicatorVar;
import com.indago.models.SegmentationModel;
import com.indago.models.TrackingModel;
import com.indago.models.assignments.AppearanceHypothesis;
import com.indago.models.assignments.DisappearanceHypothesis;
import com.indago.models.assignments.DivisionHypothesis;
import com.indago.models.assignments.MovementHypothesis;
import com.indago.models.segments.ConflictSet;
import com.indago.models.segments.SegmentVar;

/**
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class FactorGraphFactory {

	public static class MappedFactorGraph {

		private final UnaryCostConstraintGraph fg;
		private final Map< IndicatorVar, Variable > varmap;
		private final AssignmentMapper< Variable, IndicatorVar > mapper;

		public MappedFactorGraph(
				final UnaryCostConstraintGraph fg,
				final Map< IndicatorVar, Variable > varmap,
				final AssignmentMapper< Variable, IndicatorVar > mapper ) {
			this.fg = fg;
			this.varmap = varmap;
			this.mapper = mapper;
		}

		/**
		 * @return the fg
		 */
		public UnaryCostConstraintGraph getFg() {
			return fg;
		}

		/**
		 * @return the varmap
		 */
		public Map< IndicatorVar, Variable > getVarmap() {
			return varmap;
		}

		/**
		 * @return the mapper
		 */
		public AssignmentMapper< Variable, IndicatorVar > getMapper() {
			return mapper;
		}
	}

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

		final ArrayList< MappedFactorGraph > frameResults =
				new ArrayList< >();

		for ( final SegmentationModel frameModel : trackingModel.getTimepoints() ) {
			final MappedFactorGraph frameResult =
					FactorGraphFactory.createFactorGraph( frameModel );
			frameResults.add( frameResult ); // TODO: need????

			for ( final SegmentVar segVar : frameModel.getSegments() ) {

				for ( final AppearanceHypothesis app : segVar.getInAssignments().getAppearances() ) {
//					app.
				}
				for ( final DisappearanceHypothesis disapp : segVar.getInAssignments().getDisappearances() ) {

				}
				for ( final MovementHypothesis move : segVar.getInAssignments().getMoves() ) {

				}
				for ( final DivisionHypothesis div : segVar.getInAssignments().getDivisions() ) {

				}
			}
		}

		// Collect FG variables
		for ( final Variable var : varmap.values() ) {
			variables.add( var );
		}
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
