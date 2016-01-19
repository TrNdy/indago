package com.indago.brandnewfg;

import java.util.List;

public class TensorTable implements Function {

	private final double[] entries;

	private final List< Domain > argumentDomains;

	public TensorTable(
			final double[] entries,
			final List< Domain > argumentDomains ) {
		this.entries = entries;
		this.argumentDomains = argumentDomains;
	}

	public int getArity() {
		return argumentDomains.size();
	}

	@Override
	public List< Domain > getArgumentDomains() {
		return argumentDomains;
	}

	// TODO: Do we need this? Do we need it to be public?
	/**
	 * @return flattened array of tensor entries.
	 */
	public double[] getTensorEntries() {
		return entries;
	}

	@Override
	public double evaluate( final int... arguments ) {
		assert arguments.length == getArity();

		int d = getArity() - 1;
		int i = arguments[ d ];
		for ( --d; d >= 0; --d )
			i = i * argumentDomains.get( d ).getCardinality() + arguments[ d ];

		return entries[ i ];
	}
}
