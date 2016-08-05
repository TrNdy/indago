/**
 *
 */
package com.indago.costs;

/**
 * @author jug
 */
public interface CostFactory< T > {

	public default String getName() {
		return this.getClass().getName();
	}

	public double getCost( final T segment );

	public CostParams getParameters();

	public void setParameters( CostParams p );
}
