package com.indago.fg.scalar;

import java.util.Arrays;
import java.util.List;

import com.indago.fg.Factor;

public class EnumeratedFactor implements Factor< EnumeratedDomain, EnumeratedVariable, EnumeratedFunction > {

	protected final EnumeratedFunctionDomain domain;

	protected EnumeratedFunction function;

	protected final EnumeratedVariable[] variables;

	private final int id;

	public EnumeratedFactor( final EnumeratedFunctionDomain domain, final int id ) {
		this.domain = domain;
		this.id = id;
		function = null;
		variables = new EnumeratedVariable[ domain.numDimensions() ];
	}

	@Override
	public EnumeratedFunctionDomain getDomain() {
		return domain;
	}

	@Override
	public void setFunction( final EnumeratedFunction function ) {
		this.function = function;
	}

	@Override
	public EnumeratedFunction getFunction() {
		return function;
	}

	@Override
	public void setVariable( final int index, final EnumeratedVariable v ) {
		variables[ index ] = v;
	}

	@Override
	public void setVariables( final EnumeratedVariable... variables ) {
		for ( int i = 0; i < variables.length; ++i )
			setVariable( i, variables[ i ] );
	}

	@Override
	public EnumeratedVariable getVariable( final int index ) {
		return variables[ index ];
	}

	@Override
	public List< ? extends EnumeratedVariable > getVariables() {
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
