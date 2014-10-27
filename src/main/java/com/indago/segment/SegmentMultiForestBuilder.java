package com.indago.segment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SegmentMultiForestBuilder {

	private final SegmentMultiForest segmentMultiForest;

	public SegmentMultiForestBuilder( final List< LabelingForest > labelingForests ) {

		final Collection< Collection< ? extends Segment > > rootsCollection = new ArrayList< Collection< ? extends Segment > >();

		for ( final LabelingForest forest : labelingForests ) {
			rootsCollection.add( new SegmentForestBuilder( forest ).getSegmentForest().roots() );
		}

		segmentMultiForest = new SegmentMultiForest( rootsCollection );
	}

	public SegmentMultiForest getSegmentMultiForest() {
		return segmentMultiForest;
	}
}
