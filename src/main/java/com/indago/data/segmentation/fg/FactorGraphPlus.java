package com.indago.data.segmentation.fg;

import java.util.Collection;
import java.util.Map;

import com.indago.data.segmentation.Segment;
import com.indago.fg.FactorGraph;

/**
 * A <code>FactorGraph</code> enriched by a dictionary that maps instances of
 * type <code>T extends Segment</code> to the representing variable in the
 * factor graph.
 *
 * @author pietzsch, jug
 */
public class FactorGraphPlus< T extends Segment > {
	private final FactorGraph factorGraph;

	private final Map< T, SegmentHypothesisVariable< T > > segmentVariableDict;

	public FactorGraphPlus( final FactorGraph factorGraph, final Map< T, SegmentHypothesisVariable< T > > segmentVariableDict )
	{
		this.factorGraph = factorGraph;
		this.segmentVariableDict = segmentVariableDict;
	}

	public FactorGraph getFactorGraph() {
		return factorGraph;
	}

	public Map< T, SegmentHypothesisVariable< T > > getSegmentVariableDictionary() {
		return segmentVariableDict;
	}

	public Collection< SegmentHypothesisVariable< T > > getSegmentVariables() {
		return segmentVariableDict.values();
	}

}
