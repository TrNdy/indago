/**
 *
 */
package com.indago.log;

import org.scijava.log.slf4j.SLF4JLogService;
import org.slf4j.Logger;
import org.slf4j.spi.LocationAwareLogger;

/**
 * @author jug
 */
public class Log {

	private static boolean initialized = false;

	private static Logger logger = null;
	private static LocationAwareLogger locationAwareLogger = null;
	private static SLF4JLogService logService = null;

	public static void initialize( final SLF4JLogService logs ) {
		logService = logs;
		logger = logs.getLogger();
		if ( logger instanceof LocationAwareLogger ) {
			locationAwareLogger = ( LocationAwareLogger ) logger;
		} else {
			logger.warn( "SLF4JLogService does not provide a slf4j LocationAwareLogger" );
		}
		initialized = true;
	}

	public static void trace( final String message ) {
		if ( initialized ) {
			if ( locationAwareLogger != null ) {
				locationAwareLogger.log( null, Log.class.getCanonicalName(), LocationAwareLogger.TRACE_INT, message, null, null );
			} else {
				logger.trace( message );
			}
		} else {
			System.err.println( "Used static method without initializing the logger (" + locationAwareLogger.getClass().getCanonicalName() + ")" );
		}
	}

	public static void info( final String message ) {
		if ( initialized ) {
			if ( locationAwareLogger != null ) {
				locationAwareLogger.log( null, Log.class.getCanonicalName(), LocationAwareLogger.INFO_INT, message, null, null );
			} else {
				logger.info( message );
			}
		} else {
			System.err.println( "Used static method without initializing the logger (" + locationAwareLogger.getClass().getCanonicalName() + ")" );
		}
	}

	public static void debug( final String message ) {
		if ( initialized ) {
			if ( locationAwareLogger != null ) {
				locationAwareLogger.log( null, Log.class.getCanonicalName(), LocationAwareLogger.DEBUG_INT, message, null, null );
			} else {
				logger.debug( message );
			}
		} else {
			System.err.println( "Used static method without initializing the logger (" + locationAwareLogger.getClass().getCanonicalName() + ")" );
		}
	}

	public static void warn( final String message ) {
		if ( initialized ) {
			if ( locationAwareLogger != null ) {
				locationAwareLogger.log( null, Log.class.getCanonicalName(), LocationAwareLogger.WARN_INT, message, null, null );
			} else {
				logger.warn( message );
			}
		} else {
			System.err.println( "Used static method without initializing the logger (" + locationAwareLogger.getClass().getCanonicalName() + ")" );
		}
	}

	public static void error( final String message ) {
		if ( initialized ) {
			if ( locationAwareLogger != null ) {
				locationAwareLogger.log( null, Log.class.getCanonicalName(), LocationAwareLogger.ERROR_INT, message, null, null );
			} else {
				logger.error( message );
			}
		} else {
			System.err.println( "Used static method without initializing the logger (" + locationAwareLogger.getClass().getCanonicalName() + ")" );
		}
	}
}
