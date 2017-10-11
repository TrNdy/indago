/**
 *
 */
package com.indago.fg;

import java.util.Collection;

/**
 * @author jug
 */
public interface FactorGraph {

	/**
	 * @return the variables
	 */
	public Collection< ? extends Variable > getVariables();

	/**
	 * @return the factors
	 */
	public Collection< ? extends Factor > getFactors();

	/**
	 * @return the functions
	 */
	public Collection< ? extends Function > getFunctions();

}
