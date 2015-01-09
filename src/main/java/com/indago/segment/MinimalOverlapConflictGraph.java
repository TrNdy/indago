package com.indago.segment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class MinimalOverlapConflictGraph implements ConflictGraph< LabelingSegment > {

	private final LabelingBuilder labelingBuilder;

	private Collection< ? extends Collection< LabelingSegment >> conflictGraphCliques;

	public MinimalOverlapConflictGraph( final LabelingBuilder labelingBuilder ) {
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

		int heads[] = new int[ 32 ];

		final ArrayList< LabelingFragment > fragments = labelingBuilder.getFragments();

		// for each fragment
		for ( final LabelingFragment fragment : fragments ) {
			// if number of contained segments < 2
			if ( fragment.getSegments().size() < 2 ) continue;

			// find minimal elements of contained segments
			// (those do not have a hypothesis-tree child that is also in contained segments)
			final ArrayList< SegmentLabel > intersect = new ArrayList<>();
			A1: for ( final SegmentLabel sl : fragment.getSegments() ) {
				for ( final LabelingTreeNode child : sl.getLabelingTreeNode().getChildren() )
					for ( final SegmentLabel sl2 : fragment.getSegments() )
						if ( sl2.getLabelingTreeNode() == child ) continue A1;
				intersect.add( sl );
			}

			// compute the intersection of multiple ordered lists of fragment indices.
			// only proceed until we know whether the intersection contains 2 or more elements.

			// this is how we would do it naively:
//			final ArrayList< Integer > intersection = new ArrayList<>( intersect.get( 0 ).getFragmentIndices() );
//			final int numLists = intersect.size();
//			for ( int i = 1; i < numLists; ++i )
//				intersection.retainAll( intersect.get( i ).getFragmentIndices() );
//			final int intersectionSize = intersection.size();

			int intersectionSize = 0;
			final int numLists = intersect.size();
			if ( numLists < 2 ) {
				intersectionSize = intersect.get( 0 ).getFragmentIndices().size();
			} else {
				if ( numLists > heads.length )
					heads = new int[ numLists ];
				else
					Arrays.fill( heads, 1, numLists, 0 );
				heads[ 0 ] = -1;
				int currListIndex, firstListElem, currListElem;
				ArrayList< Integer > currList;
				final ArrayList< Integer > firstList = intersect.get( 0 ).getFragmentIndices();
				A2: while ( true ) {
					if ( ++heads[ 0 ] >= firstList.size() ) {
						// done. we found all elements of the intersection.
						break A2;
					}
					firstListElem = firstList.get( heads[ 0 ] );
					currListIndex = 1;
					currList = intersect.get( currListIndex ).getFragmentIndices();
					currListElem = currList.get( heads[ currListIndex ] );
					while ( firstListElem < currListElem ) {
						if ( ++heads[ 0 ] >= firstList.size() ) {
							// done. we found all elements of the intersection.
							break A2;
						}
						firstListElem = firstList.get( heads[ 0 ] );
					} // now firstListElem >= currListElem
					while ( true ) {
						if ( currListElem == firstListElem ) {
							// common element found in all lists up to currListIndex.
							// go to next list if there is one
							if ( ++currListIndex == numLists ) {
								// found an element of the intersection.
								if ( ++intersectionSize > 1 ) break A2;
								break;
							}
							currList = intersect.get( currListIndex ).getFragmentIndices();
							currListElem = currList.get( heads[ currListIndex ] );
						} else {
							// firstListElem > currListElem
							while ( currListElem < firstListElem ) {
								if ( ++heads[ currListIndex ] >= currList.size() ) {
									// done. we found all elements of the intersection.
									break A2;
								}
								currListElem = currList.get( heads[ currListIndex ] );
							} // now currListElem >= firstListElem
							if ( currListElem == firstListElem ) {
								// common element found in all lists up to currListIndex.
								// go to next list if there is one
								if ( ++currListIndex == numLists ) {
									// found an element of the intersection.
									if ( ++intersectionSize > 1 ) break A2;
									break;
								}
								currList = intersect.get( currListIndex ).getFragmentIndices();
								currListElem = currList.get( heads[ currListIndex ] );
							} else
								break;
						}
					}
				}
			}

			// found new conflict set
			if ( intersectionSize < 2 ) {
				final ArrayList< LabelingSegment > clique = new ArrayList<>();
				for ( final SegmentLabel sl : fragment.getSegments() )
					clique.add( sl.getSegment() );
				conflictGraphCliques.add( clique );
			}
		}

		return conflictGraphCliques;
	}
}
