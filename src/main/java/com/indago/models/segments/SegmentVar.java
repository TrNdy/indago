package com.indago.models.segments;

import com.indago.data.segmentation.LabelingSegment;
import com.indago.models.IndicatorVar;
import com.indago.models.SegmentationModel;
import com.indago.models.assignments.AssignmentVars;

public class SegmentVar extends IndicatorVar {

	private SegmentationModel sp;
	private final LabelingSegment segment;
	private final AssignmentVars in;
	private final AssignmentVars out;

	/**
	 * @param segment
	 */
	public SegmentVar( final LabelingSegment segment, final double cost ) {
		super( cost );
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

	public void setSegmentationProblem( final SegmentationModel sp ) {
		this.sp = sp;
	}

	public SegmentationModel getSegmentationProblem() {
		return sp;
	}

}