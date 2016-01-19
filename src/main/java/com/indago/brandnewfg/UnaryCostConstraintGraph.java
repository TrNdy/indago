package com.indago.brandnewfg;

import java.util.Collection;

/**
 * A factor graph with only binary variables, unary cost functions, and linear
 * constraints. This is the type of FactorGraph that we can currently solve with
 * {@link SolveGurobi}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class UnaryCostConstraintGraph {

	private final Collection< Variable > variables;

	private final Collection< Factor > unaries;

	private final Collection< Factor > constraints;

	public UnaryCostConstraintGraph(
			final Collection< Variable > variables,
			final Collection< Factor > unaries,
			final Collection< Factor > constraints ) {
		this.variables = variables;
		this.unaries = unaries;
		this.constraints = constraints;
	}

	public Collection< Variable > getVariables() {
		return variables;
	}

	public Collection< Factor > getUnaries() {
		return unaries;
	}

	public Collection< Factor > getConstraints() {
		return constraints;
	}
}
