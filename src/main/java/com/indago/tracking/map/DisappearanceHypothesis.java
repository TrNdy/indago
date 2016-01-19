package com.indago.tracking.map;

import com.indago.tracking.seg.SegmentVar;

public class DisappearanceHypothesis extends AssignmentVar {

	private final SegmentVar src;

	/**
	 * @param cost
	 */
	public DisappearanceHypothesis( final double cost, final SegmentVar src ) {
		super( cost );
		this.src = src;
	}

	public SegmentVar getSrc() {
		return src;
	}
}
