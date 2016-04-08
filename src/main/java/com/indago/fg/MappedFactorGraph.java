package com.indago.fg;

import java.util.Map;

import com.indago.models.IndicatorNode;

public class MappedFactorGraph {

	private final UnaryCostConstraintGraph fg;
	private final Map< IndicatorNode, Variable > modelMap;
	private final AssignmentMapper< Variable, IndicatorNode > assignmentMap;

	public MappedFactorGraph(
			final UnaryCostConstraintGraph fg,
			final Map< IndicatorNode, Variable > varmap,
			final AssignmentMapper< Variable, IndicatorNode > mapper ) {
		this.fg = fg;
		this.modelMap = varmap;
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
	public Map< IndicatorNode, Variable > getVarmap() {
		return modelMap;
	}

	/**
	 * @return the mapper
	 */
	public AssignmentMapper< Variable, IndicatorNode > getAssmntMapper() {
		return assignmentMap;
	}
}
