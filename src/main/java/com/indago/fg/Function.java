package com.indago.fg;

import java.util.List;

public interface Function {

	public List< Domain > getArgumentDomains();

	public double evaluate( final int... arguments );
}
