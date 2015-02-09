/**
 *
 */
package com.indago.segment.features;

import java.util.HashMap;
import java.util.Map;

import com.indago.segment.Segment;


/**
 * Holds a set of computed features for a given set of segments.
 *
 * @author jug
 */
public class FeatureMatrix {

	Map<Segment, Map<String,Double>> data;

	public FeatureMatrix() {
		data = new HashMap< Segment, Map< String, Double >>();
	}

	public void addRow( final Segment segment, final Map< String, Double > row ) {
		data.put( segment, row );
	}

	public double getValue( final Segment rowId, final String featureName ) {

		final Map< String, Double > row = data.get( rowId );
		if ( row == null ) return Double.NaN;
		final Double value = row.get( featureName );
		if ( value == null ) return Double.NaN;

		return value.doubleValue();
	}
}
