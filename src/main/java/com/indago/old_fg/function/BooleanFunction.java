package com.indago.old_fg.function;

import com.indago.old_fg.domain.BooleanDomain;
import com.indago.old_fg.domain.BooleanFunctionDomain;
import com.indago.old_fg.value.Value;

public interface BooleanFunction extends Function< BooleanDomain, Value< Boolean, BooleanDomain > > {

	@Override
	public BooleanFunctionDomain getDomain();

}
