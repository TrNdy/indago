package com.indago.tracking.map;

import com.indago.tracking.seg.SegmentVar;

public class AppearanceHypothesis extends AssignmentVar
{

	private final SegmentVar dest;

	/**
	 * @param cost
	 */
	public AppearanceHypothesis( final double cost, final SegmentVar dest ) {
		super( cost );
		this.dest = dest;
	}

	public SegmentVar getDest() {
		return dest;
	}
}
