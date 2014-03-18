package com.indago.fg;

import java.util.List;

public interface Factor< D extends Domain< ? >, V extends Variable< D >, F extends Function< D, ? > > {

	public FunctionDomain< D > getDomain();

	public void setFunction( F function );

	public F getFunction();

	public void setVariable( int index, V v );

	public void setVariables( V... variables );

	public V getVariable( int index );

	public List< ? extends V > getVariables();
}
