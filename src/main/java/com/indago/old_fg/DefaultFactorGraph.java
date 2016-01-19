package com.indago.old_fg;

import java.util.Collection;

import com.indago.old_fg.factor.Factor;
import com.indago.old_fg.function.Function;
import com.indago.old_fg.variable.Variable;

public class DefaultFactorGraph implements FactorGraph {

	private final Collection< ? extends Variable< ? > > variables;
	private final Collection< ? extends Factor< ?, ?, ? > > factors;
	private final Collection< ? extends Function< ?, ? > > functions;

	public DefaultFactorGraph(
			final Collection< ? extends Variable< ? > > variables,
			final Collection< ? extends Factor< ?, ?, ? > > factors,
			final Collection< ? extends Function< ?, ? > > functions ) {
		this.variables = variables;
		this.factors = factors;
		this.functions = functions;
	}

	/**
	 * @see com.indago.old_fg.FactorGraph#getVariables()
	 */
	@Override
	public Collection< ? extends Variable< ? > > getVariables() {
		return variables;
	}

	/**
	 * @see com.indago.old_fg.FactorGraph#getFactors()
	 */
	@Override
	public Collection< ? extends Factor< ?, ?, ? > > getFactors() {
		return factors;
	}

	/**
	 * @see com.indago.old_fg.FactorGraph#getFunctions()
	 */
	@Override
	public Collection< ? extends Function< ?, ? > > getFunctions() {
		return functions;
	}

}
