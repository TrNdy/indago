package com.indago.pg;

import java.util.Collection;

import com.indago.pg.segments.ConflictSet;
import com.indago.pg.segments.SegmentNode;

public interface SegmentationProblem {

	public int getTime();

	public Collection< SegmentNode > getSegments();

	public Collection< ConflictSet > getConflictSets();

}