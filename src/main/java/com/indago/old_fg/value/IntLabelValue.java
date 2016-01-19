/**
 *
 */
package com.indago.old_fg.value;

import com.indago.old_fg.domain.IntLabelDomain;

/**
 * @author jug
 */
public class IntLabelValue implements Value< Integer, IntLabelDomain > {

	private final IntLabelDomain domain;
	private Integer value;

	public IntLabelValue( final Integer value, final IntLabelDomain domain ) {
		this.domain = domain;
		this.value = value;
	}

	/**
	 * @see com.indago.old_fg.value.Value#get()
	 */
	@Override
	public Integer get() {
		return value;
	}

	/**
	 * @see com.indago.old_fg.value.Value#getAsIndex()
	 */
	@Override
	public int getAsIndex() {
		return value;
	}

	/**
	 * @see com.indago.old_fg.value.Value#set()
	 */
	@Override
	public void set( final Integer value ) {
		for ( int i = 0; i < domain.size(); i++ ) {
			if ( value.equals( domain.getElement( i ) ) ) {
				this.value = value;
				break;
			}
		}
		throw new IllegalArgumentException( "Integer value to be set does not comply IntLabelDomain of this Value instance!" );
	}

	/**
	 * @see com.indago.old_fg.value.Value#setAsIndex(int)
	 */
	@Override
	public void setAsIndex( final int index ) {
		this.set( index );
	}

}
