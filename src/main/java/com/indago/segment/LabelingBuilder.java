package com.indago.segment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import net.imglib2.Dimensions;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelRegion;
import net.imglib2.roi.labeling.LabelRegions;
import net.imglib2.roi.labeling.LabelingMapping;
import net.imglib2.roi.labeling.LabelingType;
import net.imglib2.algorithm.tree.Forest;
import net.imglib2.algorithm.tree.TreeNode;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Util;

public class LabelingBuilder {

	private final Img< IntType > indexImg;

	private final ImgLabeling< SegmentLabel, IntType > labeling;

	private final LabelRegions< SegmentLabel > labelRegions;

	private final static IntType intType = new IntType();

	public LabelingBuilder( final Dimensions dimensions ) {
		indexImg = Util.getArrayOrCellImgFactory( dimensions, intType ).create( dimensions, intType );
		labeling = new ImgLabeling<>( indexImg );
		labelRegions = new LabelRegions< SegmentLabel >( labeling );
	}

	public LabelingBuilder( final Img< IntType > indexImg ) {
		this.indexImg = indexImg;
		labeling = new ImgLabeling<>( indexImg );
		labelRegions = new LabelRegions< SegmentLabel >( labeling );
	}

	public ImgLabeling< SegmentLabel, IntType > getLabeling() {
		return labeling;
	}

	private ArrayList< LabelingFragment > fragments = null;

	public synchronized ArrayList< LabelingFragment > getFragments() {
		if ( fragments == null ) {
			fragments = new ArrayList<>();
			final LabelingMapping< SegmentLabel > mapping = labeling.getMapping();
			for ( final SegmentLabel label : mapping.getLabels() )
				label.getFragmentIndices().clear();
			final int numLabelSets = mapping.numSets();
			final boolean[] flags = new boolean[ numLabelSets ];
			for ( final IntType t : indexImg )
				flags[ t.get() ] = true;
			for ( int i = 0; i < numLabelSets; ++i ) {
				if ( flags[ i ] ) {
					final int fragmentIndex = fragments.size();
					final LabelingFragment fragment = new LabelingFragment( fragmentIndex );
					fragments.add( fragment );
					for ( final SegmentLabel label : mapping.labelsAtIndex( i ) ) {
						fragment.getSegments().add( label );
						label.getFragmentIndices().add( fragmentIndex );
					}
				}
			}
		}
		return fragments;
	}

	private ArrayList< LabelingSegment > segments = null;

	public synchronized ArrayList< LabelingSegment > getSegments() {
		if ( segments == null ) {
			segments = new ArrayList<>();
			for ( final SegmentLabel label : getLabeling().getMapping().getLabels() )
				segments.add( label.getSegment() );
		}
		return segments;
	}

	public synchronized < T extends TreeNode< T > & Iterable< ? extends Localizable > > LabelingForest buildLabelingForest( final Forest< T > forest ) {
		// invalidate fragments and segments because we will add new labels
		fragments = null;
		segments = null;
		// TODO: use modCount invalidation instead

		// maps nodes in the original forest to labels
		final HashMap< T, SegmentLabel > nodeToLabel = new HashMap<>();

		// create new labels for the nodes in the original forest and label node pixels accordingly.
		// remember which node corresponds to which label in the nodeToLabel map.
		ArrayList< T > currentLevel = new ArrayList< T >( forest.roots() );
		final RandomAccess< LabelingType< SegmentLabel > > a = labeling.randomAccess();
		while ( !currentLevel.isEmpty() ) {
			final ArrayList< T > nextLevel = new ArrayList< T >();
			for ( final T node : currentLevel ) {
				final SegmentLabel label = new SegmentLabel();
				for ( final Localizable pos : node ) {
					a.setPosition( pos );
					final LabelingType< SegmentLabel > t = a.get();
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
			roots.add( buildLabelingTreeNodeFor( node, nodeToLabel ) );
		return new LabelingForest( roots );
	}

	private < T extends TreeNode< T > & Iterable< ? extends Localizable > > LabelingTreeNode buildLabelingTreeNodeFor( final T node, final HashMap< T, SegmentLabel > nodeToLabel ) {
		final SegmentLabel label = nodeToLabel.get( node );
		final LabelRegion< SegmentLabel > labelRegion = labelRegions.getLabelRegion( label );
		final LabelingSegment segment = new LabelingSegment( labelRegion );
		label.setSegment( segment );
		final LabelingTreeNode ltn = new LabelingTreeNode( segment, label );
		label.setLabelingTreeNode( ltn );
		for ( final T c : node.getChildren() )
			ltn.addChild( buildLabelingTreeNodeFor( c, nodeToLabel ) );
		return ltn;
	}
}
