package com.indago.tracking.map;

import com.indago.tracking.seg.SegmentVar;

public interface DivisionHypothesis extends AssignmentVar {

	public SegmentVar getSrc();

	public SegmentVar getDest1();

	public SegmentVar getDest2();
}