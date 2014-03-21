package com.indago.fg.function;

import com.indago.fg.domain.IntLabelDomain;
import com.indago.fg.domain.IntLabelFunctionDomain;
import com.indago.fg.value.Value;

public interface IntLabelFunction extends Function< IntLabelDomain, Value< Integer, IntLabelDomain > > {

	@Override
	public IntLabelFunctionDomain getDomain();

}
