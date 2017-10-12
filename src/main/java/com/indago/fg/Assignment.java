package com.indago.fg;

/**
 * Assignment from some type variable to integer labels
 *
 * @param <V>
 *
 * @author Tobias Pietzsch
 */
public interface Assignment< V > {

	/**
	 * Return {@code true} iff the given {@code variable} is contained in this
	 * {@link Assignment}.
	 *
	 * @param variable
	 *            variable to query.
	 * @return {@code true} iff {@code variable} is contained in this
	 *         {@link Assignment}.
	 */
	boolean isAssigned( V variable );

	/**
	 * Get the label assigned to {@code variable}.
	 *
	 * @param variable
	 *            variable to query.
	 * @return the label assigned to {@code variable}.
	 */
	int getAssignment( V variable );
}
