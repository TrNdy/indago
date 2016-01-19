package com.indago.data.segmentation.ui;

import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.view.composite.NumericComposite;

public class ARGBCompositeAlphaBlender implements Converter< NumericComposite< ARGBType >, ARGBType > {

	@Override
	public void convert( final NumericComposite< ARGBType > input, final ARGBType output ) {
		double r = 0;
		double g = 0;
		double b = 0;
		for ( final ARGBType s : input ) {
			final int v = s.get();
			final double sa = ( 1.0 / 255 ) * ARGBType.alpha( v );
			final double da = 1.0 - sa;
			r = da * r + sa * ARGBType.red( v );
			g = da * g + sa * ARGBType.green( v );
			b = da * b + sa * ARGBType.blue( v );
		}
		final int ir = Math.min( 255, ( int ) ( r + 0.5 ) );
		final int ig = Math.min( 255, ( int ) ( g + 0.5 ) );
		final int ib = Math.min( 255, ( int ) ( b + 0.5 ) );
		output.set( ARGBType.rgba( ir, ig, ib, 255 ) );
	}
}