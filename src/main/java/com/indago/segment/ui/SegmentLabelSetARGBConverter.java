package com.indago.segment.ui;

import net.imglib2.converter.Converter;
import net.imglib2.roi.labeling.LabelingMapping;
import net.imglib2.roi.labeling.LabelingType;
import net.imglib2.type.numeric.ARGBType;

import com.indago.segment.SegmentLabel;

public class SegmentLabelSetARGBConverter implements Converter< LabelingType< SegmentLabel >, ARGBType > {

	private final LabelingMapping< SegmentLabel > labelingMapping;

	private final SegmentLabelSetColor labelSetColors;

	// fragment index -> ARGB color
	private int[] colors;

	public SegmentLabelSetARGBConverter( final SegmentLabelSetColor labelSetColors, final LabelingMapping< SegmentLabel > labelingMapping ) {
		this.labelSetColors = labelSetColors;
		this.labelingMapping = labelingMapping;
		assignColors();
	}

	private void assignColors() {
		final int numFragments = labelingMapping.numSets();
		if ( colors == null || colors.length < numFragments )
			colors = new int[ numFragments ];
		for ( int i = 0; i < numFragments; ++i )
			colors[ i ] = labelSetColors.getSegmentLabelSetColor( labelingMapping.labelsAtIndex( i ) );
	}

	@Override
	public void convert( final LabelingType< SegmentLabel > input, final ARGBType output ) {
		output.set( colors[ input.getIndex().getInteger() ] );
	}
}