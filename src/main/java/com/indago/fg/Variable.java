package com.indago.fg;

import java.util.ArrayList;

public class Variable {

	private final ArrayList< Factor > factors;

	private final Domain domain;

	public Variable( final Domain domain ) {
		this.domain = domain;
		factors = new ArrayList<>();
	}

	public Domain getDomain() {
		return domain;
	}

	public void addFactor( final Factor factor ) {
		factors.add( factor );
	}

	// getFactors() ???
}
