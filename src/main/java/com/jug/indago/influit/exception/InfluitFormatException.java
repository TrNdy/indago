/**
 *
 */
package com.jug.indago.influit.exception;


/**
 * @author jug
 */
public class InfluitFormatException extends Exception {

	private static final long serialVersionUID = -8887626788506798826L;

	public InfluitFormatException( final Exception e ) {
		super( "Not supported InfluitDatum requested!", e );
		this.setStackTrace( e.getStackTrace() );
	}

}
