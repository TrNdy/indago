package com.indago.segment.visualization;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.display.projector.RandomAccessibleProjector2D;
import net.imglib2.labeling.LabelingMapping;
import net.imglib2.labeling.LabelingType;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.type.Type;

public class VisualizeLabeling
{

	public static < C extends Type< C >, L extends Comparable< L > > void colorLabels( final NativeImgLabeling< L, ? > labeling, final Iterator< C > colors, final RandomAccessibleInterval< C > regionsImg )
	{
		final HashMap< List< ? >, C > colorTable = new HashMap< List< ? >, C >();
		final LabelingMapping< ? > mapping = labeling.getMapping();
		final int numLists = mapping.numLists();
		for ( int i = 0; i < numLists; ++i )
		{
			final List< ? > list = mapping.listAtIndex( i );
			colorTable.put( list, colors.next() );
		}
//		colorTable.put( mapping.emptyList(), colors.next() );

		new RandomAccessibleProjector2D< LabelingType< L >, C >( 0, 1, labeling, regionsImg,
				new LabelingTypeConverter< L, C >( colorTable )
				).map();
	}

	public static class LabelingTypeConverter< L extends Comparable< L >, T extends Type< T > > implements Converter< LabelingType< L >, T >
	{
		private final HashMap< List< ? >, T > colorTable;

		public LabelingTypeConverter( final HashMap< List< ? >, T > colorTable )
		{
			this.colorTable = colorTable;
		}

		@Override
		public void convert( final LabelingType< L > input, final T output )
		{
			final T t = colorTable.get( input.getLabeling() );
			if ( t != null )
				output.set( t );
		}
	}
}
