/**
 *
 */
package com.indago.data.segmentation;

import com.indago.costs.CostsFactory;

/**
 * @author jug
 */
public interface SegmentCosts extends CostsFactory< Segment > {

	public double getCost( final Segment segment );
}
