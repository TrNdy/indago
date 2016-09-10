/**
 *
 */
package com.indago.util.math;

/**
 * @author jug
 */
public class VectorUtil {

	public static int checkSameSize( final double[] pos1, final double[] pos2 ) {
		if ( pos1.length != pos2.length ) { throw new IllegalArgumentException( "Size mismatch!" ); }
		return pos1.length;
	}

	public static double getSquaredDistance( final double[] pos1, final double[] pos2 ) {
		final int len = checkSameSize( pos1, pos2 );
		double sqDist = 0;
		for ( int i = 0; i < len; i++ ) {
			final double d = pos1[ i ] - pos2[ i ];
			sqDist += d * d;
		}
		return sqDist;
	}

	public static double getDistance( final double[] pos1, final double[] pos2 ) {
		return Math.sqrt( getSquaredDistance( pos1, pos2 ) );
	}
}
