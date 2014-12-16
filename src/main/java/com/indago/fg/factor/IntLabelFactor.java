package com.indago.fg.factor;

import java.util.Arrays;
import java.util.List;

import com.indago.fg.domain.IntLabelDomain;
import com.indago.fg.domain.IntLabelFunctionDomain;
import com.indago.fg.function.IntLabelFunction;
import com.indago.fg.variable.IntLabel;

public class IntLabelFactor implements Factor< IntLabelDomain, IntLabel, IntLabelFunction > {

	protected final IntLabelFunctionDomain domain;

	protected IntLabelFunction function;

	protected final IntLabel[] variables;

	private final int id;

	public IntLabelFactor( final IntLabelFunctionDomain domain, final int id ) {
		this.domain = domain;
		this.id = id;
		function = null;
		variables = new IntLabel[ domain.numDimensions() ];
	}

	@Override
	public IntLabelFunctionDomain getDomain() {
		return domain;
	}

	@Override
	public void setFunction( final IntLabelFunction function ) {
		this.function = function;
	}

	@Override
	public IntLabelFunction getFunction() {
		return function;
	}

	@Override
	public void setVariable( final int index, final IntLabel v ) {
		variables[ index ] = v;
		v.addFactor( this );
	}

	@Override
	public void setVariables( final IntLabel... variables ) {
		for ( int i = 0; i < variables.length; ++i )
			setVariable( i, variables[ i ] );
	}

	@Override
	public IntLabel getVariable( final int index ) {
		return variables[ index ];
	}

	@Override
	public List< ? extends IntLabel > getVariables() {
		return Arrays.asList( variables );
	}

	@Override
	public String toString() {
		String arguments = "" + variables[ 0 ];
		for ( int i = 1; i < variables.length; ++i )
			arguments += ", " + variables[ i ];
		return getClass().getSimpleName() + "(" + id + ") = " + function + " ( " + arguments + " )";
	}
}
