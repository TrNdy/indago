package com.indago.segment.fg;

import java.util.Map;

import com.indago.fg.FactorGraph;
import com.indago.segment.Segment;

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
}