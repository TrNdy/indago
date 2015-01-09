package com.indago.segment;

import java.util.ArrayList;

import net.imglib2.newlabeling.Labeling;

/**
 * Represents a particular {@link LabelingSegment} in a {@link Labeling}. It may
 * be associated with a {@link LabelingTreeNode node} in a
 * {@link LabelingForest hypothesis forest}. It may have an ordered list of
 * indices of the {@link LabelingFragment}s making up the segment.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class SegmentLabel {

	private LabelingSegment segment;

	private LabelingTreeNode labelingTreeNode;

	private final ArrayList< Integer > fragmentIndices;

	SegmentLabel() {
		segment = null;
		labelingTreeNode = null;
		fragmentIndices = new ArrayList<>();
	}

	void setSegment( final LabelingSegment segment ) {
		this.segment = segment;
	}

	void setLabelingTreeNode( final LabelingTreeNode labelingTreeNode ) {
		this.labelingTreeNode = labelingTreeNode;
	}

	public LabelingSegment getSegment() {
		return segment;
	}

	public LabelingTreeNode getLabelingTreeNode() {
		return labelingTreeNode;
	}

	public ArrayList< Integer > getFragmentIndices() {
		return fragmentIndices;
	}
}
