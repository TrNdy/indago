package com.indago.pg.assignments;

import com.indago.pg.segments.SegmentNode;

public class AppearanceHypothesis extends AssignmentNode
{

	private final SegmentNode dest;

	/**
	 * @param cost
	 */
	public AppearanceHypothesis( final double cost, final SegmentNode dest ) {
		super( cost );
		this.dest = dest;
	}

	public SegmentNode getDest() {
		return dest;
	}
}
