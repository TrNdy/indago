package com.indago.pg.assignments;

import java.util.ArrayList;
import java.util.Collection;

public class AssignmentNodes {

	private final Collection< AssignmentNode > all = new ArrayList<>();
	private final Collection< MovementHypothesis > moves = new ArrayList<>();
	private final Collection< DivisionHypothesis > divisions = new ArrayList<>();
	private final Collection< AppearanceHypothesis > apps = new ArrayList<>();
	private final Collection< DisappearanceHypothesis > disapps = new ArrayList<>();

	public Collection< AssignmentNode > getAllAssignments() {
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

	public void add( final AssignmentNode var ) {
		if ( var == null ) {
			throw new IllegalArgumentException( "'null' cannot be added to AssignmentVars." );
		}
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
