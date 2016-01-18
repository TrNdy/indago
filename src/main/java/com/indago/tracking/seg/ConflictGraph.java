package com.indago.tracking.seg;

import java.util.Collection;

/**
 * Provides correct and complete set of conflict graph cliques (sets of mutually
 * conflicting segment hypothesis).
 *
 * @param <T>
 *            vertex type (segment hypotheses).
 */
public interface ConflictGraph {

	public Collection< ConflictSet > getConflictCliques();
}
