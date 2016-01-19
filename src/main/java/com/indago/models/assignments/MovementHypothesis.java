package com.indago.models.assignments;

import com.indago.models.segments.SegmentVar;

public class MovementHypothesis extends AssignmentVar
{

	private double cost;
	private final SegmentVar src;
	private final SegmentVar dest;

	/**
	 * @param cost
	 */
	public MovementHypothesis (
			final double cost,
			final SegmentVar src,
			final SegmentVar dest ) {
		super( cost );
		this.src = src;
		this.dest = dest;
	}

	/**
	 * @see com.indago.models.IndicatorVar#getCost()
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
