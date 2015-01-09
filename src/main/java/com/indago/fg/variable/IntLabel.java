package com.indago.fg.variable;

import java.util.ArrayList;
import java.util.List;

import com.indago.fg.domain.IntLabelDomain;
import com.indago.fg.factor.Factor;

public class IntLabel implements Variable< IntLabelDomain > {

	private final List< Factor< IntLabelDomain, ?, ? >> factors = new ArrayList< Factor< IntLabelDomain, ?, ? >>();

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
	public String toString() {
		return getClass().getSimpleName() + "(" + id + ")";
	}

	/**
	 * @see com.indago.fg.variable.Variable#addFactor(com.indago.fg.factor.Factor)
	 */
	@Override
	public void addFactor( final Factor< IntLabelDomain, ?, ? > factor ) {
		factors.add( factor );
	}

	/**
	 * @see com.indago.fg.variable.Variable#getFactors()
	 */
	@Override
	public List< ? extends Factor< IntLabelDomain, ?, ? >> getFactors() {
		return factors;
	}

	/**
	 * @see com.indago.fg.variable.Variable#addFactor(com.indago.fg.factor.Factor)
	 */
	@Override
	public void addFactor( final Factor< IntLabelDomain, ?, ? > factor ) {
		factors.add( factor );
	}

	/**
	 * @see com.indago.fg.variable.Variable#getFactors()
	 */
	@Override
	public List< ? extends Factor< IntLabelDomain, ?, ? >> getFactors() {
		return factors;
	}
}
