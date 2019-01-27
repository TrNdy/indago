package com.indago.pg;

import java.util.Set;

import com.indago.pg.segments.ConflictSet;
import com.indago.pg.segments.SegmentNode;

public interface TrackingSegmentationProblem extends SegmentationProblem {

	public int getTime();

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

}