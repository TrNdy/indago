/**
 *
 */
package com.indago.segment.fg;

import com.indago.fg.variable.BooleanVariable;
import com.indago.segment.Segment;

/**
 * @author jug
 */
public class SegmentHypothesisVariable< T extends Segment > extends BooleanVariable {

	private final T segment;

	/**
	 * @param value
	 */
	public SegmentHypothesisVariable( final T segment ) {
		this.segment = segment;
	}

	public T getSegment() {
		return this.segment;
	}
}
