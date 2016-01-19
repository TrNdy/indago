package com.indago.brandnewfg;

public interface Assignment {

	/**
	 * Return {@code true} iff the given {@link Variable} is contained in this
	 * {@link Assignment}.
	 *
	 * @param var
	 *            {@link Variable} to query.
	 * @return {@code true} iff {@code var} is contained in this
	 *         {@link Assignment}.
	 */
	boolean isAssigned( Variable var );

	/**
	 * Get the label assigned to {@code var}.
	 *
	 * @param var
	 *            {@link Variable} to query.
	 * @return the label assigned to {@code var}.
	 */
	int getAssignment( Variable var );
}
