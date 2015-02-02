package com.indago.segment;

import net.imglib2.EuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;

/**
 * A segment (hypothesis).
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface Segment extends EuclideanSpace, Iterable< Localizable > {

	public long getArea();

	public RealLocalizable getCenterOfMass();

	public boolean conflictsWith( final Segment segment );
}
