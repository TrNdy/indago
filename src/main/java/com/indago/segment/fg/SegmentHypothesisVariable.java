/**
 *
 */
package com.indago.segment.fg;

import java.util.ArrayList;
import java.util.List;

import com.indago.fg.variable.BooleanVariable;
import com.indago.segment.Segment;

/**
 * @author jug
 */
public class SegmentHypothesisVariable< T extends Segment >
		extends
		BooleanVariable {

	private final T segment;

	private final List< BooleanVariable > leftNeighbors;
	private final List< BooleanVariable > rightNeighbors;

	/**
	 * @param value
	 */
	public SegmentHypothesisVariable( final T segment ) {
		this.segment = segment;
		leftNeighbors = new ArrayList< BooleanVariable >();
		rightNeighbors = new ArrayList< BooleanVariable >();
	}

	public T getSegment() {
		return this.segment;
	}

	public void addLeftNeighbor( final BooleanVariable leftNeighbor ) {
		leftNeighbors.add( leftNeighbor );
	}

	public List< BooleanVariable > getLeftNeighbors() {
		return leftNeighbors;
	}

	public void addRightNeighbor( final BooleanVariable rightNeighbor ) {
		rightNeighbors.add( rightNeighbor );
	}

	public List< BooleanVariable > getRightNeighbors() {
		return rightNeighbors;
	}

}
