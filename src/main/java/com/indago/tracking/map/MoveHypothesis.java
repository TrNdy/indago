package com.indago.tracking.map;

import com.indago.tracking.seg.SegmentVar;

public class MoveHypothesis extends AssignmentVar
{

	private double cost;
	private final SegmentVar src;
	private final SegmentVar dest;

	/**
	 * @param cost
	 */
	public MoveHypothesis (
			final double cost,
			final SegmentVar src,
			final SegmentVar dest ) {
		super( cost );
		this.src = src;
		this.dest = dest;
	}

	/**
	 * @see com.indago.tracking.IndicatorVar#getCost()
	 */
	@Override
	public double getCost() {
		return cost;
	}

	public SegmentVar getSrc() {
		return src;
	}

	public SegmentVar getDest() {
		return dest;
	}

}
