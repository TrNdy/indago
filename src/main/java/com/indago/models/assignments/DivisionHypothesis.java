package com.indago.models.assignments;

import com.indago.models.segments.SegmentNode;

public class DivisionHypothesis extends AssignmentNode {

	private double cost;
	private final SegmentNode src;
	private final SegmentNode dest1;
	private final SegmentNode dest2;

	/**
	 * @param cost
	 */
	public DivisionHypothesis(
			final double cost,
			final SegmentNode src,
			final SegmentNode dest1,
			final SegmentNode dest2 ) {
		super( cost );
		this.src = src;
		this.dest1 = dest1;
		this.dest2 = dest2;
	}

	/**
	 * @see com.indago.models.IndicatorNode#getCost()
	 */
	@Override
	public double getCost() {
		return cost;
	}

	public SegmentNode getSrc() {
		return src;
	}

	public SegmentNode getDest1() {
		return dest1;
	}

	public SegmentNode getDest2() {
		return dest2;
	}

}