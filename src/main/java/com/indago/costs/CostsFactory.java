/**
 *
 */
package com.indago.costs;

/**
 * @author jug
 */
public interface CostsFactory< T > {

	public double getCost( final T segment );
}
