package com.indago.fg.scalar;

import com.indago.fg.Function;
import com.indago.fg.LabelValue;

public interface EnumeratedFunction extends Function< EnumeratedDomain, LabelValue< ?, EnumeratedDomain > > {

	@Override
	public EnumeratedFunctionDomain getDomain();

}
