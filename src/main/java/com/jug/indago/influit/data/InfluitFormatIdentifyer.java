/**
 *
 */
package com.jug.indago.influit.data;


/**
 * @author jug
 */
public class InfluitFormatIdentifyer {

	final String id;

	public InfluitFormatIdentifyer( final String id ) {
		this.id = id;
	}

	@Override
	public String toString() {
		if ( id == null ) return "NULL";
		return id;
	}

	@Override
	public boolean equals( final Object o ) {
		if ( o instanceof InfluitFormatIdentifyer ) {
			return this.id.equals( ( ( InfluitFormatIdentifyer ) o ).id );
		}
		return false;
	}

	/**
	 * Default isCompatible method.
	 * 
	 * @param otherFormat
	 * @return true if
	 *         <code>this.toString().equals( otherFormat.toString() )</code>.
	 */
	public boolean isCompatible( final InfluitFormatIdentifyer otherFormat ) {
		return this.toString().equals( otherFormat.toString() );
	}
}
