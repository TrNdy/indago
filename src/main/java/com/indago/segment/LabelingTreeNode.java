package com.indago.segment;

import java.util.Iterator;

import net.imglib2.Localizable;

/**
 * A node in a {@link LabelingForest}.
 * Links to the {@link LabelingSegment} and its {@link SegmentLabel}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class LabelingTreeNode extends HypothesisTreeNode< LabelingTreeNode, LabelingSegment > implements Iterable< Localizable >
{
	private final SegmentLabel label;

	public LabelingTreeNode( final LabelingSegment segment, final SegmentLabel label )
	{
		super( segment );
		this.label = label;
	}

	public SegmentLabel getLabel() {
		return label;
	}

	@Override
	public Iterator< Localizable > iterator() {
		return getSegment().iterator();
	}
}