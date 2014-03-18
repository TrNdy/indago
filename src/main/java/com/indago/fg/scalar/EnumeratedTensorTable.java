package com.indago.fg.scalar;

import net.imglib2.util.Util;

import com.indago.fg.LabelValue;

public class EnumeratedTensorTable extends TensorTable< EnumeratedDomain, EnumeratedFunctionDomain, LabelValue< ?, EnumeratedDomain > > implements EnumeratedFunction {

	private final int id;

	public EnumeratedTensorTable( final EnumeratedFunctionDomain domain, final double[] entries, final int id  ) {
		super( domain, entries );
		this.id = id;
	}

	public EnumeratedTensorTable( final int[] statesForDim, final double[] entries, final int id ) {
		this( EnumeratedFunctionDomain.getForSizes( statesForDim ), entries, id );
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + id + ") : " + domain + " -> R : " + Util.printCoordinates( entries );
	}

}
