package com.indago.data.segmentation;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.IterableRegion;
import net.imglib2.roi.labeling.LabelRegion;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Intervals;

/**
 * A {@link Segment} that is backed by a {@link LabelRegion} providing the
 * segment pixels.
 *
 * @author Tobias Pietzsch
 */
public class LabelingSegment implements Segment {

	private final LabelRegion< LabelData > region;

	protected LabelingSegment( final LabelRegion< LabelData > region ) {
		this.region = region;
	}

	/**
	 * Returns a unique id that can be used for serialization. Specifically,
	 * this is the unique serialization id that is assigned to the label of the
	 * backing {@link LabelRegion}.
	 *
	 * @returns unique serialization id of the backing {@code LabelRegion}.
	 */
	public int getId() {
		return region.getLabel().getId();
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
	public IterableRegion< ? > getRegion()
	{
		return region;
	}

	/**
	 * @param segment
	 * @return
	 */
	@Override
	public boolean conflictsWith( final Segment segment ) {
		final IterableRegion< ? > segmentRegion = segment.getRegion();
		if ( segment instanceof LabelingSegment )
			if ( Intervals.isEmpty( Intervals.intersect( this.region, segmentRegion ) ) )
				return false;

		final RandomAccess< BoolType > raMask = region.randomAccess();
		final Cursor< ? > cSegment = segmentRegion.cursor();
		while ( cSegment.hasNext() ) {
			cSegment.fwd();
			raMask.setPosition( cSegment );
			if ( raMask.get().get() ) { return true; }
		}
		return false;
	}

	@Override
	public int numDimensions() {
		return region.numDimensions();
	}

	@Override
	public String toString() {
		String ret = "LabelingSegment:";
		final RealLocalizable com = region.getCenterOfMass();
		for ( int i=0; i<com.numDimensions(); i++) {
			ret += String.format("%.0f:",region.getCenterOfMass().getDoublePosition( i ));
		}
		return ret;
	}
}
