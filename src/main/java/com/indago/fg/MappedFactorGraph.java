package com.indago.fg;

import com.indago.pg.IndicatorNode;
import com.indago.util.Bimap;

public class MappedFactorGraph {

	private final UnaryCostConstraintGraph fg;
	private final Bimap< IndicatorNode, Variable > varMap;
	private final AssignmentMapper< Variable, IndicatorNode > assignmentMap;

	public MappedFactorGraph(
			final UnaryCostConstraintGraph fg,
			final Bimap< IndicatorNode, Variable > varmap,
			final AssignmentMapper< Variable, IndicatorNode > mapper ) {
		this.fg = fg;
		this.varMap = varmap;
		this.assignmentMap = mapper;
	}

	/**
	 * @return the fg
	 */
	public UnaryCostConstraintGraph getFg() {
		return fg;
	}

	/**
	 * @return the varmap
	 */
	public Bimap< IndicatorNode, Variable > getVarmap() {
		return varMap;
	}

	/**
	 * @return the mapper
	 */
	public AssignmentMapper< Variable, IndicatorNode > getAssmntMapper() {
		return assignmentMap;
	}
}
