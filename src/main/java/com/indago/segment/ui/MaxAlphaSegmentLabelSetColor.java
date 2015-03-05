package com.indago.segment.ui;

import java.util.Set;

import net.imglib2.type.numeric.ARGBType;

import com.indago.segment.SegmentLabel;

public class MaxAlphaSegmentLabelSetColor implements SegmentLabelSetColor {

	private final SegmentLabelColor labelColors;

	public MaxAlphaSegmentLabelSetColor( final SegmentLabelColor labelColors ) {
		this.labelColors = labelColors;
	}

	@Override
	public int getSegmentLabelSetColor( final Set< SegmentLabel > labels ) {
		int bestColor = 0;
		for ( final SegmentLabel label : labels ) {
			final int color = labelColors.getSegmentLabelColor( label );
			if ( ARGBType.alpha( color ) > ARGBType.alpha( bestColor ) )
				bestColor = color;
		}
		return bestColor;
	}
}