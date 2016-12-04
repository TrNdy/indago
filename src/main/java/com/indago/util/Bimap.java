package com.indago.util;

import java.util.HashMap;
import java.util.Map;

public class Bimap< A, B > {

	private final Map< A, B > ab = new HashMap< A, B >();
	private final Map< B, A > ba = new HashMap< B, A >();

	public A getA( final B b ) {
		return ba.get( b );
	}

	public B getB( final A a ) {
		return ab.get( a );
	}

	public void add( final A a, final B b ) {
		ab.put( a, b );
		ba.put( b, a );
	}

	public int size() {
		return ab.size();
	}
}