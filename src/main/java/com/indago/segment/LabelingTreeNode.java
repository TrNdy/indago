package com.indago.segment;

import java.util.Iterator;

import net.imglib2.Localizable;


public class LabelingTreeNode extends HypothesisTreeNode< LabelingTreeNode, LabelingSegment< Integer > > implements Iterable< Localizable >
{
	public LabelingTreeNode( final LabelingSegment< Integer > segment )
	{
		super( segment );
	}

	@Override
	public Iterator< Localizable > iterator() {
		return getSegment().iterator();
	}
}