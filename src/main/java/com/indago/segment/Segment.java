package com.indago.segment;

import net.imglib2.EuclideanSpace;
import net.imglib2.Localizable;

/**
 * A segment (hypothesis).
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface Segment extends EuclideanSpace, Iterable< Localizable > {

	public long getArea();

	public Localizable getCenterOfMass();

	public boolean conflictsWith( final Segment segment );
}
