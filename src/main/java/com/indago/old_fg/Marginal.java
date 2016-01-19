package com.indago.old_fg;

import com.indago.old_fg.domain.Domain;

public interface Marginal< T, D extends Domain< T > > {

	public double[] get();

	public void set( double[] probabilities );
}
