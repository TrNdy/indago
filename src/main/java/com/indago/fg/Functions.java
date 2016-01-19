package com.indago.fg;

public class Functions {

	public static TensorTable unary( final double... costs ) {
		assert costs.length == 2;
		return new TensorTable(
				costs,
				Domains.getDefaultBinaryArgumentDomains( 1 ) );
	}
}
