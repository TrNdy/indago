package com.indago.data.segmentation;

import java.util.ArrayList;

/**
 * Represents a particular {@link LabelingSegment} in a Labeling. It may be
 * associated with a {@link LabelingTreeNode node} in a {@link LabelingForest
 * hypothesis forest}. It may have an ordered list of indices of the
 * {@link LabelingFragment}s making up the segment.
 *
 * @author Tobias Pietzsch
 * @author fjug
 */
public class LabelData {

	/**
	 * Unique id used for serialization.
	 */
	private final int id;

	private LabelingSegment segment;

	private LabelingTreeNode labelingTreeNode;

	private final ArrayList< Integer > fragmentIndices;

	LabelData() {
		this( createId() );
	}

	LabelData( final int id ) {
		this.id = id;
		useId( id );
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

	public int getId() {
		return id;
	}

	private static int nextId = 0;

	private static synchronized int createId() {
		return nextId++;
	}

	private static synchronized void useId( final int id ) {
		if ( nextId < id + 1 )
			nextId = id + 1;
	}
}
