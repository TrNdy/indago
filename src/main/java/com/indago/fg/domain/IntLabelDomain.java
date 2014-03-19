package com.indago.fg.domain;

import java.util.HashMap;

public class IntLabelDomain implements Domain< Integer > {

	public static final int ARBITRARY = -1;

	protected final int size;

	public IntLabelDomain( final int size ) {
		this.size = size;
	}

	@Override
	public Integer getType() {
		return 0;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Integer getElement( final int i ) {
		assert i > 0 && i < size;
		return i;
	}

	@Override
	public int getElementIndex( final Integer element ) {
		assert element != null && element > 0 && element < size;
		return element;
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( !( obj instanceof IntLabelDomain ) )
			return false;
		return size == ( ( IntLabelDomain ) obj ).size;
	}

	@Override
	public int hashCode() {
		return size;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + size +"]";
	}

	protected static final HashMap< Integer, IntLabelDomain > domains = new HashMap< Integer, IntLabelDomain >();

	public static IntLabelDomain getForSize( final int size ) {
		IntLabelDomain d = domains.get( new Integer( size ) );
		if ( d == null ) {
			d = new IntLabelDomain( size );
			domains.put( size, d );
		}
		return d;
	}
}
