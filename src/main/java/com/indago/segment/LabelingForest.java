package com.indago.segment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import net.imglib2.newlabeling.ImgLabeling;
import net.imglib2.newlabeling.LabelRegions;
import net.imglib2.tree.Forest;
import net.imglib2.tree.Util;
import net.imglib2.type.numeric.integer.IntType;

public class LabelingForest< T > implements Forest< LabelingTreeNode< T > >, ConflictGraph< LabelingSegment > {

	private final HashSet< LabelingTreeNode< T > > roots;

	private final LabelingBuilder builder;

	public LabelingForest( final ImgLabeling< Integer, IntType > labeling, final LabelRegions< Integer > labelRegions, final HashSet< LabelingTreeNode< T > > roots ) {
		this.builder = null;
		this.roots = roots;
	}

	public LabelingForest( final LabelingBuilder builder, final HashSet< LabelingTreeNode< T > > roots ) {
		this.builder = builder;
		this.roots = roots;
	}

	@Override
	public HashSet< LabelingTreeNode< T > > roots() {
		return roots;
	}

	/**
	 * This method recomputes the conflict cliques. This is potentially
	 * expensive -- call it once and cache the result!
	 * TODO JF asks: why should this ever be expensive? In a forest that should
	 * be very efficient!
	 */
	@Override
	public Collection< ? extends Collection< LabelingSegment > > getConflictGraphCliques() {
		final ArrayList< ArrayList< LabelingSegment > > cliques = new ArrayList<>();
		final ArrayList< LabelingTreeNode< T > > leafs = Util.getLeafs( this );
		for ( final LabelingTreeNode< T > leaf : leafs ) {
			final ArrayList< LabelingSegment > clique = new ArrayList<>();
			clique.add( leaf.getSegment() );
			for ( final LabelingTreeNode< T > node : leaf.getConflictingHypotheses() ) {
				clique.add( node.getSegment() );
			}
			if ( clique.size() > 1 ) cliques.add( clique );
		}
		return cliques;
	}
}
