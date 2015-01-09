package com.indago.segment;

import java.util.ArrayList;
import java.util.Collection;

public class PairwiseConflictGraph implements ConflictGraph< LabelingSegment > {

	private final LabelingBuilder labelingBuilder;

	private Collection< ? extends Collection< LabelingSegment > > conflictGraphCliques;

	public PairwiseConflictGraph( final LabelingBuilder labelingBuilder ) {
		this.labelingBuilder = labelingBuilder;
	}

	@Override
	public Collection< ? extends Collection< LabelingSegment > > getConflictGraphCliques() {
		if ( conflictGraphCliques == null )
			conflictGraphCliques = getConflictGraphCliques( labelingBuilder );
		return conflictGraphCliques;
	}

	public static ArrayList< ArrayList< LabelingSegment > > getConflictGraphCliques( final LabelingBuilder labelingBuilder ) {
		final ArrayList< ArrayList< LabelingSegment > > conflictGraphCliques = new ArrayList<>();

		labelingBuilder.getFragments(); // we call this to update FragmentIndices of all segments
		final ArrayList< SegmentLabel > segmentLabels = new ArrayList<>( labelingBuilder.getLabeling().getMapping().getLabels() );

		final int numSegments = segmentLabels.size();
		for ( int i = 0; i < numSegments - 1; ++i ) {
			final SegmentLabel sli = segmentLabels.get( i );
			for ( int j = i + 1; j < numSegments; ++j ) {
				final SegmentLabel slj = segmentLabels.get( j );
				for ( final Integer fj : slj.getFragmentIndices() ) {
					if ( sli.getFragmentIndices().contains( fj ) ) {
						final ArrayList< LabelingSegment > clique = new ArrayList<>();
						clique.add( sli.getSegment() );
						clique.add( slj.getSegment() );
						conflictGraphCliques.add( clique );
						break;
					}
				}
			}
		}

		return conflictGraphCliques;
	}
}
