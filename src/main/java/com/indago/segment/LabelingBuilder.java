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
import net.imglib2.newlabeling.LabelRegions;
import net.imglib2.newlabeling.LabelingType;
import net.imglib2.tree.Forest;
import net.imglib2.tree.TreeNode;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Util;

public class LabelingBuilder {

	/**
	 * A label type representing a segment.
	 *
	 * @param <T>
	 *
	 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
	 */
	// TODO visibility, javadoc
	public static class SegmentLabel< T extends TreeNode< T > > {

		private final T segment;

		private final ArrayList< Integer > fragmentIndices;

		SegmentLabel( final T segment ) {
			this.segment = segment;
			this.fragmentIndices = new ArrayList<>();
		}

		public T getSegment() {
			return segment;
		}

		public ArrayList< Integer > getFragmentIndices() {
			return fragmentIndices;
		}
	}

	private final Img< IntType > indexImg;

	private final ImgLabeling< SegmentLabel< ? >, IntType > labeling;

	private final LabelRegions< SegmentLabel< ? > > labelRegions;

	private final static IntType intType = new IntType();

	public LabelingBuilder( final Dimensions dimensions ) {
		indexImg = Util.getArrayOrCellImgFactory( dimensions, intType ).create( dimensions, intType );
		labeling = new ImgLabeling<>( indexImg );
		labelRegions = new LabelRegions< SegmentLabel< ? > >( labeling );
	}

	public ImgLabeling< SegmentLabel< ? >, IntType > getLabeling() {
		return labeling;
	}

	public < T extends TreeNode< T > & Iterable< ? extends Localizable > > LabelingForest< SegmentLabel< T > > buildLabelingForest( final Forest< T > forest ) {
		// maps nodes in the original forest to labels
		final HashMap< T, SegmentLabel< T > > nodeToLabel = new HashMap<>();

		// create new labels for the nodes in the original forest and label node pixels accordingly.
		// remember which node corresponds to which label in the nodeToLabel map.
		ArrayList< T > currentLevel = new ArrayList< T >( forest.roots() );
		final RandomAccess< LabelingType< SegmentLabel< ? > > > a = labeling.randomAccess();
		while ( !currentLevel.isEmpty() ) {
			final ArrayList< T > nextLevel = new ArrayList< T >();
			for ( final T node : currentLevel ) {
				final SegmentLabel< T > label = new SegmentLabel<>( node );
				for ( final Localizable pos : node ) {
					a.setPosition( pos );
					final LabelingType< SegmentLabel< ? > > t = a.get();
					t.add( label );
				}
				nodeToLabel.put( node, label );
				nextLevel.addAll( node.getChildren() );
			}
			currentLevel = nextLevel;
		}

		// build a LabelingForest using the structure of the original forest
		final HashSet< LabelingTreeNode< SegmentLabel< T > > > roots = new HashSet<>();
		for ( final T node : forest.roots() )
			roots.add( buildLabelingTreeNodeFor( node, nodeToLabel ) );
		return new LabelingForest< SegmentLabel< T > >( this, roots );
	}

	private < T extends TreeNode< T > & Iterable< ? extends Localizable > > LabelingTreeNode< SegmentLabel< T > > buildLabelingTreeNodeFor( final T node, final HashMap< T, SegmentLabel< T > > nodeToLabel ) {
		final SegmentLabel< T > label = nodeToLabel.get( node );
		final LabelRegion< SegmentLabel< ? > > labelRegion = labelRegions.getLabelRegion( label );
		final LabelingSegment segment = new LabelingSegment( labelRegion );
		final LabelingTreeNode< SegmentLabel< T > > ltn = new LabelingTreeNode<>( segment, label );
		for ( final T c : node.getChildren() )
			ltn.addChild( buildLabelingTreeNodeFor( c, nodeToLabel ) );
		return ltn;
	}
}
