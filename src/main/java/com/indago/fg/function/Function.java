package com.indago.fg.function;

import com.indago.fg.domain.Domain;
import com.indago.fg.domain.FunctionDomain;
import com.indago.fg.value.Value;



public interface Function< D extends Domain< ? >, V extends Value< ?, D > > {

	public FunctionDomain< D > getDomain();

	public double evaluate( V... values );

}
