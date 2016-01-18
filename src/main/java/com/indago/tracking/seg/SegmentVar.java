package com.indago.tracking.seg;

import com.indago.tracking.IndicatorVar;
import com.indago.tracking.SegmentationProblem;
import com.indago.tracking.map.AssignmentVars;

public interface SegmentVar extends IndicatorVar {

	public AssignmentVars getInAssignments();

	public AssignmentVars getOutAssignments();

	public SegmentationProblem getSegmentationProblem();
}