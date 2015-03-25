package com.indago.fg.function;

import net.imglib2.util.Util;

import com.indago.fg.domain.BooleanDomain;
import com.indago.fg.domain.BooleanFunctionDomain;
import com.indago.fg.value.Value;

public class BooleanTensorTable extends TensorTable< BooleanDomain, BooleanFunctionDomain, Value< Boolean, BooleanDomain > > implements BooleanFunction {

	private final int id;

	/**
	 * @param domain
	 * @param entries
	 */
	public BooleanTensorTable( final int numDimensions, final double[] entries, final int id ) {
		super( BooleanFunctionDomain.getForNumDimensions( numDimensions ), entries );
		this.id = id;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + id + ") : " + domain + " -> R : " + Util.printCoordinates( entries );
	}

}
