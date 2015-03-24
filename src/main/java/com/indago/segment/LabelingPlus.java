package com.indago.segment;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.Dimensions;
import net.imglib2.img.Img;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelRegion;
import net.imglib2.roi.labeling.LabelRegions;
import net.imglib2.roi.labeling.LabelingMapping;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Util;

/**
 * @author tpietzsch, jug
 */
public class LabelingPlus
{
	private final Img< IntType > indexImg;

	protected final ImgLabeling< SegmentLabel, IntType > labeling;

	protected final LabelRegions< SegmentLabel > labelRegions;

	protected ArrayList< LabelingFragment > fragments = null;

	protected ArrayList< LabelingSegment > segments = null;

	protected List< LabelingForest > labelingForests;

	private final static IntType intType = new IntType();

	public LabelingPlus( final Dimensions dimensions ) {
		indexImg = Util.getArrayOrCellImgFactory( dimensions, intType ).create( dimensions, intType );
		labeling = new ImgLabeling<>( indexImg );
		labelRegions = new LabelRegions< SegmentLabel >( labeling );
		labelingForests = new ArrayList< LabelingForest >();
	}

	public LabelingPlus( final Img< IntType > indexImg ) {
		this.indexImg = indexImg;
		labeling = new ImgLabeling<>( indexImg );
		labelRegions = new LabelRegions< SegmentLabel >( labeling );
		labelingForests = new ArrayList< LabelingForest >();
	}

	/**
	 * @param labelingPlus
	 */
	protected LabelingPlus( final LabelingPlus labelingPlus ) {
		this.indexImg = labelingPlus.indexImg;
		this.labeling = labelingPlus.labeling;
		this.labelRegions = labelingPlus.labelRegions;
		this.fragments = labelingPlus.fragments;
		this.segments = labelingPlus.segments;
		this.labelingForests = labelingPlus.labelingForests;
	}

	public ImgLabeling< SegmentLabel, IntType > getLabeling() {
		return labeling;
	}

	public List< LabelingForest > getLabelingForests() {
		return labelingForests;
	}

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

	public synchronized ArrayList< LabelingSegment > getSegments() {
		if ( segments == null ) {
			segments = new ArrayList<>();
			for ( final SegmentLabel label : getLabeling().getMapping().getLabels() )
				segments.add( label.getSegment() );
		}
		return segments;
	}

	void createSegmentAndTreeNode( final SegmentLabel label )
	{
		final LabelRegion< SegmentLabel > labelRegion = labelRegions.getLabelRegion( label );
		final LabelingSegment segment = new LabelingSegment( labelRegion );
		label.setSegment( segment );
		final LabelingTreeNode ltn = new LabelingTreeNode( segment, label );
		label.setLabelingTreeNode( ltn );
	}
}
