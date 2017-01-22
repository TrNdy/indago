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

	public Set< SegmentNode > getForcedSegmentNodeMovesTo();

	public Set< SegmentNode > getForcedSegmentNodeMovesFrom();

	public Set< SegmentNode > getForcedSegmentNodeDivisionsTo();

	public Set< SegmentNode > getForcedSegmentNodeDivisionsFrom();

	public Set< ConflictSet > getForcedConflictSetMovesTo();

	public Set< ConflictSet > getForcedConflictSetMovesFrom();

	public Set< ConflictSet > getForcedConflictSetDivisionsTo();

	public Set< ConflictSet > getForcedConflictSetDivisionsFrom();

	public Set< SegmentNode > getAvoidedNodes();

}