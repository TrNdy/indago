package com.indago.old_fg.function;

import com.indago.old_fg.domain.IntLabelDomain;
import com.indago.old_fg.domain.IntLabelFunctionDomain;
import com.indago.old_fg.value.Value;

public interface IntLabelFunction extends Function< IntLabelDomain, Value< Integer, IntLabelDomain > > {

	@Override
	public IntLabelFunctionDomain getDomain();

}
