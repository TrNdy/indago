package com.indago.fg.scalar;

import java.util.HashMap;

import com.indago.fg.LabelDomain;

public class EnumeratedDomain implements LabelDomain< Integer > {

	public static final int ARBITRARY = -1;

	protected final int size;

	public EnumeratedDomain( final int size ) {
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
		if ( !( obj instanceof EnumeratedDomain ) )
			return false;
		return size == ( ( EnumeratedDomain ) obj ).size;
	}

	@Override
	public int hashCode() {
		return size;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + size +"]";
	}

	protected static final HashMap< Integer, EnumeratedDomain > domains = new HashMap< Integer, EnumeratedDomain >();

	public static EnumeratedDomain getForSize( final int size ) {
		EnumeratedDomain d = domains.get( new Integer( size ) );
		if ( d == null ) {
			d = new EnumeratedDomain( size );
			domains.put( size, d );
		}
		return d;
	}
}
