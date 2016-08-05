/**
 *
 */
package com.indago.costs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

/**
 * @author jug
 */
public class CostParams implements Iterable {

	private final List< Pair< String, Double > > params = new ArrayList<>();
	private final Map< String, Integer > name2index = new HashMap<>();

	public void add( final String name, final double value ) {
		params.add( new ValuePair< String, Double >( name, value ) );
		name2index.put( name, params.size() - 1 );
	}

	public int size() {
		return params.size();
	}

	public double get( final int i ) {
		return params.get( i ).getB();
	}

	public String getName( final int i ) {
		return params.get( i ).getA();
	}

	public double get( final String name ) {
		return get( name2index.get( name ) );
	}

	public void set( final int i, final double value ) {
		final String name = params.get( i ).getA();
		params.remove( i );
		params.add( i, new ValuePair< String, Double >( name, value ) );
	}

	public void set( final String name, final double value ) {
		final int i = name2index.get( name );
		params.remove( i );
		params.add( i, new ValuePair< String, Double >( name, value ) );
	}

	public double[] getAsArray() {
		final double[] ret = new double[ params.size() ];
		int i = 0;
		for ( final Pair< String, Double > p : params ) {
			ret[ i ] = p.getB();
			i++;
		}
		return ret;
	}

	public void setFromArray( final double[] array ) {
		int i = 0;
		for ( final double d : array ) {
			set( i, d );
			i++;
		}
	}

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator iterator() {
		return params.iterator();
	}
}
