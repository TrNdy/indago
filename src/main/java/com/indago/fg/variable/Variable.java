package com.indago.fg.variable;

import com.indago.fg.domain.Domain;
import com.indago.fg.gui.FgNode;
import com.indago.fg.util.Typed;

public interface Variable< D extends Domain< ? > > extends Typed< D >, FgNode {

}
