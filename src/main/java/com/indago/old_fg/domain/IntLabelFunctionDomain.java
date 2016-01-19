package com.indago.old_fg.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class IntLabelFunctionDomain implements FunctionDomain< IntLabelDomain > {

	private final List< IntLabelDomain > domains;

	public IntLabelFunctionDomain( final int... sizes ) {
		final IntLabelDomain[] domains = new IntLabelDomain[ sizes.length ];
		for ( int d = 0; d < sizes.length; ++d )
			domains[ d ] = IntLabelDomain.getForSize( sizes[ d ] );
		this.domains = Arrays.asList( domains );
	}

	@Override
	public List< ? extends IntLabelDomain > argumentDomains() {
		return domains;
	}

	@Override
	public int numDimensions() {
		return domains.size();
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( !( obj instanceof IntLabelFunctionDomain ) ) return false;
		final IntLabelFunctionDomain dom = ( IntLabelFunctionDomain ) obj;
		if ( dom.domains.size() != domains.size() ) return false;
		for ( int d = 0; d < domains.size(); ++d )
			if ( !dom.domains.get( d ).equals( domains.get( d ) ) )
				return false;
		return true;
	}

	@Override
	public int hashCode() {
		int value = 1;
		for ( final IntLabelDomain d : domains )
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

	protected static final HashMap< IntLabelFunctionDomain, IntLabelFunctionDomain > functionDomains = new HashMap< IntLabelFunctionDomain, IntLabelFunctionDomain >();

	public static IntLabelFunctionDomain getForSizes( final int... sizes ) {
		final IntLabelFunctionDomain fd = new IntLabelFunctionDomain( sizes );
		final IntLabelFunctionDomain cached = functionDomains.get( fd );
		if ( cached == null ) {
			functionDomains.put( fd, fd );
			return fd;
		} else
			return cached;
	}

	public static IntLabelFunctionDomain getArbitrary( final int numDimensions ) {
		final int[] sizes = new int[ numDimensions ];
		Arrays.fill( sizes, IntLabelDomain.ARBITRARY );
		return getForSizes( sizes );
	}

}
