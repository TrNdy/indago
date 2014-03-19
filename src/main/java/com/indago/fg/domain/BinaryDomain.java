package com.indago.fg.domain;

public class BinaryDomain< T > implements Domain< T > {

	private final T zero;
	private final T one;

	public BinaryDomain( final T zero, final T one ) {
		this.zero = zero;
		this.one = one;
	}

	@Override
	public T getType() {
		return zero;
	}

	@Override
	public int size() {
		return 2;
	}

	@Override
	public T getElement( final int i ) {
		if ( i == 0 )
			return zero;
		else if ( i == 1 )
			return one;
		else
			throw new IllegalArgumentException();
	}

	@Override
	public int getElementIndex( final T element ) {
		if ( zero.equals( element ) )
			return 0;
		else if ( one.equals( element ) )
			return 1;
		else
			throw new IllegalArgumentException();
	}

}
