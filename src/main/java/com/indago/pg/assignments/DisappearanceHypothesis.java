package com.indago.pg.assignments;

import com.indago.pg.segments.SegmentNode;

public class DisappearanceHypothesis extends AssignmentNode {

	private final SegmentNode src;

	/**
	 * @param cost
	 */
	public DisappearanceHypothesis( final double cost, final SegmentNode src ) {
		super( cost );
		this.src = src;
	}

	public SegmentNode getSrc() {
		return src;
	}
}
