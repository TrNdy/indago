package net.imglib2.views;

import java.util.List;

import net.imglib2.AbstractInterval;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.util.Util;

public class RandomAccessibleIntervalStack< T > extends AbstractInterval implements RandomAccessibleInterval< T > {

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

	@Override
	public RandomAccess< T > randomAccess() {
		return new RandomAccessibleIntervalStack.RA< T >( slices );
	}

	@Override
	public RandomAccess< T > randomAccess( final Interval interval ) {
		return new RandomAccessibleIntervalStack.RA< T >( slices, interval );
	}

	// TODO: alternative RandomAccess moves all slice RandomAccesses on every Positionable call. Then get() only fwds to one of them. This assumes that we are going to have the slice dimension in the inner loop, which is likely to be the case for example when collapsing into a composite.

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
			if ( interval == null ) {
				for ( int i = 0; i < slices.length; ++i )
					sliceAccesses[ i ] = slices[ i ].randomAccess();
			} else {
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

		private RA( final RandomAccessibleIntervalStack.RA< T > a ) {
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
		public RandomAccessibleIntervalStack.RA< T > copy() {
			return new RandomAccessibleIntervalStack.RA< T >( this );
		}

		@Override
		public RandomAccessibleIntervalStack.RA< T > copyRandomAccess() {
			return copy();
		}
	}
}
