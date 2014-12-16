package com.indago.fg.value;

import com.indago.fg.domain.Domain;

public interface Value< T, D extends Domain< T > > {

	public T get();

	public int getAsIndex();

	public void set( T value );

	public void setAsIndex( int index );
}
