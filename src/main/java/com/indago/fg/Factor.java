package com.indago.fg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Factor {

	private final Function function;

	private final ArrayList< Variable > variables;

	public Factor( final Function function, final List< ? extends Variable > variables ) {
		assert variables.size() > 0;
		this.function = function;
		this.variables = new ArrayList<>( variables );
		for ( final Variable variable : variables ) {
			variable.addFactor( this );
		}
	}

	public Factor( final Function function, final Variable... variables ) {
		this( function, Arrays.asList( variables ) );
	}

	public Function getFunction() {
		return function;
	}

	public List< Variable > getVariables() {
		return variables;
	}

	public int getArity() {
		return variables.size();
	}
}
