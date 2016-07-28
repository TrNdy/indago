/**
 *
 */
package com.indago.costs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.indago.data.segmentation.LabelingSegment;

import net.imglib2.Cursor;

/**
 * @author jug
 */
public class SegmentCostUtils {

	/**
	 * @param sourceImage
	 * @param s2_1
	 * @param s2_2
	 * @return
	 */
	public static SimpleRegression getSimpleRegressionOfSegmentPixels( final LabelingSegment... segments ) {
		final SimpleRegression regression = new SimpleRegression( false );

		final List< Point > points = new ArrayList<>();
		double avgX = 0;
		double avgY = 0;
		int n = 0;
		for ( final LabelingSegment s : segments ) {
			final Cursor< ? > cSegment = s.getRegion().cursor();
			while ( cSegment.hasNext() ) {
				cSegment.fwd();
				final int[] pos = new int[ 2 ];
				cSegment.localize( pos );
				points.add( new Point( pos[ 0 ], pos[ 1 ] ) );
				n++;
				avgX += pos[ 0 ];
				avgY += pos[ 1 ];
			}
		}

		avgX /= n;
		avgY /= n;
		for ( final Point point : points ) {
			regression.addData( point.getX() - avgX, point.getY() - avgY );
		}

		return regression;
	}

}
