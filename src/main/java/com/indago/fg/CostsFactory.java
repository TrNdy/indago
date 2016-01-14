/**
 *
 */
package com.indago.fg;

/**
 * @author jug
 */
public interface CostsFactory< T > {

	public double getCost( final T segment );
}
