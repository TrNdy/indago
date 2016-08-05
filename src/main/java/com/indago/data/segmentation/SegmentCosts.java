/**
 *
 */
package com.indago.data.segmentation;

import com.indago.costs.CostFactory;

/**
 * @author jug
 */
public interface SegmentCosts extends CostFactory< Segment > {

	public double getCost( final Segment segment );
}
