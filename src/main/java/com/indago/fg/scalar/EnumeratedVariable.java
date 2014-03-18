package com.indago.fg.scalar;

import com.indago.fg.Variable;

public class EnumeratedVariable implements Variable< EnumeratedDomain > {

	private final EnumeratedDomain domain;
	private final int id;

	public EnumeratedVariable( final int numStates, final int id ) {
		this( EnumeratedDomain.getForSize( numStates ), id );
	}

	public EnumeratedVariable( final EnumeratedDomain domain, final int id ) {
		this.domain = domain;
		this.id = id;
	}

	@Override
	public EnumeratedDomain getType() {
		return domain;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + id + ")";
	}
}
