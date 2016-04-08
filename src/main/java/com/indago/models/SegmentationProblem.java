package com.indago.models;

import java.util.Collection;

import com.indago.models.segments.ConflictSet;
import com.indago.models.segments.SegmentNode;

public interface SegmentationProblem {

	public int getTime();

	public Collection< SegmentNode > getSegments();

	public Collection< ConflictSet > getConflictSets();

}