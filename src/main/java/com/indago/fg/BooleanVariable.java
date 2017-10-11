package com.indago.fg;

public class BooleanVariable extends Variable {

	public BooleanVariable() {
		super( Domains.getDefaultBinaryDomain() );
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
