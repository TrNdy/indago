/**
 *
 */
package com.indago.util;

import com.indago.IndagoLog;

/**
 * @author jug
 *
 */
public class OSValidator {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static void main(final String[] args) {

		IndagoLog.log.info( OS );

		if ( isWindows() ) {
			IndagoLog.log.info( "This is Windows" );
		} else if ( isMac() ) {
			IndagoLog.log.info( "This is Mac" );
		} else if ( isUnix() ) {
			IndagoLog.log.info( "This is Unix or Linux" );
		} else if ( isSolaris() ) {
			IndagoLog.log.info( "This is Solaris" );
		} else {
			IndagoLog.log.info( "Your OS is not support!!" );
		}
    }

    public static boolean isWindows() {

		return ( OS.indexOf( "win" ) >= 0 );

    }

    public static boolean isMac() {

		return ( OS.indexOf( "mac" ) >= 0 );

    }

    public static boolean isUnix() {

		return ( OS.indexOf( "nix" ) >= 0 || OS.indexOf( "nux" ) >= 0 || OS.indexOf( "aix" ) > 0 );

    }

    public static boolean isSolaris() {

		return ( OS.indexOf( "sunos" ) >= 0 );

    }

}
