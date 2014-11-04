package com.indago.fg.function;

import com.indago.fg.domain.BooleanDomain;
import com.indago.fg.domain.BooleanFunctionDomain;
import com.indago.fg.value.Value;

public interface BooleanFunction extends Function< BooleanDomain, Value< Boolean, BooleanDomain > > {

	@Override
	public BooleanFunctionDomain getDomain();

}
