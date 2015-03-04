package com.indago.ui;

import java.util.ArrayList;
import java.util.List;

import ij.ImageJ;
import io.scif.img.IO;
import net.imglib2.AbstractInterval;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.converter.TypeIdentity;
import net.imglib2.display.RealARGBColorConverter;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.roi.labeling.LabelingType;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.ui.viewer.InteractiveViewer2D;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import net.imglib2.view.composite.NumericComposite;

import com.indago.segment.LabelingBuilder;
import com.indago.segment.LabelingForest;
import com.indago.segment.SegmentLabel;
import com.indago.segment.filteredcomponents.FilteredComponentTree;
import com.indago.segment.filteredcomponents.FilteredComponentTree.Filter;
import com.indago.segment.filteredcomponents.FilteredComponentTree.MaxGrowthPerStep;

public class PlayGround5 {
	public static void main( final String[] args ) throws Exception {
		ImageJ.main( args );
		final String image = "src/main/resources/image.tif";
		final String segments = "src/main/resources/forest1.tif";
		doIt( image, segments, new UnsignedIntType() );
	}

	public static < T extends RealType< T > & NativeType< T > > void doIt( final String imageFn, final String segmentsFn, final T type ) throws Exception {
		final int minComponentSize = 10;
		final int maxComponentSize = 10000 - 1;
		final Filter maxGrowthPerStep = new MaxGrowthPerStep( 1 );
		final boolean darkToBright = false;

		final ArrayImgFactory< T > factory = new ArrayImgFactory< T >();
		final Img< T > image = IO.openImgs( imageFn, factory, type ).get( 0 );
		final Img< T > segments = IO.openImgs( segmentsFn, factory, type ).get( 0 );

		final LabelingBuilder labelingBuilder = new LabelingBuilder( segments );
		final LabelingForest labelingForest = labelingBuilder.buildLabelingForest(
				FilteredComponentTree.buildComponentTree(
						segments,
						type,
						minComponentSize,
						maxComponentSize,
						maxGrowthPerStep,
						darkToBright ) );
		final RandomAccessibleInterval< LabelingType< SegmentLabel > > labeling = labelingBuilder.getLabeling();

		final RealARGBColorConverter< T > imageConverter = new RealARGBColorConverter.Imp0< T >( 0, 255 );
		imageConverter.setColor( new ARGBType( 0xff8888ff ) );
		final SegmentARGBConverter labelingConverter = new SegmentARGBConverter();

		final ArrayList< RandomAccessibleInterval< ARGBType > > hyperslices = new ArrayList< RandomAccessibleInterval< ARGBType > >();
		hyperslices.add( Converters.convert( ( RandomAccessibleInterval< T > ) image, imageConverter, new ARGBType() ) );
		hyperslices.add( Converters.convert( labeling, labelingConverter, new ARGBType() ) );

		final RandomAccessibleInterval< ARGBType > stack = new RandomAccessibleIntervalStack< ARGBType >( hyperslices );
		final RandomAccessibleInterval< NumericComposite< ARGBType > > composite = Views.collapseNumeric( stack );

		final RandomAccessibleInterval< ARGBType > blended = Converters.convert( composite, new ARGBCompositeAlphaBlender(), new ARGBType() );
		ImageJFunctions.show( blended );

//		new InteractiveViewer2D< NumericComposite< ARGBType > >( 800, 600, Views.extendZero( composite ), new ARGBCompositeAlphaBlender() );
		new InteractiveViewer2D< ARGBType >( 800, 600, Views.extendZero( blended ), new TypeIdentity< ARGBType >() );
	}


	public static class SegmentARGBConverter implements Converter< LabelingType< SegmentLabel >, ARGBType > {
		@Override
		public void convert( final LabelingType< SegmentLabel > input, final ARGBType output ) {
			output.set(
					input.getIndex().getInteger() % 2 == 1
					? ARGBType.rgba( 255, 0, 0, 64 )
					: ARGBType.rgba( 0, 0, 0, 0 ) );
		}
	}

	public static class ARGBCompositeAlphaBlender implements Converter< NumericComposite< ARGBType >, ARGBType >
	{
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

	public static class RandomAccessibleIntervalStack< T > extends AbstractInterval implements RandomAccessibleInterval< T > {

		private final RandomAccessibleInterval< T >[] slices;

		@SuppressWarnings( "unchecked" )
		public RandomAccessibleIntervalStack( final List< RandomAccessibleInterval< T > > hyperslices ) {
			super( hyperslices.get( 0 ).numDimensions() + 1 );
			slices = hyperslices.toArray( new RandomAccessibleInterval[ hyperslices.size() ] );
			for ( int d = 0; d < n - 1; ++d ) {
				min[ d ] = slices[ 0 ].min( d );
				max[ d ] = slices[ 0 ].max( d );
			}
			min[ n - 1 ] = 0;
			max[ n - 1 ] = slices.length - 1;
		}

		public static class RA< T > implements RandomAccess< T > {

			private final int n;

			private final int sd;

			private int slice;

			private final long[] tmpLong;

			private final int[] tmpInt;

			private final RandomAccess< T >[] sliceAccesses;

			private RandomAccess< T > sliceAccess;

			public RA( final RandomAccessibleInterval< T >[] slices ) {
				this( slices, null );
			}

			@SuppressWarnings( "unchecked" )
			public RA( final RandomAccessibleInterval< T >[] slices, final Interval interval ) {
				n = slices[ 0 ].numDimensions() + 1;
				sd = n - 1;
				slice = 0;
				tmpLong = new long[ sd ];
				tmpInt = new int[ sd ];
				sliceAccesses = new RandomAccess[ slices.length ];
				if ( interval == null )
				{
					for ( int i = 0; i < slices.length; ++i )
						sliceAccesses[ i ] = slices[ i ].randomAccess();
				}
				else
				{
					final long[] smin = new long[ sd ];
					final long[] smax = new long[ sd ];
					for ( int d = 0; d < sd; ++d ) {
						smin[ d ] = interval.min( d );
						smax[ d ] = interval.max( d );
					}
					final Interval sliceInterval = new FinalInterval( smin, smax );
					for ( int i = 0; i < slices.length; ++i )
						sliceAccesses[ i ] = slices[ i ].randomAccess( sliceInterval );
				}
				sliceAccess = sliceAccesses[ slice ];
			}

			private RA( final RA< T > a ) {
				sliceAccesses = Util.genericArray( a.sliceAccesses.length );
				for ( int i = 0; i < sliceAccesses.length; ++i )
					sliceAccesses[ i ] = a.sliceAccesses[ i ].copyRandomAccess();
				slice = a.slice;
				sliceAccess = sliceAccesses[ slice ];
				n = a.n;
				sd = a.sd;
				tmpLong = a.tmpLong.clone();
				tmpInt = a.tmpInt.clone();
			}

			@Override
			public void localize( final int[] position ) {
				for ( int d = 0; d < sd; ++d )
					position[ d ] = sliceAccess.getIntPosition( d );
				position[ sd ] = slice;
			}

			@Override
			public void localize( final long[] position ) {
				for ( int d = 0; d < sd; ++d )
					position[ d ] = sliceAccess.getLongPosition( d );
				position[ sd ] = slice;
			}

			@Override
			public int getIntPosition( final int d ) {
				return ( d < sd ) ? sliceAccess.getIntPosition( d ) : slice;
			}

			@Override
			public long getLongPosition( final int d ) {
				return ( d < sd ) ? sliceAccess.getLongPosition( d ) : slice;
			}

			@Override
			public void localize( final float[] position ) {
				for ( int d = 0; d < sd; ++d )
					position[ d ] = sliceAccess.getLongPosition( d );
				position[ sd ] = slice;
			}

			@Override
			public void localize( final double[] position ) {
				for ( int d = 0; d < sd; ++d )
					position[ d ] = sliceAccess.getLongPosition( d );
				position[ sd ] = slice;
			}

			@Override
			public float getFloatPosition( final int d ) {
				return getLongPosition( d );
			}

			@Override
			public double getDoublePosition( final int d ) {
				return getLongPosition( d );
			}

			@Override
			public int numDimensions() {
				return n;
			}

			@Override
			public void fwd( final int d ) {
				if ( d < sd )
					sliceAccess.fwd( d );
				else
					setSlice( slice + 1 );
			}

			@Override
			public void bck( final int d ) {
				if ( d < sd )
					sliceAccess.bck( d );
				else
					setSlice( slice - 1 );
			}

			@Override
			public void move( final int distance, final int d ) {
				if ( d < sd )
					sliceAccess.move( distance, d );
				else
					setSlice( slice + distance );
			}

			@Override
			public void move( final long distance, final int d ) {
				if ( d < sd )
					sliceAccess.move( distance, d );
				else
					setSlice( slice + ( int ) distance );
			}

			@Override
			public void move( final Localizable distance ) {
				for ( int d = 0; d < sd; ++d )
					sliceAccess.move( distance.getLongPosition( d ), d );
				setSlice( slice + distance.getIntPosition( sd ) );
			}

			@Override
			public void move( final int[] distance ) {
				for ( int d = 0; d < sd; ++d )
					sliceAccess.move( distance[ d ], d );
				setSlice( slice + distance[ sd ] );
			}

			@Override
			public void move( final long[] distance ) {
				for ( int d = 0; d < sd; ++d )
					sliceAccess.move( distance[ d ], d );
				setSlice( slice + ( int ) distance[ sd ] );
			}

			@Override
			public void setPosition( final Localizable position ) {
				for ( int d = 0; d < sd; ++d )
					tmpLong[ d ] = position.getLongPosition( d );
				sliceAccess.setPosition( tmpLong );
				setSlice( position.getIntPosition( sd ) );
			}

			@Override
			public void setPosition( final int[] position ) {
				System.arraycopy( position, 0, tmpInt, 0, sd );
				sliceAccess.setPosition( tmpInt );
				setSlice( position[ sd ] );
			}

			@Override
			public void setPosition( final long[] position ) {
				System.arraycopy( position, 0, tmpLong, 0, sd );
				sliceAccess.setPosition( tmpLong );
				setSlice( position[ sd ] );
			}

			@Override
			public void setPosition( final int position, final int d ) {
				if ( d < sd )
					sliceAccess.setPosition( position, d );
				else
					setSlice( position );
			}

			@Override
			public void setPosition( final long position, final int d ) {
				if ( d < sd )
					sliceAccess.setPosition( position, d );
				else
					setSlice( position );
			}

			private void setSlice( final int i ) {
				if ( i != slice ) {
					slice = i;
					if ( slice >= 0 && slice < sliceAccesses.length ) {
						sliceAccesses[ slice ].setPosition( sliceAccess );
						sliceAccess = sliceAccesses[ slice ];
					}
				}
			}

			private void setSlice( final long i ) {
				setSlice( ( int ) i );
			}

			@Override
			public T get() {
				return sliceAccess.get();
			}

			@Override
			public RA< T > copy() {
				return new RA< T >( this );
			}

			@Override
			public RA< T > copyRandomAccess() {
				return copy();
			}
		}

		@Override
		public RandomAccess< T > randomAccess() {
			return new RA< T >( slices );
		}

		@Override
		public RandomAccess< T > randomAccess( final Interval interval ) {
			return new RA< T >( slices, interval );
		}
	}
}
