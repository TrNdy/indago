package com.indago.fg;

import com.indago.fg.domain.Domain;
import com.indago.fg.util.Typed;

public interface Marginal< T, D extends Domain< T > > extends Typed< D > {

	public double[] get();

	public void set( double[] probabilities );
}
