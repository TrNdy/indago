package com.indago.tracking;

import java.util.Collection;

import com.indago.tracking.map.AssignmentVars;
import com.indago.tracking.seg.ConflictSet;
import com.indago.tracking.seg.SegmentVar;

public interface SegmentationProblem {

	public int getTime();

	public Collection< SegmentVar > getSegments();

	public Collection< ConflictSet > getConflictSets();

	public AssignmentVars getInAssignments();

	public AssignmentVars getOutAssignments();
}