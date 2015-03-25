/**
 *
 */
package com.indago.fg.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author jug
 */
public class BooleanFunctionDomain implements FunctionDomain< BooleanDomain > {

	private final List< BooleanDomain > domains;

	protected BooleanFunctionDomain( final int numDimensions ) {
		final BooleanDomain[] domains = new BooleanDomain[ numDimensions ];
		for ( int d = 0; d < numDimensions; ++d )
			domains[ d ] = new BooleanDomain();
		this.domains = Arrays.asList( domains );
	}

	@Override
	public List< ? extends BooleanDomain > argumentDomains() {
		return domains;
	}

	@Override
	public int numDimensions() {
		return domains.size();
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( !( obj instanceof BooleanFunctionDomain ) ) return false;
		final BooleanFunctionDomain dom = ( BooleanFunctionDomain ) obj;
		if ( dom.domains.size() != domains.size() ) return false;
		for ( int d = 0; d < domains.size(); ++d )
			if ( !dom.domains.get( d ).equals( domains.get( d ) ) )
				return false;
		return true;
	}

	@Override
	public int hashCode() {
		int value = 1;
		for ( final BooleanDomain d : domains )
			value = value * 100 + d.size();
		return value;
	}

	@Override
	public String toString() {
		String doms = "" + domains.get( 0 ).size();
		for ( int d = 1; d < domains.size(); ++d )
			doms += " x " + domains.get( d ).size();
		return getClass().getSimpleName() + " (" + doms + ")";
	}

	protected static final HashMap< Integer, BooleanFunctionDomain > functionDomains = new HashMap< Integer, BooleanFunctionDomain >();

	public static BooleanFunctionDomain getForNumDimensions( final int numDimensions ) {
		BooleanFunctionDomain cached = functionDomains.get( numDimensions );
		if ( cached == null ) {
			cached = new BooleanFunctionDomain( numDimensions );
			functionDomains.put( numDimensions, cached );
		}
		return cached;
	}
}
