/**
 * 
 */
package com.indago.segment.fg;

import java.util.HashMap;
import java.util.Map;

import com.indago.segment.Segment;
import com.indago.segment.SegmentMultiForest;

/**
 * @author jug
 */
public class VariableSetFactory {

	/**
	 * @param segmentMultiForest
	 * @return
	 */
	public static Map< Segment, SegmentHypothesisVariable > getSegmentMultiForestVariableDictionary( final SegmentMultiForest segmentMultiForest ) {
		final Map< Segment, SegmentHypothesisVariable > ret = new HashMap< Segment, SegmentHypothesisVariable >();
		for ( final Segment segment : segmentMultiForest.getAllSegments() ) {
			ret.put( segment, new SegmentHypothesisVariable( segment ) );
		}
		return ret;
	}

}
