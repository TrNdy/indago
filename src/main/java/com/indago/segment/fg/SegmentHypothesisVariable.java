/**
 * 
 */
package com.indago.segment.fg;

import com.indago.fg.variable.BooleanVariable;
import com.indago.segment.Segment;

/**
 * @author jug
 */
public class SegmentHypothesisVariable extends BooleanVariable {

	private final Segment segment;

	/**
	 * @param value
	 */
	public SegmentHypothesisVariable( final Segment segment ) {
		super( Boolean.FALSE );
		this.segment = segment;
	}

	public Segment getSegment() {
		return this.segment;
	}
}
