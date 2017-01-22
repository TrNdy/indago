package com.indago.pg;

import java.util.Collection;
import java.util.Set;

import com.indago.pg.segments.ConflictSet;
import com.indago.pg.segments.SegmentNode;

public interface SegmentationProblem {

	public int getTime();

	public Collection< SegmentNode > getSegments();

	public Collection< ConflictSet > getConflictSets();

	public void force( SegmentNode segVar );

	public void avoid( SegmentNode segVar );

	public Set< SegmentNode > getForcedNodes();

	public Set< SegmentNode > getForcedByAppearanceNodes();

	public Set< SegmentNode > getForcedByDisappearanceNodes();

	public Set< SegmentNode > getForcedByMoveNodesTo();

	public Set< SegmentNode > getForcedByMoveNodesFrom();

	public Set< SegmentNode > getForcedByDivisionNodesTo();

	public Set< SegmentNode > getForcedByDivisionNodesFrom();

	public Set< SegmentNode > getAvoidedNodes();

}