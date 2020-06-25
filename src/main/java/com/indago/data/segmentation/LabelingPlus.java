package com.indago.data.segmentation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
import net.imglib2.Cursor;
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

	protected final ImgLabeling< LabelData, IntType > labeling;

	protected final LabelRegions< LabelData > labelRegions;

	protected ArrayList< LabelingFragment > fragments = null;

	protected ArrayList< LabelingSegment > segments = null;

	protected List< LabelingForest > labelingForests;

	private final static IntType intType = new IntType();

	public LabelingPlus( final Dimensions dimensions ) {
		indexImg = Util.getArrayOrCellImgFactory( dimensions, intType ).create( dimensions, intType );
		labeling = new ImgLabeling<>( indexImg );
		labelRegions = new LabelRegions<>( labeling );
		labelingForests = new ArrayList<>();
	}

	public LabelingPlus( final Img< IntType > indexImg ) {
		this.indexImg = indexImg;
		labeling = new ImgLabeling<>( indexImg );
		labelRegions = new LabelRegions<>( labeling );
		labelingForests = new ArrayList<>();
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

	public ImgLabeling< LabelData, IntType > getLabeling() {
		return labeling;
	}

	public List< LabelingForest > getLabelingForests() {
		return labelingForests;
	}

	public synchronized ArrayList< LabelingFragment > getFragments() {
		if ( fragments == null ) {
			fragments = new ArrayList<>();
			final LabelingMapping< LabelData > mapping = labeling.getMapping();
			final Set< LabelData > labels = mapping.getLabels();
			for ( final LabelData label : labels )
				label.getFragmentIndices().clear();
			final int numLabelSets = mapping.numSets();
			final boolean[] flags = new boolean[ numLabelSets ];
			for ( final IntType t : indexImg )
				flags[ t.get() ] = true;
			for ( int i = 0; i < numLabelSets; ++i ) {
				if ( flags[ i ] ) {
					final int fragmentIndex = fragments.size();
					final LabelingFragment fragment = new LabelingFragment( fragmentIndex, i );
					fragments.add( fragment );
					for ( final LabelData label : mapping.labelsAtIndex( i ) ) {
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
			for ( final LabelData label : getLabeling().getMapping().getLabels() )
				segments.add( label.getSegment() );
		}
		return segments;
	}

	void createSegmentAndTreeNode( final LabelData label, final String source )
	{
		final LabelRegion< LabelData > labelRegion = labelRegions.getLabelRegion( label );
		final LabelingSegment segment = new LabelingSegment( labelRegion );
		label.setSegment( segment );
		label.setSegmentSource( source );
		final LabelingTreeNode ltn = new LabelingTreeNode( segment, label );
		label.setLabelingTreeNode( ltn );
	}

	/**
	 * "Compress" labeling by removing (from mapping and index image) label sets that don't occur in the index image.
	 * <p>
	 * TODO:
	 *  - move to imglib2-roi ImgLabeling
	 *  - parallelize indexImg remapping using Parallelization/LoopBuilder
	 *  - increment modcount
	 */
	public synchronized void pack()
	{
		final LabelingMapping< LabelData > mapping = labeling.getMapping();

		// indexMap[ oldIndex ] == newIndex
		final int[] indexMap = new int[ mapping.numSets() ];

		int nextIndex = 1;
		final Cursor< IntType > c = indexImg.cursor();
		while ( c.hasNext() )
		{
			final IntType type = c.next();
			final int oldIndex = type.get();
			if ( oldIndex != 0 )
			{
				int newIndex = indexMap[ oldIndex ];
				if ( newIndex == 0 )
				{
					newIndex = nextIndex++;
					indexMap[ oldIndex ] = newIndex;
				}
				type.set( newIndex );
			}
		}
		final int numLabelSets = nextIndex;

		final List< Set< LabelData > > labelSets = new ArrayList<>( numLabelSets );
		labelSets.add( new HashSet<>() );
		for ( int i = 1; i < indexMap.length; i++ )
			if ( indexMap[ i ] != 0 )
				labelSets.add( new HashSet<>( mapping.labelsAtIndex( i ) ) );
		mapping.setLabelSets( labelSets );
	}
}
