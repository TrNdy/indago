/**
 *
 */
package com.indago.pg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.indago.costs.CostFactory;
import com.indago.data.segmentation.ConflictGraph;
import com.indago.data.segmentation.LabelingSegment;
import com.indago.fg.Assignment;
import com.indago.fg.AssignmentMapper;
import com.indago.pg.segments.ConflictSet;
import com.indago.pg.segments.SegmentNode;

import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

/**
 * @author Tobias Pietzsch
 */
public class ModelGraphFactory {

	public static Pair< TrackingSegmentationProblem, AssignmentMapper< SegmentNode, LabelingSegment > > createSegmentationProblem(
			final Collection< LabelingSegment > segments,
			final ConflictGraph< LabelingSegment > conflicts,
			final CostFactory< ? super LabelingSegment > segmentCosts ) {

		final ArrayList< SegmentNode > segmentVars = new ArrayList< >();

		final Map< LabelingSegment, SegmentNode > varmap = new HashMap< >();
		for ( final LabelingSegment segment : segments ) {
			final SegmentNode var = new SegmentNode( segment, segmentCosts.getCost( segment ) );
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

		final TrackingSegmentationProblem problem = new TrackingSegmentationProblem() {

			@Override
			public Collection< SegmentNode > getSegments() {
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
			public void force( final SegmentNode segVar ) {
			}

			@Override
			public void avoid( final SegmentNode segVar ) {
			}

			@Override
			public Set< SegmentNode > getForcedNodes() {
				return null;
			}

			@Override
			public Set< SegmentNode > getAvoidedNodes() {
				return null;
			}

			@Override
			public Set< SegmentNode > getForcedByAppearanceNodes() {
				return null;
			}

			@Override
			public Set< SegmentNode > getForcedByDisappearanceNodes() {
				return null;
			}

			@Override
			public Set< SegmentNode > getForcedSegmentNodeMovesTo() {
				return null;
			}

			@Override
			public Set< SegmentNode > getForcedSegmentNodeDivisionsTo() {
				return null;
			}

			@Override
			public Set< SegmentNode > getForcedSegmentNodeMovesFrom() {
				return null;
			}

			@Override
			public Set< SegmentNode > getForcedSegmentNodeDivisionsFrom() {
				return null;
			}

			@Override
			public Set< ConflictSet > getForcedConflictSetMovesTo() {
				return null;
			}

			@Override
			public Set< ConflictSet > getForcedConflictSetMovesFrom() {
				return null;
			}

			@Override
			public Set< ConflictSet > getForcedConflictSetDivisionsTo() {
				return null;
			}

			@Override
			public Set< ConflictSet > getForcedConflictSetDivisionsFrom() {
				return null;
			}

		};

		final AssignmentMapper< SegmentNode, LabelingSegment > mapper =
				new AssignmentMapper< SegmentNode, LabelingSegment >() {
			@Override
					public Assignment< LabelingSegment > map( final Assignment< ? super SegmentNode > assignment ) {
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

}
