package com.indago.fg.variable;

import java.util.List;

import com.indago.fg.domain.Domain;
import com.indago.fg.factor.Factor;
import com.indago.fg.gui.FgNode;

public interface Variable< D extends Domain< ? > > extends FgNode {

	public void addFactor( Factor< D, ?, ? > factor );

	public List< ? extends Factor< D, ?, ? > > getFactors();

	public void addFactor( Factor< D, ?, ? > factor );

	public List< ? extends Factor< D, ?, ? > > getFactors();

}
