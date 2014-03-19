package com.indago.fg.value;

import com.indago.fg.domain.Domain;
import com.indago.fg.util.Typed;

public interface Value< T, D extends Domain< T > > extends Typed< D > {

	public T get();

	public int getAsIndex();

	public void set();

	public void setAsIndex( int index );
}
