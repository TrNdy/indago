package com.indago.segment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import net.imglib2.Dimensions;
import net.imglib2.Localizable;
import net.imglib2.newlabeling.ImgLabeling;
import net.imglib2.tree.Forest;
import net.imglib2.tree.TreeNode;
import net.imglib2.tree.Util;
import net.imglib2.type.numeric.integer.IntType;

public class LabelingForest implements Forest< LabelingTreeNode >, ConflictGraph< LabelingSegment< Integer > > {

	public static < T extends TreeNode< T > & Iterable< ? extends Localizable > > LabelingForest fromForest( final Forest< T > forest, final ImgLabeling< Integer, IntType > sharedLabeling ) {
		return new LabelingForestBuilder< T >( forest, sharedLabeling, null ).getLabelingForest();
	}

	public static < T extends TreeNode< T > & Iterable< ? extends Localizable > > LabelingForest fromForest( final Forest< T > forest, final Dimensions dimensions ) {
		return new LabelingForestBuilder< T >( forest, null, dimensions ).getLabelingForest();
	}

	private final ImgLabeling< Integer, IntType > labeling;

	private final HashSet< LabelingTreeNode > roots;

	public LabelingForest( final ImgLabeling< Integer, IntType > labeling, final HashSet< LabelingTreeNode > roots ) {
		this.labeling = labeling;
		this.roots = roots;
	}

	public ImgLabeling< Integer, IntType > getLabeling() {
		return labeling;
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
	public Collection< ? extends Collection< LabelingSegment< Integer > > > getConflictGraphCliques() {
		final ArrayList< ArrayList< LabelingSegment< Integer > > > cliques = new ArrayList<>();
		final ArrayList< LabelingTreeNode > leafs = Util.getLeafs( this );
		for ( final LabelingTreeNode leaf : leafs ) {
			final ArrayList< LabelingSegment< Integer > > clique = new ArrayList<>();
			clique.add( leaf.getSegment() );
			for ( final LabelingTreeNode node : leaf.getConflictingHypotheses() ) {
				clique.add( node.getSegment() );
			}
			if ( clique.size() > 1 ) cliques.add( clique );
		}
		return cliques;
	}
}
