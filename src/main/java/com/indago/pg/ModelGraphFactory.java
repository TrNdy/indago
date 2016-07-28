/**
 *
 */
package com.indago.pg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.indago.costs.CostsFactory;
import com.indago.data.segmentation.ConflictGraph;
import com.indago.data.segmentation.LabelingSegment;
import com.indago.fg.Assignment;
import com.indago.fg.AssignmentMapper;
import com.indago.pg.segments.ConflictSet;
import com.indago.pg.segments.SegmentNode;

import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

/**
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class ModelGraphFactory {

	public static Pair< SegmentationProblem, AssignmentMapper< SegmentNode, LabelingSegment > > createSegmentationProblem(
			final Collection< LabelingSegment > segments,
			final ConflictGraph< LabelingSegment > conflicts,
			final CostsFactory< ? super LabelingSegment > segmentCosts ) {

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

		final SegmentationProblem problem = new SegmentationProblem() {

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
				// TODO Auto-generated method stub

			}

			@Override
			public void avoid( final SegmentNode segVar ) {
				// TODO Auto-generated method stub

			}

			@Override
			public Set< SegmentNode > getForcedNodes() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set< SegmentNode > getAvoidedNodes() {
				// TODO Auto-generated method stub
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
