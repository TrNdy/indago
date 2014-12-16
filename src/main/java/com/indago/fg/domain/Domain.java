package com.indago.fg.domain;


public interface Domain< T > {

	public int size();

	public T getElement( int i );

	public int getElementIndex( T element );
}
