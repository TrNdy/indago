package com.indago.segment;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.newlabeling.LabelRegion;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Intervals;

public class LabelingSegment< T > implements Iterable< Localizable > {

	private final LabelRegion< T > region;

	protected LabelingSegment( final LabelRegion< T > region ) {
		this.region = region;
	}

	public T getLabel() {
		return region.getLabel();
	}

	public long getArea() {
		return region.getArea();
	}

	public Localizable getCenterOfMass() {
		return region.getCenterOfMass();
	}

	@Override
	public Iterator< Localizable > iterator() {
		final Cursor< BoolType > c = region.cursor();
		return new Iterator< Localizable >() {

			@Override
			public boolean hasNext() {
				return c.hasNext();
			}

			@Override
			public Localizable next() {
				c.fwd();
				return c;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * @param segment
	 * @return
	 */
	public boolean conflictsWith( final LabelingSegment< T > segment ) {
		if ( Intervals.isEmpty( Intervals.intersect( this.region, segment.region ) ) )
			return false;

		final RandomAccess< BoolType > raMask = region.randomAccess();
		for ( final Localizable localizable : segment ) {
			raMask.setPosition( localizable );
			if ( raMask.get().get() ) { return true; }
		}
		return false;
	}

}
