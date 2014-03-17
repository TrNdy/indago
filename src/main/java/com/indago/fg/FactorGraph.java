package com.indago.fg;

import java.util.List;

public class FactorGraph {

	protected final List< Variable< ? > > variables;
	protected final List< Factor< ? > > factors;
	protected final List< Function< ? > > functions;

	public FactorGraph(
			final List< Variable< ? > > variables,
			final List< Factor< ? > > factors,
			final List< Function< ? > > functions )
	{
		this.variables = variables;
		this.factors = factors;
		this.functions = functions;
	}
}
