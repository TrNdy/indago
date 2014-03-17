package com.indago.fg;



public interface Function< D extends Domain< ? > > {

	public FunctionDomain getDomain();

	public double evaluate( Value< ?, D >... values );

}
