package com.indago.fg;

import java.util.Map;

import com.indago.models.IndicatorVar;

public class MappedFactorGraph {

	private final UnaryCostConstraintGraph fg;
	private final Map< IndicatorVar, Variable > modelMap;
	private final AssignmentMapper< Variable, IndicatorVar > assignmentMap;

	public MappedFactorGraph(
			final UnaryCostConstraintGraph fg,
			final Map< IndicatorVar, Variable > varmap,
			final AssignmentMapper< Variable, IndicatorVar > mapper ) {
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
	public Map< IndicatorVar, Variable > getVarmap() {
		return modelMap;
	}

	/**
	 * @return the mapper
	 */
	public AssignmentMapper< Variable, IndicatorVar > getAssmntMapper() {
		return assignmentMap;
	}
}
