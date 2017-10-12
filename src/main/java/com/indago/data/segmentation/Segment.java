package com.indago.data.segmentation;

import net.imglib2.EuclideanSpace;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.IterableRegion;

/**
 * A segment (hypothesis).
 *
 * @author Tobias Pietzsch
 */
public interface Segment extends EuclideanSpace {

	public long getArea();

	public RealLocalizable getCenterOfMass();

	public boolean conflictsWith( final Segment segment );

	public IterableRegion< ? > getRegion();
}
