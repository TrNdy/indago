package com.indago.tracking.map;

import com.indago.tracking.seg.SegmentVar;

public interface MoveHypothesis extends AssignmentVar
{
	SegmentVar getSrc();

	SegmentVar getDest();
}
