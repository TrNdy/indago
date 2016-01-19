/**
 *
 */
package com.indago.old_fg;

import java.util.Collection;

import com.indago.old_fg.factor.Factor;
import com.indago.old_fg.function.Function;
import com.indago.old_fg.variable.Variable;

/**
 * @author jug
 */
public interface FactorGraph {

	/**
	 * @return the variables
	 */
	public Collection< ? extends Variable< ? > > getVariables();

	/**
	 * @return the factors
	 */
	public Collection< ? extends Factor< ?, ?, ? > > getFactors();

	/**
	 * @return the functions
	 */
	public Collection< ? extends Function< ?, ? > > getFunctions();

}
