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

	public static Logger log = stdLogger().subLogger("Indago");

	public static Logger stdLogger() {
		return new DefaultLogger(System.out::print, LogSource.newRoot(), LogLevel.INFO);
	}
}
