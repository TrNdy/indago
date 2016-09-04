/**
 *
 */
package com.indago.log;

/**
 * @author jug
 */
public class Log {

	public void print( final String message ) {
		System.out.println( message );
	}

	public void warn( final String message ) {
		System.out.println( message );
	}

	public void error( final String message ) {
		System.err.println( message );
	}
}
