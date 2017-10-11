/**
 *
 */
package com.indago.data.segmentation.fg;

import java.util.ArrayList;
import java.util.List;

import com.indago.data.segmentation.Segment;
import com.indago.fg.BooleanVariable;

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

	@Override
	public String toString() {
		return super.toString() + String.format( " (%s)", getSegment().toString() );
	}

}
