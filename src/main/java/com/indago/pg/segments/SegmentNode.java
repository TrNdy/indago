package com.indago.pg.segments;

import com.indago.data.segmentation.LabelingSegment;
import com.indago.pg.IndicatorNode;
import com.indago.pg.assignments.AssignmentNodes;

public class SegmentNode extends IndicatorNode {

	private final LabelingSegment segment;
	private final AssignmentNodes in;
	private final AssignmentNodes out;

	/**
	 * @param segment
	 */
	public SegmentNode( final LabelingSegment segment, final double cost ) {
		super( cost );
		this.segment = segment;
		in = new AssignmentNodes();
		out = new AssignmentNodes();
	}

	public AssignmentNodes getInAssignments() {
		return in;
	}

	public AssignmentNodes getOutAssignments() {
		return out;
	}

	public LabelingSegment getSegment() {
		return segment;
	}
}
