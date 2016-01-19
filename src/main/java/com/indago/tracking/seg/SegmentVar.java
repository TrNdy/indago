package com.indago.tracking.seg;

import com.indago.segment.LabelingSegment;
import com.indago.tracking.IndicatorVar;
import com.indago.tracking.SegmentationProblem;
import com.indago.tracking.map.AssignmentVars;

public class SegmentVar extends IndicatorVar {

	private SegmentationProblem sp;
	private final LabelingSegment segment;
	private final AssignmentVars in;
	private final AssignmentVars out;

	/**
	 * @param segment
	 */
	public SegmentVar( final LabelingSegment segment ) {
		super(0.0);
		this.segment = segment;
		this.sp = null;
		in = new AssignmentVars();
		out = new AssignmentVars();
	}

	public AssignmentVars getInAssignments() {
		return in;
	}

	public AssignmentVars getOutAssignments() {
		return out;
	}

	public void setSegmentationProblem( final SegmentationProblem sp ) {
		this.sp = sp;
	}

	public SegmentationProblem getSegmentationProblem() {
		return sp;
	}
}