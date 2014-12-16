package com.indago.fg.variable;

import java.util.List;

import com.indago.fg.domain.Domain;
import com.indago.fg.factor.Factor;
import com.indago.fg.gui.FgNode;
import com.indago.fg.util.Typed;

public interface Variable< D extends Domain< ? > > extends Typed< D >, FgNode {

	public void addFactor( Factor< D, ?, ? > factor );

	public List< ? extends Factor< D, ?, ? > > getFactors();

}
