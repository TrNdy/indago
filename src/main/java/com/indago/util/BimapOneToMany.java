package com.indago.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tobias Pietzsch
 * @author Florian Jug
 */
public class BimapOneToMany< A, B > {

	private final Map< A, List< B > > ab = new HashMap<>();
	private final Map< B, A > ba = new HashMap<>();
	private final List< B > valuesBs = new ArrayList<>();

	public A getA( final B b ) {
		return ba.get( b );
	}

	public List< B > getBs( final A a ) {
		return ab.get( a );
	}

	public B getB( final A a ) {
		return getB( a, 0 );
	}

	public B getB( final A a, final int i ) {
		return ab.get( a ).get( i );
	}

	public void add( final A a, final B b ) {
		ab.computeIfAbsent( a, k -> new ArrayList<>() ).add( b );
		ba.put( b, a );
		valuesBs.add( b );
	}

	public void addAll( final A a, final Collection< ? extends B > bs ) {
		ab.computeIfAbsent( a, k -> new ArrayList<>() ).addAll( bs );
		bs.forEach( b -> ba.put( b, a ) );
		valuesBs.addAll( bs );
	}

	public int size() {
		return ab.size();
	}

	public Collection< A > valuesAs() {
		return ba.values();
	}

	public Collection< B > valuesBs() {
		return valuesBs;
	}
}
