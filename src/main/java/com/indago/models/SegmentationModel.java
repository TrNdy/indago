package com.indago.models;

import java.util.Collection;

import com.indago.models.assignments.AssignmentVars;
import com.indago.models.segments.ConflictSet;
import com.indago.models.segments.SegmentVar;

public interface SegmentationModel {

	public int getTime();

	public Collection< SegmentVar > getSegments();

	public Collection< ConflictSet > getConflictSets();

	public AssignmentVars getInAssignments();

	public AssignmentVars getOutAssignments();
}