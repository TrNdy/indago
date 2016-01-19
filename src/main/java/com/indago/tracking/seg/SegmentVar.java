package com.indago.tracking.seg;

import com.indago.tracking.IndicatorVar;
import com.indago.tracking.SegmentationProblem;
import com.indago.tracking.map.AssignmentVars;

public class SegmentVar extends IndicatorVar {

	private final SegmentationProblem segprob;
	private final AssignmentVars in;
	private final AssignmentVars out;

	public SegmentVar( final double cost, final SegmentationProblem segprob ) {
		super( cost );
		this.segprob = segprob;
		in = new AssignmentVars();
		out = new AssignmentVars();
	}

	public AssignmentVars getInAssignments() {
		return in;
	}

	public AssignmentVars getOutAssignments() {
		return out;
	}

	public SegmentationProblem getSegmentationProblem() {
		return segprob;
	}

}