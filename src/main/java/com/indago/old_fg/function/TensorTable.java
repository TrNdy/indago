package com.indago.old_fg.function;

import java.util.List;

import com.indago.old_fg.domain.Domain;
import com.indago.old_fg.domain.FunctionDomain;
import com.indago.old_fg.value.Value;

public class TensorTable< D extends Domain< ? >, FD extends FunctionDomain< D >, V extends Value< ?, D > > implements Function< D, V > {

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
