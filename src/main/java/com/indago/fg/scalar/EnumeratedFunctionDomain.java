package com.indago.fg.scalar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.indago.fg.FunctionDomain;

public class EnumeratedFunctionDomain implements FunctionDomain< EnumeratedDomain > {

	private final List< EnumeratedDomain > domains;

	public EnumeratedFunctionDomain( final int... sizes ) {
		final EnumeratedDomain[] domains = new EnumeratedDomain[ sizes.length ];
		for ( int d = 0; d < sizes.length; ++d )
			domains[ d ] = EnumeratedDomain.getForSize( sizes[ d ] );
		this.domains = Arrays.asList( domains );
	}

	@Override
	public List< ? extends EnumeratedDomain > argumentDomains() {
		return domains;
	}

	@Override
	public int numDimensions() {
		return domains.size();
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( !( obj instanceof EnumeratedFunctionDomain ) ) return false;
		final EnumeratedFunctionDomain dom = ( EnumeratedFunctionDomain ) obj;
		if ( dom.domains.size() != domains.size() ) return false;
		for ( int d = 0; d < domains.size(); ++d )
			if ( !dom.domains.get( d ).equals( domains.get( d ) ) )
				return false;
		return true;
	}

	@Override
	public int hashCode() {
		int value = 1;
		for ( final EnumeratedDomain d : domains )
			value = value * 100 + d.size();
		return value;
	}

	@Override
	public String toString() {
		String doms = domains.get( 0 ).toString();
		for ( int d = 1; d < domains.size(); ++d )
			doms += " x " + domains.get( d );
		return getClass().getSimpleName() + "(" + doms + ")";
	}

	protected static final HashMap< EnumeratedFunctionDomain, EnumeratedFunctionDomain > functionDomains = new HashMap< EnumeratedFunctionDomain, EnumeratedFunctionDomain >();

	public static EnumeratedFunctionDomain getForSizes( final int... sizes ) {
		final EnumeratedFunctionDomain fd = new EnumeratedFunctionDomain( sizes );
		final EnumeratedFunctionDomain cached = functionDomains.get( fd );
		if ( cached == null ) {
			functionDomains.put( fd, fd );
			return fd;
		} else
			return cached;
	}
}
