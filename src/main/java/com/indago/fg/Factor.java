package com.indago.fg;

import java.util.List;

public interface Factor< F extends Function< ? > > {

	public FunctionDomain getDomain();

	public void setFunction( F function );

	public F getFunction();

	public void setVariable( int index, Variable< ? > v );

	public void setVariables( Variable< ? >... variables );

	public Variable< ? > getVariable( int index );

	public List< ? extends Variable< ? > > getVariables();
}
