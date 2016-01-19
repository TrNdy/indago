package com.indago.old_fg.function;

import com.indago.old_fg.domain.Domain;
import com.indago.old_fg.domain.FunctionDomain;
import com.indago.old_fg.value.Value;



public interface Function< D extends Domain< ? >, V extends Value< ?, D > > {

	public FunctionDomain< D > getDomain();

	public double evaluate( V... values );

}
