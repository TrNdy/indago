/**
 * 
 */
package com.indago.old_fg.domain;

import java.util.Arrays;
import java.util.List;

/**
 * @author jug
 */
public class BooleanFunctionDomain implements FunctionDomain< BooleanDomain > {

	private final List< BooleanDomain > domains;

	public BooleanFunctionDomain( final int dimensions ) {
		final BooleanDomain[] domains = new BooleanDomain[ dimensions ];
		for ( int d = 0; d < dimensions; ++d )
			domains[ d ] = new BooleanDomain();
		this.domains = Arrays.asList( domains );
	}

	@Override
	public List< ? extends BooleanDomain > argumentDomains() {
		return domains;
	}

	@Override
	public int numDimensions() {
		return domains.size();
	}

}
