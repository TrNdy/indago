package com.indago.segment.ui;

import java.util.Set;

import net.imglib2.type.numeric.ARGBType;

import com.indago.segment.LabelData;

public class AlphaMixedSegmentLabelSetColor implements SegmentLabelSetColor {

		private final SegmentLabelColor labelColors;

		public AlphaMixedSegmentLabelSetColor( final SegmentLabelColor labelColors ) {
			this.labelColors = labelColors;
		}

		@Override
		public int getSegmentLabelSetColor( final Set< LabelData > labels ) {
			if ( labels.isEmpty() )
				return 0;

			double sr = 0;
			double sg = 0;
			double sb = 0;
			double sa = 0;
			double maxAlpha = 0;
			for ( final LabelData label : labels ) {
				final int color = labelColors.getSegmentLabelColor( label );
				final double a = ARGBType.alpha( color );
				if ( a > maxAlpha )
					maxAlpha = a;
				sa += a;
				sr += a * ARGBType.red( color );
				sg += a * ARGBType.green( color );
				sb += a * ARGBType.blue( color );
			}

			final int r = Math.max( Math.min( ( int ) ( sr / sa ), 255 ), 0 );
			final int g = Math.max( Math.min( ( int ) ( sg / sa ), 255 ), 0 );
			final int b = Math.max( Math.min( ( int ) ( sb / sa ), 255 ), 0 );
//			final int a = Math.max( Math.min( ( int ) ( sa / labels.size() ), 255 ), 0 );
			final int a = ( int ) maxAlpha;
			return ARGBType.rgba( r, g, b, a );
		}
	}