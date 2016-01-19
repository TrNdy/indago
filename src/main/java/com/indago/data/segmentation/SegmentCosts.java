/**
 *
 */
package com.indago.data.segmentation;

import com.indago.fg.CostsFactory;

/**
 * @author jug
 */
public interface SegmentCosts extends CostsFactory< Segment > {

	public double getCost( final Segment segment );
}
