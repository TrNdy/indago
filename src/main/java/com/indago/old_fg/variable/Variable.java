package com.indago.old_fg.variable;

import java.util.List;

import com.indago.old_fg.domain.Domain;
import com.indago.old_fg.factor.Factor;
import com.indago.old_fg.gui.FgNode;

public interface Variable< D extends Domain< ? > > extends FgNode {

	public void addFactor( Factor< D, ?, ? > factor );

	public List< ? extends Factor< D, ?, ? > > getFactors();

}
