/**
 *
 */
package com.indago.fg.value;

import com.indago.fg.domain.BooleanDomain;

/**
 * @author jug
 */
public class BooleanValue implements Value< Boolean, BooleanDomain > {
	public static final BooleanValue TRUE = new BooleanValue( Boolean.TRUE );
	public static final BooleanValue FALSE = new BooleanValue( Boolean.FALSE );

	private Boolean value;

	public BooleanValue( final Boolean value ) {
		this.value = value;
	}

	/**
	 * @see com.indago.fg.value.Value#get()
	 */
	@Override
	public Boolean get() {
		return value;
	}

	/**
	 * @see com.indago.fg.value.Value#getAsIndex()
	 */
	@Override
	public int getAsIndex() {
		return ( value.booleanValue() ) ? 1 : 0;
	}

	/**
	 * @see com.indago.fg.value.Value#set()
	 */
	@Override
	public void set( final Boolean value ) {
		this.value = value;
	}

	/**
	 * @see com.indago.fg.value.Value#setAsIndex(int)
	 */
	@Override
	public void setAsIndex( final int index ) {
		if ( index == 1 ) {
			this.set( Boolean.TRUE );
		} else {
			this.set( Boolean.FALSE );
		}
	}

	@Override
	public String toString() {
		return value ? "true" : "false";
	}

}
