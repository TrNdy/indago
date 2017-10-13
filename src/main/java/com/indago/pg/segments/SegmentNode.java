package com.indago.pg.segments;

import com.indago.data.segmentation.LabelingSegment;
import com.indago.pg.IndicatorNode;
import com.indago.pg.SegmentationProblem;
import com.indago.pg.assignments.AssignmentNodes;

/**
 * @author Tobias Pietzsch
 * @author Florian Jug
 */
public class SegmentNode extends IndicatorNode {

	private SegmentationProblem sp;
	private final LabelingSegment segment;
	private final AssignmentNodes in;
	private final AssignmentNodes out;
	private int maxDelta;

	public SegmentNode( final LabelingSegment segment, final double cost ) {
		this( segment, cost, 0 );
	}

	public SegmentNode( final LabelingSegment segment, final double cost, final int maxDelta ) {
		super( cost );
		this.segment = segment;
		this.sp = null;
		in = new AssignmentNodes();
		out = new AssignmentNodes();
		this.maxDelta = maxDelta;
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

	public int getMaxDelta() {
		return maxDelta;
	}

	public void setMaxDelta( int maxDelta ) {
		this.maxDelta = maxDelta;
	}
}
