package com.indago.tracking.map;

import java.util.Collection;

public interface AssignmentVars {

	public Collection< AssignmentVar > getAllAssignments();

	public Collection< MoveHypothesis > getMoves();

	public Collection< DivisionHypothesis > getDivisions();

	public Collection< AppearanceHypothesis > getAppearances();

	public Collection< DisappearanceHypothesis > getDisappearances();
}
