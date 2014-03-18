package com.indago.fg;

public interface LabelValue< T, D extends LabelDomain< T > > extends Value< T, D > {

	public int getAsIndex();

	public void setAsIndex( int index );
}
