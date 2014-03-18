package com.indago.fg.scalar;

import java.util.List;

import com.indago.fg.FunctionDomain;
import com.indago.fg.LabelDomain;
import com.indago.fg.LabelValue;
import com.indago.fg.Tensor;

public class TensorTable< D extends LabelDomain< ? >, FD extends FunctionDomain< D >, V extends LabelValue< ?, D > > implements Tensor< D, V > {

	protected FD domain;

	protected final int[] dim;

	protected final double[] entries;

	public TensorTable( final FD domain, final double[] entries ) {
		this.domain = domain;

		final List< ? extends D > arguments = domain.argumentDomains();
		dim = new int[ arguments.size() ];
		for ( int d = 0; d < dim.length; ++d )
			dim[ d ] = arguments.get( d ).size();

		this.entries = entries.clone();
	}

	@Override
	public FD getDomain() {
		return domain;
	}

	@Override
	public double evaluate( final V... values ) {
		assert values.length == dim.length;

		int i = values[ dim.length - 1 ].getAsIndex();
		for ( int d = dim.length - 2; d >= 0; --d )
			i = i * dim[ d ] + values[ d ].getAsIndex();

		return entries[ i ];
	}

}
