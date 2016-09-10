/**
 *
 */
package com.indago.util;

import com.indago.IndagoLog;

/**
 * @author jug
 */
public class TicToc {

	private boolean isTicked = false;
	private long t0 = 0;
	private long t1 = 0;

	public long tic() {
		isTicked = true;
		t0 = System.nanoTime();
		return t0;
	}

	public long tic( final String message ) {
		final long ret = tic();
		IndagoLog.log.info( String.format( "t0=%d - %s", ret / 1000000, message ) );
		return ret;
	}

	public long toc() {
		isTicked = false;
		t1 = System.nanoTime();
		return t1 - t0;
	}

	public long toc( final String message ) {
		final long ret = toc();
		final long ms = ret / 1000000;
		IndagoLog.log.info( String.format( "dt=%d - %s", ms, message ) );
		return ret;
	}

	public long getLatest() {
		if (!isTicked) {
			return t1-t0;
		} else {
			throw new IllegalStateException("toc() not called before trying to retrieve latest time span...");
		}
	}
}
