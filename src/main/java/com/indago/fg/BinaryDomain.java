package com.indago.fg;

public class BinaryDomain< T > implements LabelDomain< T > {

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

}
