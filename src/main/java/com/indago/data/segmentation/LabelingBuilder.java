package com.indago.data.segmentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import net.imglib2.Dimensions;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.algorithm.tree.Forest;
import net.imglib2.algorithm.tree.TreeNode;
import net.imglib2.img.Img;
import net.imglib2.roi.labeling.LabelingType;
import net.imglib2.type.numeric.integer.IntType;

/**
 * @author tpietzsch, jug
 */
public class LabelingBuilder extends LabelingPlus {

	public LabelingBuilder( final Dimensions dimensions ) {
		super( dimensions );
	}

	public LabelingBuilder( final Img< IntType > indexImg ) {
		super( indexImg );
	}

	public LabelingBuilder( final LabelingPlus labelingPlus ) {
		super( labelingPlus );
	}

	public synchronized < T extends TreeNode< T > & Iterable< ? extends Localizable > > LabelingForest buildLabelingForest(
			final Forest< T > forest ) {
		String segmentationSource = "";
		return buildingLabelingForestWithSegmentationSource( forest, segmentationSource );
	}

	private < T extends TreeNode< T > & Iterable< ? extends Localizable > > LabelingForest buildingLabelingForestWithSegmentationSource(
			final Forest< T > forest,
			String segmentationSource ) {

		return buildingLabelingForestWithSegmentationSource( forest, segmentationSource );
	}

	public synchronized < T extends TreeNode< T > & Iterable< ? extends Localizable > > LabelingForest buildLabelingForest(
			final Forest< T > forest,
			String segmentationSource ) {
		// invalidate fragments and segments because we will add new labels
		fragments = null;
		segments = null;
		// TODO: use modCount invalidation instead

		// maps nodes in the original forest to labels
		final HashMap< T, LabelData > nodeToLabel = new HashMap<>();

		// create new labels for the nodes in the original forest and label node pixels accordingly.
		// remember which node corresponds to which label in the nodeToLabel map.
		ArrayList< T > currentLevel = new ArrayList< T >( forest.roots() );
		final RandomAccess< LabelingType< LabelData > > a = labeling.randomAccess();
		while ( !currentLevel.isEmpty() ) {
			final ArrayList< T > nextLevel = new ArrayList< T >();
			for ( final T node : currentLevel ) {
				final LabelData label = new LabelData();
				for ( final Localizable pos : node ) {
					a.setPosition( pos );
					final LabelingType< LabelData > t = a.get();
					t.add( label );
				}
				nodeToLabel.put( node, label );
				nextLevel.addAll( node.getChildren() );
			}
			currentLevel = nextLevel;
		}

		// build a LabelingForest using the structure of the original forest
		final HashSet< LabelingTreeNode > roots = new HashSet<>();
		for ( final T node : forest.roots() )
			roots.add( buildLabelingTreeNodeFor( node, nodeToLabel, segmentationSource ) );

		// add new forest to list of forests ever added
		final LabelingForest labelingForest = new LabelingForest( roots );
		labelingForests.add( labelingForest );

		return labelingForest;
	}

	private < T extends TreeNode< T > > LabelingTreeNode buildLabelingTreeNodeFor(
			final T node,
			final HashMap< T, LabelData > nodeToLabel,
			final String segmentationSource ) {
		final LabelData label = nodeToLabel.get( node );
		createSegmentAndTreeNode( label, segmentationSource );
		final LabelingTreeNode ltn = label.getLabelingTreeNode();
		for ( final T c : node.getChildren() )
			ltn.addChild( buildLabelingTreeNodeFor( c, nodeToLabel, segmentationSource ) );
		return ltn;
	}
}
