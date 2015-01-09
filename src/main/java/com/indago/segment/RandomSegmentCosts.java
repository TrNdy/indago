package com.indago.segment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

/**
 * Assigns random costs to all segments in a SegmentMultiForest.
 * This is useful for testing artificial setups...
 *
 * @author jug
 */
class RandomSegmentCosts implements SegmentCosts {

	private final HashMap< Segment, Double > segmentToCost = new HashMap< Segment, Double >();

	public RandomSegmentCosts( final Collection< ? extends Segment > segments, final int randomSeed ) {
		final Random rand = new Random( randomSeed );
		for ( final Segment segment : segments )
			segmentToCost.put( segment, - rand.nextDouble() );
	}

	/**
	 * @param segment
	 * @return the cost assigned to the given segment or Double.MAX_VALUE if
	 *         this segment is unknown.
	 */
	@Override
	public double getCost( final Segment segment ) {
		final Double muh = segmentToCost.get( segment );
		if ( muh != null ) { return muh.doubleValue(); }
		return Double.MAX_VALUE;
	}
}