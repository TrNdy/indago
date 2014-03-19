package com.indago.fg.domain;

import java.util.List;

public interface FunctionDomain< D extends Domain< ? > > {

	public List< ? extends D > argumentDomains();

	public int numDimensions();

}
