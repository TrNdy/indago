/**
 *
 */
package com.indago;

import org.scijava.log.DefaultLogger;
import org.scijava.log.LogLevel;
import org.scijava.log.LogSource;
import org.scijava.log.Logger;
/**
 * @author jug
 */
public class IndagoLog {

	// NB: Tr2d replaces this logger during runtime, when it sets up it's logging panel.
	public static Logger log = stdLogger().subLogger("Indago");

	@Deprecated
	public static Logger stderrLogger() {
		return stdLogger();
	}

	public static Logger stdLogger() {
		return new DefaultLogger(System.out::print, LogSource.newRoot(), LogLevel.INFO);
	}
}
