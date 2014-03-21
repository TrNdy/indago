package com.indago.fg.variable;

import com.indago.fg.domain.IntLabelDomain;

public class IntLabel implements Variable< IntLabelDomain > {

	private final IntLabelDomain domain;
	private final int id;

	public IntLabel( final int numStates, final int id ) {
		this( IntLabelDomain.getForSize( numStates ), id );
	}

	public IntLabel( final IntLabelDomain domain, final int id ) {
		this.domain = domain;
		this.id = id;
	}

	@Override
	public IntLabelDomain getType() {
		return domain;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + id + ")";
	}
}
