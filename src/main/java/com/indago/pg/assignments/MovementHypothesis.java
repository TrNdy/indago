package com.indago.pg.assignments;

import com.indago.pg.segments.SegmentNode;

public class MovementHypothesis extends AssignmentNode
{

	private double cost;
	private final SegmentNode src;
	private final SegmentNode dest;

	/**
	 * @param cost
	 */
	public MovementHypothesis (
			final double cost,
			final SegmentNode src,
			final SegmentNode dest ) {
		super( cost );
		this.src = src;
		this.dest = dest;
	}

	/**
	 * @see com.indago.pg.IndicatorNode#getCost()
	 */
	@Override
	public double getCost() {
		return cost;
	}

	public SegmentNode getSrc() {
		return src;
	}

	public SegmentNode getDest() {
		return dest;
	}

}
