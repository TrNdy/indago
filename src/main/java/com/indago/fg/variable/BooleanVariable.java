package com.indago.fg.variable;

import com.indago.fg.domain.BooleanDomain;

public class BooleanVariable implements Variable< BooleanDomain > {

	private final BooleanDomain domain;
	private final Boolean value;

	public BooleanVariable( final Boolean value ) {
		this( new BooleanDomain(), value );
	}

	public BooleanVariable( final BooleanDomain domain, final Boolean value ) {
		this.domain = domain;
		this.value = value;
	}

	@Override
	public BooleanDomain getType() {
		return domain;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + value + ")";
	}
}
