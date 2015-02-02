package com.indago.segment;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.labeling.LabelRegion;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Intervals;

/**
 * A {@link Segment} that is backed by a {@link LabelRegion} providing the
 * segment pixels.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class LabelingSegment implements Segment {

	private final LabelRegion< ? > region;

	protected LabelingSegment( final LabelRegion< ? > region ) {
		this.region = region;
	}

	@Override
	public long getArea() {
		return region.size();
	}

	@Override
	public RealLocalizable getCenterOfMass() {
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
	@Override
	public boolean conflictsWith( final Segment segment ) {
		if ( segment instanceof LabelingSegment )
			if ( Intervals.isEmpty( Intervals.intersect( this.region, ( ( LabelingSegment ) segment ).region ) ) )
				return false;

		final RandomAccess< BoolType > raMask = region.randomAccess();
		for ( final Localizable localizable : segment ) {
			raMask.setPosition( localizable );
			if ( raMask.get().get() ) { return true; }
		}
		return false;
	}

	@Override
	public int numDimensions() {
		return region.numDimensions();
	}
}
