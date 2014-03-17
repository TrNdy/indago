package com.indago.fg;

public interface Value< T, D extends Domain< T > > extends Typed< D > {

	public T get();

	public void set();
}
