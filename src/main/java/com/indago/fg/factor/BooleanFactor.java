package com.indago.fg.factor;

import java.util.Arrays;
import java.util.List;

import com.indago.fg.domain.BooleanDomain;
import com.indago.fg.domain.BooleanFunctionDomain;
import com.indago.fg.function.BooleanFunction;
import com.indago.fg.variable.BooleanVariable;

/**
 * @author jug
 */
public class BooleanFactor implements Factor< BooleanDomain, BooleanVariable, BooleanFunction > {

	protected final BooleanFunctionDomain domain;

	protected BooleanFunction function;

	protected final BooleanVariable[] variables;

	private final int id;

	public BooleanFactor( final BooleanFunctionDomain domain, final int id ) {
		this.domain = domain;
		this.id = id;
		function = null;
		variables = new BooleanVariable[ domain.numDimensions() ];
	}

	@Override
	public BooleanFunctionDomain getDomain() {
		return domain;
	}

	@Override
	public void setFunction( final BooleanFunction function ) {
		this.function = function;
	}

	@Override
	public BooleanFunction getFunction() {
		return function;
	}

	@Override
	public void setVariable( final int index, final BooleanVariable v ) {
		variables[ index ] = v;
		v.addFactor( this );
	}

	@Override
	public void setVariables( final BooleanVariable... variables ) {
		for ( int i = 0; i < variables.length; ++i )
			setVariable( i, variables[ i ] );
	}

	@Override
	public BooleanVariable getVariable( final int index ) {
		return variables[ index ];
	}

	@Override
	public List< ? extends BooleanVariable > getVariables() {
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
