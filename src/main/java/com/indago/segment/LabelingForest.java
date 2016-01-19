package com.indago.segment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.indago.tracking.seg.ConflictGraph;
import com.indago.tracking.seg.ConflictSet;
import com.indago.tracking.seg.SegmentVar;

import net.imglib2.algorithm.tree.Forest;
import net.imglib2.algorithm.tree.TreeUtils;

public class LabelingForest implements Forest< LabelingTreeNode >, ConflictGraph {

	private final HashSet< LabelingTreeNode > roots;

	public LabelingForest( final HashSet< LabelingTreeNode > roots ) {
		this.roots = roots;
	}

	@Override
	public HashSet< LabelingTreeNode > roots() {
		return roots;
	}

	/**
	 * This method recomputes the conflict cliques. This is potentially
	 * expensive -- call it once and cache the result!
	 * TODO JF asks: why should this ever be expensive? In a forest that should
	 * be very efficient!
	 */
	@Override
	public Collection< ConflictSet > getConflictCliques() {
		final ArrayList< ConflictSet > cliques = new ArrayList< >();
		final ArrayList< LabelingTreeNode > leafs = TreeUtils.getLeafs( this );
		for ( final LabelingTreeNode leaf : leafs ) {
			final ConflictSet clique = new ConflictSet();
			clique.add( new SegmentVar( leaf.getSegment() ) );
			for ( final LabelingTreeNode node : leaf.getConflictingHypotheses() ) {
				clique.add( new SegmentVar( leaf.getSegment() ) );
			}
			if ( clique.size() > 1 ) cliques.add( clique );
		}
		return cliques;
	}
}
