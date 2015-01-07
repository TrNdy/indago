package com.indago.segment;

import java.util.Iterator;

import net.imglib2.Localizable;


public class LabelingTreeNode< T > extends HypothesisTreeNode< LabelingTreeNode< T >, LabelingSegment > implements Iterable< Localizable >
{
	private final T label;

	public LabelingTreeNode( final LabelingSegment segment, final T label )
	{
		super( segment );
		this.label = label;
	}

	public T getLabel() {
		return label;
	}

	@Override
	public Iterator< Localizable > iterator() {
		return getSegment().iterator();
	}
}