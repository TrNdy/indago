package com.indago.segment;

import java.util.Collection;

/**
 * @param <T> vertex type
 */
public interface ConflictGraph< T >
{
	public Collection< ? extends Collection < T > > getConflictGraphCliques();
}
