package com.indago.fg;

import java.util.Collection;

import com.indago.fg.factor.Factor;
import com.indago.fg.function.Function;
import com.indago.fg.variable.Variable;

public class FactorGraph {

	private final Collection< ? extends Variable< ? > > variables;
	private final Collection< ? extends Factor< ?, ?, ? > > factors;
	private final Collection< ? extends Function< ?, ? > > functions;

	public FactorGraph(
			final Collection< ? extends Variable< ? > > variables,
			final Collection< ? extends Factor< ?, ?, ? > > factors,
			final Collection< ? extends Function< ?, ? > > functions ) {
		this.variables = variables;
		this.factors = factors;
		this.functions = functions;
	}

	/**
	 * @return the variables
	 */
	public Collection< ? extends Variable< ? > > getVariables() {
		return variables;
	}

	/**
	 * @return the factors
	 */
	public Collection< ? extends Factor< ?, ?, ? > > getFactors() {
		return factors;
	}

	/**
	 * @return the functions
	 */
	public Collection< ? extends Function< ?, ? > > getFunctions() {
		return functions;
	}
}
