package com.indago.fg;

import java.util.List;

import com.indago.fg.factor.Factor;
import com.indago.fg.function.Function;
import com.indago.fg.variable.Variable;

public class FactorGraph {

	private final List< ? extends Variable< ? > > variables;
	private final List< ? extends Factor< ?, ?, ? > > factors;
	private final List< ? extends Function< ?, ? > > functions;

	public FactorGraph( final List< ? extends Variable< ? > > variables, final List< ? extends Factor< ?, ?, ? > > factors, final List< ? extends Function< ?, ? > > functions ) {
		this.variables = variables;
		this.factors = factors;
		this.functions = functions;
	}

	/**
	 * @return the variables
	 */
	public List< ? extends Variable< ? > > getVariables() {
		return variables;
	}

	/**
	 * @return the factors
	 */
	public List< ? extends Factor< ?, ?, ? > > getFactors() {
		return factors;
	}

	/**
	 * @return the functions
	 */
	public List< ? extends Function< ?, ? > > getFunctions() {
		return functions;
	}
}
