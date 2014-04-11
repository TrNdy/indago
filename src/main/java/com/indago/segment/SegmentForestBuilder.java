package com.indago.segment;

import java.util.ArrayList;

import net.imglib2.labeling.LabelRegions;

public class SegmentForestBuilder
{
	private final LabelRegions< Integer > regions;

	private final SegmentForest segmentForest;

	public SegmentForestBuilder( final LabelingForest labelingForest )
	{
		regions = new LabelRegions< Integer >( labelingForest.getLabeling() );
		final ArrayList< Segment > roots = new ArrayList< Segment >();
		for ( final LabelingTreeNode c : labelingForest.roots() )
			roots.add( buildSegmentTreeNode( c ) );
		segmentForest = new SegmentForest( roots );
	}

	private Segment buildSegmentTreeNode( final LabelingTreeNode node )
	{
		final Segment segment = new Segment( regions, node.getLabel() );
		for ( final LabelingTreeNode c : node.getChildren() )
			segment.addChild( buildSegmentTreeNode( c ) );
		return segment;
	}

	public SegmentForest getSegmentForest()
	{
		return segmentForest;
	}
}
