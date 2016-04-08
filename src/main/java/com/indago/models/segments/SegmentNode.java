package com.indago.models.segments;

import com.indago.data.segmentation.LabelingSegment;
import com.indago.models.IndicatorNode;
import com.indago.models.SegmentationProblem;
import com.indago.models.assignments.AssignmentNodes;

public class SegmentNode extends IndicatorNode {

	private SegmentationProblem sp;
	private final LabelingSegment segment;
	private final AssignmentNodes in;
	private final AssignmentNodes out;

	/**
	 * @param segment
	 */
	public SegmentNode( final LabelingSegment segment, final double cost ) {
		super( cost );
		this.segment = segment;
		this.sp = null;
		in = new AssignmentNodes();
		out = new AssignmentNodes();
	}

	public AssignmentNodes getInAssignments() {
		return in;
	}

	public AssignmentNodes getOutAssignments() {
		return out;
	}

	public void setSegmentationProblem( final SegmentationProblem sp ) {
		this.sp = sp;
	}

	public SegmentationProblem getSegmentationProblem() {
		return sp;
	}

	public LabelingSegment getSegment() {
		return segment;
	}

}