package com.indago.old_fg.function;

import com.indago.old_fg.domain.IntLabelDomain;
import com.indago.old_fg.domain.IntLabelFunctionDomain;
import com.indago.old_fg.value.Value;

import net.imglib2.util.Util;

public class IntLabelTensorTable extends TensorTable< IntLabelDomain, IntLabelFunctionDomain, Value< Integer, IntLabelDomain > > implements IntLabelFunction {

	private final int id;

	public IntLabelTensorTable( final IntLabelFunctionDomain domain, final double[] entries, final int id ) {
		super( domain, entries );
		this.id = id;
	}

	public IntLabelTensorTable( final int[] statesForDim, final double[] entries, final int id ) {
		this( IntLabelFunctionDomain.getForSizes( statesForDim ), entries, id );
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + id + ") : " + domain + " -> R : " + Util.printCoordinates( entries );
	}

}
