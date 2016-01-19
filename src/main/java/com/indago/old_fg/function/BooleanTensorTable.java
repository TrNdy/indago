package com.indago.old_fg.function;

import com.indago.old_fg.domain.BooleanDomain;
import com.indago.old_fg.domain.BooleanFunctionDomain;
import com.indago.old_fg.value.Value;

import net.imglib2.util.Util;

public class BooleanTensorTable extends TensorTable< BooleanDomain, BooleanFunctionDomain, Value< Boolean, BooleanDomain > > implements BooleanFunction {

	private final int id;

	/**
	 * @param domain
	 * @param entries
	 */
	public BooleanTensorTable( final BooleanFunctionDomain domain, final double[] entries, final int id ) {
		super( domain, entries );
		this.id = id;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + id + ") : " + domain + " -> R : " + Util.printCoordinates( entries );
	}

}
