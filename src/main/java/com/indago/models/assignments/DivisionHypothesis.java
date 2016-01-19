package com.indago.models.assignments;

import com.indago.models.segments.SegmentVar;

public class DivisionHypothesis extends AssignmentVar {

	private double cost;
	private final SegmentVar src;
	private final SegmentVar dest1;
	private final SegmentVar dest2;

	/**
	 * @param cost
	 */
	public DivisionHypothesis(
			final double cost,
			final SegmentVar src,
			final SegmentVar dest1,
			final SegmentVar dest2 ) {
		super( cost );
		this.src = src;
		this.dest1 = dest1;
		this.dest2 = dest2;
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

	public SegmentVar getDest1() {
		return dest1;
	}

	public SegmentVar getDest2() {
		return dest2;
	}

}