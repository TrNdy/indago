package com.indago.fg.domain;

import com.indago.fg.util.Typed;

public interface Domain< T > extends Typed< T > {

	public int size();

	public T getElement( int i );

	public int getElementIndex( T element );
}
