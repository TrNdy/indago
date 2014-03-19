package com.indago.fg.factor;

import java.util.List;

import com.indago.fg.domain.Domain;
import com.indago.fg.domain.FunctionDomain;
import com.indago.fg.function.Function;
import com.indago.fg.variable.Variable;

public interface Factor< D extends Domain< ? >, V extends Variable< D >, F extends Function< D, ? > > {

	public FunctionDomain< D > getDomain();

	public void setFunction( F function );

	public F getFunction();

	public void setVariable( int index, V v );

	public void setVariables( V... variables );

	public V getVariable( int index );

	public List< ? extends V > getVariables();
}
