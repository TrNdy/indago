/**
 *
 */
package com.indago.brandnewfg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.indago.data.segmentation.ConflictGraph;
import com.indago.data.segmentation.LabelingSegment;
import com.indago.fg.CostsFactory;
import com.indago.models.SegmentationModel;
import com.indago.models.assignments.AssignmentVars;
import com.indago.models.segments.ConflictSet;
import com.indago.models.segments.SegmentVar;

import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

/**
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class ProblemGraphFactory_WIP {

	public static Pair< SegmentationModel, AssignmentMapper< SegmentVar, LabelingSegment > > createSegmentationProblem(
			final Collection< LabelingSegment > segments,
			final ConflictGraph< LabelingSegment > conflicts,
			final CostsFactory< ? super LabelingSegment > segmentCosts ) {

		final ArrayList< SegmentVar > segmentVars = new ArrayList< >();

		final Map< LabelingSegment, SegmentVar > varmap = new HashMap<>();
		for ( final LabelingSegment segment : segments ) {
			final SegmentVar var = new SegmentVar( segment, segmentCosts.getCost( segment ) );
			varmap.put( segment, var );
			segmentVars.add( var );
		}

		final Collection< ConflictSet > conflictSets = new ArrayList< >();

		for ( final Collection< LabelingSegment > clique : conflicts.getConflictGraphCliques() ) {
			final ConflictSet cs = new ConflictSet();
			for ( final LabelingSegment seg : clique )
				cs.add( varmap.get( seg ) );
			conflictSets.add( cs );
		}

		final SegmentationModel problem = new SegmentationModel() {

			@Override
			public Collection< SegmentVar > getSegments() {
				return segmentVars;
			}

			@Override
			public Collection< ConflictSet > getConflictSets() {
				return conflictSets;
			}

			@Override
			public int getTime() {
				return 0;
			}

			@Override
			public AssignmentVars getOutAssignments() {
				return null;
			}

			@Override
			public AssignmentVars getInAssignments() {
				return null;
			}
		};

		final AssignmentMapper< SegmentVar, LabelingSegment > mapper = new AssignmentMapper< SegmentVar, LabelingSegment >() {
			@Override
			public Assignment< LabelingSegment > map( final Assignment< SegmentVar > assignment ) {
				return new Assignment< LabelingSegment >() {
					@Override
					public boolean isAssigned( final LabelingSegment variable ) {
						return assignment.isAssigned( varmap.get( variable ) );
					}

					@Override
					public int getAssignment( final LabelingSegment variable ) {
						return assignment.getAssignment( varmap.get( variable ) );
					}
				};
			}
		};

		return new ValuePair< >( problem, mapper );
	}

	public static Pair< UnaryCostConstraintGraph, AssignmentMapper< Variable, SegmentVar > >
			createFactorGraph( final SegmentationModel segmentationProblem ) {

		final Map< SegmentVar, Variable > varmap = new HashMap<>();

		final Collection< Variable > variables = varmap.values();
		final ArrayList< Factor > unaries = new ArrayList<>();
		final ArrayList< Factor > constraints = new ArrayList<>();

		for ( final SegmentVar segvar : segmentationProblem.getSegments() ) {
			final Variable var = Variables.binary();
			varmap.put( segvar, var );
			unaries.add( Factors.unary( var, 0.0, segvar.getCost() ) );
		}

		for ( final ConflictSet conflictSet : segmentationProblem.getConflictSets() ) {
			final ArrayList< Variable > vars = new ArrayList<>();
			for ( final SegmentVar segvar : conflictSet )
				vars.add( varmap.get( segvar ) );
			constraints.add( Factors.atMostOneConstraint( vars ) );
		}

		final UnaryCostConstraintGraph fg = new UnaryCostConstraintGraph( variables, unaries, constraints );
		final AssignmentMapper< Variable, SegmentVar > mapper = new AssignmentMapper< Variable, SegmentVar >() {
			@Override
			public Assignment< SegmentVar > map( final Assignment< Variable > assignment ) {
				return new Assignment< SegmentVar >() {
					@Override
					public boolean isAssigned( final SegmentVar variable ) {
						return assignment.isAssigned( varmap.get( variable ) );
					}

					@Override
					public int getAssignment( final SegmentVar variable ) {
						return assignment.getAssignment( varmap.get( variable ) );
					}
				};
			}
		};

		return new ValuePair<>( fg, mapper );
	}
}
