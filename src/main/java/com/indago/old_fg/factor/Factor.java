package com.indago.old_fg.factor;

import java.util.List;

import com.indago.old_fg.domain.Domain;
import com.indago.old_fg.domain.FunctionDomain;
import com.indago.old_fg.function.Function;
import com.indago.old_fg.gui.FgNode;
import com.indago.old_fg.variable.Variable;

public interface Factor< D extends Domain< ? >, V extends Variable< D >, F extends Function< D, ? > > extends FgNode {

	public FunctionDomain< D > getDomain();

	public void setFunction( F function );

	public F getFunction();

	public void setVariable( int index, V v );

	public void setVariables( V... variables );

	public V getVariable( int index );

	public List< ? extends V > getVariables();
}