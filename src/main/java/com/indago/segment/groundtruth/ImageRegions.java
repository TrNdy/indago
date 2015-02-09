package com.indago.segment.groundtruth;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.util.iterationcode.IterationCode;
import net.imglib2.roi.util.iterationcode.IterationCodeBuilder;
import net.imglib2.roi.util.iterationcode.IterationCodeIterator;
import net.imglib2.type.Type;
import net.imglib2.view.Views;

public class ImageRegions< T extends Type< T > > extends AbstractEuclideanSpace {

	private final HashMap< T, IterationCodeBuilder > labelToFragmentProperties;

	public ImageRegions( final RandomAccessibleInterval< T > img ) {
		super( img.numDimensions() );
		labelToFragmentProperties = new HashMap< T, IterationCodeBuilder >();
		final Cursor< T > c = Views.flatIterable( img ).localizingCursor();
		while ( c.hasNext() ) {
			IterationCodeBuilder props = labelToFragmentProperties.get( c.next() );
			if ( props == null ) {
				props = new IterationCodeBuilder( img.numDimensions(), img.min( 0 ) );
				labelToFragmentProperties.put( c.get().copy(), props );
			}
			props.add( c );
		}

		for ( final Entry< T, IterationCodeBuilder > entry : labelToFragmentProperties.entrySet() ) {
			final IterationCodeBuilder props = entry.getValue();
			props.finish();
		}
	}

	public ImageRegion getImageRegion( final T value ) {
		final IterationCodeBuilder props = labelToFragmentProperties.get( value );
		return ( props == null ) ? null : new ImageRegion( props );
	}

	public Set< T > getImageValues() {
		return labelToFragmentProperties.keySet();
	}

	public static class ImageRegion implements Iterable< Localizable > {

		private final IterationCode itcode;

		ImageRegion( final IterationCode itcode ) {
			this.itcode = itcode;
		}

		@Override
		public Iterator< Localizable > iterator() {
			final long[] offset = new long[ itcode.numDimensions() ];
			final Point p = new Point( itcode.numDimensions() );
			final IterationCodeIterator< Point > it = new IterationCodeIterator< Point >( itcode, offset, p );
			return new Iterator< Localizable >() {

				@Override
				public boolean hasNext() {
					return it.hasNext();
				}

				@Override
				public Localizable next() {
					it.fwd();
					return p;
				}

				@Override
				public void remove() {}
			};
		}
	}
}
