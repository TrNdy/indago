package com.indago.fg;

import java.util.Collection;

import com.indago.fg.factor.Factor;
import com.indago.fg.function.Function;
import com.indago.fg.variable.Variable;

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
	 * @see com.indago.fg.FactorGraph#getVariables()
	 */
	@Override
	public Collection< ? extends Variable< ? > > getVariables() {
		return variables;
	}

	/**
	 * @see com.indago.fg.FactorGraph#getFactors()
	 */
	@Override
	public Collection< ? extends Factor< ?, ?, ? > > getFactors() {
		return factors;
	}

	/**
	 * @see com.indago.fg.FactorGraph#getFunctions()
	 */
	@Override
	public Collection< ? extends Function< ?, ? > > getFunctions() {
		return functions;
	}

}
