package com.indago.fg;

import com.indago.fg.domain.Domain;

public interface Marginal< T, D extends Domain< T > > {

	public double[] get();

	public void set( double[] probabilities );
}
