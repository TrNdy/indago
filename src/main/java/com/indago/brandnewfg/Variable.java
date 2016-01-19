package com.indago.brandnewfg;

import java.util.ArrayList;

public class Variable {

	private final ArrayList< Factor > factors;

	public Variable() {
		factors = new ArrayList<>();
	}

	public Domain getDomain() {
		return null; // TODO
	}

	public void addFactor( final Factor factor ) {
		factors.add( factor );
	}

	// getFactors() ???
}
