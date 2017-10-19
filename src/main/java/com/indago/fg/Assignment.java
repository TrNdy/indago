package com.indago.fg;

import java.util.Collection;
import java.util.List;

/**
 * Assignment from some type variable to integer labels
 *
 * @param <V>
 * @author Tobias Pietzsch
 * @author Florian Jug
 */
public interface Assignment< V > {

	/**
	 * Return {@code true} iff the given {@code variable} is contained in this
	 * {@link Assignment}.
	 *
	 * @param variable variable to query.
	 * @return {@code true} iff {@code variable} is contained in this
	 * {@link Assignment}.
	 */
	boolean isAssigned( V variable );

	/**
	 * Return {@code true} iff any of the given {@code variables} is contained
	 * in this {@link Assignment}.
	 *
	 * @param variables
	 *            variables to query.
	 * @return {@code true} iff any of the given {@code variables} is contained
	 *         in this {@link Assignment}.
	 */
	default boolean isAssigned( final Collection< ? extends V > variables ) {
		for ( final V v : variables )
			if ( isAssigned( v ) )
				return true;
		return false;
	}

	/**
	 * Get the label assigned to {@code variable}.
	 *
	 * @param variable
	 *            variable to query.
	 * @return the label assigned to {@code variable}.
	 */
	int getAssignment( V variable );

	/**
	 * Get the first non-zero label assigned to any of the given
	 * {@code variables}.
	 *
	 * @param variables
	 *            variables to query.
	 * @return the label assigned to {@code variables}.
	 */
	default int getAssignment( final List< ? extends V > variables ) {
		if ( variables == null ) return 0;
		for ( final V v : variables )
			if ( getAssignment( v ) != 0 )
				return getAssignment( v );
		return 0;
	}
}
