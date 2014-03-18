package com.indago.fg;



public interface Function< D extends Domain< ? >, V extends Value< ?, D > > {

	public FunctionDomain< D > getDomain();

	public double evaluate( V... values );

}
