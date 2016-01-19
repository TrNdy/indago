package com.indago.segment;

import java.util.ArrayList;
import java.util.Collection;

import com.indago.tracking.seg.ConflictGraph;
import com.indago.tracking.seg.ConflictSet;
import com.indago.tracking.seg.SegmentVar;

/**
 * @author tpietzsch, jug
 */
public class PairwiseConflictGraph implements ConflictGraph {

	private final LabelingBuilder labelingBuilder;

	private Collection< ConflictSet > conflictCliques;

	public PairwiseConflictGraph( final LabelingBuilder labelingBuilder ) {
		this.labelingBuilder = labelingBuilder;
	}

	@Override
	public Collection< ConflictSet > getConflictCliques() {
		if ( conflictCliques == null )
			conflictCliques = getConflictCliques( labelingBuilder );
		return conflictCliques;
	}

	public static Collection< ConflictSet > getConflictCliques( final LabelingPlus labelingPlus ) {
		final Collection< ConflictSet > conflictCliques = new ArrayList< >();

		labelingPlus.getFragments(); // we call this to update FragmentIndices of all segments
		final ArrayList< LabelData > segmentLabels =
				new ArrayList<>( labelingPlus.getLabeling().getMapping().getLabels() );

		final int numSegments = segmentLabels.size();
		for ( int i = 0; i < numSegments - 1; ++i ) {
			final LabelData sli = segmentLabels.get( i );
			for ( int j = i + 1; j < numSegments; ++j ) {
				final LabelData slj = segmentLabels.get( j );
				for ( final Integer fj : slj.getFragmentIndices() ) {
					if ( sli.getFragmentIndices().contains( fj ) ) {
						final ConflictSet clique = new ConflictSet();
						clique.add( new SegmentVar( sli.getSegment() ) );
						clique.add( new SegmentVar( slj.getSegment() ) );
						conflictCliques.add( clique );
						break;
					}
				}
			}
		}

		return conflictCliques;
	}
}
