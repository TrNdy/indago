package com.indago.segment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import net.imglib2.Dimensions;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.newlabeling.ImgLabeling;
import net.imglib2.newlabeling.LabelRegion;
import net.imglib2.newlabeling.Labeling;
import net.imglib2.newlabeling.LabelingType;
import net.imglib2.tree.Forest;
import net.imglib2.tree.TreeNode;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Util;

public class LabelingForestBuilder< T extends TreeNode< T > & Iterable< ? extends Localizable > > {

	private final LabelingForest labelingForest;

	private final ImgLabeling< Integer, IntType > labeling;

	private final HashMap< T, Integer > nodeToLabel;

	public LabelingForestBuilder( final Forest< T > forest, final ImgLabeling< Integer, IntType > sharedLabeling, final Dimensions dimensions ) {
		if ( sharedLabeling == null ) {
			final Img< IntType > img = Util.getArrayOrCellImgFactory( dimensions, new IntType() ).create( dimensions, new IntType() );
			labeling = new ImgLabeling< Integer, IntType >( img );
		} else {
			labeling = sharedLabeling;
		}
		nodeToLabel = labelNodes( forest, labeling );

		final HashSet< LabelingTreeNode > roots = new HashSet< LabelingTreeNode >();
		for ( final T node : forest.roots() )
			roots.add( buildLabelingTreeNodeFor( node ) );
		labelingForest = new LabelingForest( labeling, roots );
	}

	public LabelingForest getLabelingForest() {
		return labelingForest;
	}

	public HashMap< T, Integer > getNodeToLabel() {
		return nodeToLabel;
	}

	private LabelingTreeNode buildLabelingTreeNodeFor( final T node ) {
		final Integer label = nodeToLabel.get( node );
		final LabelRegion< Integer > labelRegion = labeling.getLabelRegions().getLabelRegion( label );
		final LabelingSegment< Integer > segment = new LabelingSegment< Integer >( labelRegion );
		final LabelingTreeNode ltn = new LabelingTreeNode( segment );
		for ( final T c : node.getChildren() )
			ltn.addChild( buildLabelingTreeNodeFor( c ) );
		return ltn;
	}

	/**
	 * Label all pixels belonging to nodes in the forest. Labels start from 0
	 * and the forest is traversed level-by-level starting from the roots.
	 *
	 * @param forest
	 *            forest whose nodes are {@link Iterable}s of pixel positions.
	 * @param labeling
	 *            label image.
	 * @return map from tree nodes to labels.
	 */
	public static < T extends TreeNode< T > & Iterable< ? extends Localizable > > HashMap< T, Integer > labelNodes( final Forest< T > forest, final Labeling< Integer > labeling ) {
		int label = 0;
		final HashMap< T, Integer > nodeToLabel = new HashMap< T, Integer >();
		ArrayList< T > currentLevel = new ArrayList< T >( forest.roots() );
		final RandomAccess< LabelingType< Integer > > a = labeling.randomAccess();
		while ( !currentLevel.isEmpty() ) {
			final ArrayList< T > nextLevel = new ArrayList< T >();
			for ( final T node : currentLevel ) {
				for ( final Localizable pos : node ) {
					a.setPosition( pos );
					final LabelingType< Integer > t = a.get();
					t.add( label );
				}
				nodeToLabel.put( node, label );
				nextLevel.addAll( node.getChildren() );
				++label;
			}
			currentLevel = nextLevel;
		}
		return nodeToLabel;
	}
}
