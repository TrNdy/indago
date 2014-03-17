package com.indago.fg;

public interface LabelDistribution< T, D extends LabelDomain< T > > extends Typed< D > {

	public double[] get();

	public void set( double[] probabilities );
}
