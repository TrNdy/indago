package com.indago.tracking.map;

import java.util.ArrayList;
import java.util.Collection;

public class AssignmentVars {

	Collection< AssignmentVar > all = new ArrayList< AssignmentVar >();
	Collection< MovementHypothesis > moves = new ArrayList< MovementHypothesis >();
	Collection< DivisionHypothesis > divisions = new ArrayList< DivisionHypothesis >();
	Collection< AppearanceHypothesis > apps = new ArrayList< AppearanceHypothesis >();
	Collection< DisappearanceHypothesis > disapps = new ArrayList< DisappearanceHypothesis >();

	public Collection< AssignmentVar > getAllAssignments() {
		return all;
	}

	public Collection< MovementHypothesis > getMoves() {
		return moves;
	}

	public Collection< DivisionHypothesis > getDivisions() {
		return divisions;
	}

	public Collection< AppearanceHypothesis > getAppearances() {
		return apps;
	}

	public Collection< DisappearanceHypothesis > getDisappearances() {
		return disapps;
	}

	public void add( final AssignmentVar var ) {
		all.add( var );
		if ( var instanceof MovementHypothesis ) {
			moves.add( ( MovementHypothesis ) var );
		}
		if ( var instanceof DivisionHypothesis ) {
			divisions.add( ( DivisionHypothesis ) var );
		}
		if ( var instanceof AppearanceHypothesis ) {
			apps.add( ( AppearanceHypothesis ) var );
		}
		if ( var instanceof DisappearanceHypothesis ) {
			disapps.add( ( DisappearanceHypothesis ) var );
		}
	}
}
