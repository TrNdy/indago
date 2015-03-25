/**
 *
 */
package com.indago.fg.function;

import com.indago.fg.domain.BooleanDomain;
import com.indago.fg.domain.BooleanFunctionDomain;
import com.indago.fg.value.BooleanValue;

/**
 * @author jug
 */
public class BooleanNandFunction extends NandFunction< Boolean, BooleanDomain > implements BooleanFunction {

	/**
	 * @param cost
	 */
	public BooleanNandFunction( final double cost ) {
		super( new BooleanValue( Boolean.FALSE ), cost );
	}

	/**
	 * @see com.indago.fg.function.Function#getDomain()
	 */
	@Override
	public BooleanFunctionDomain getDomain() {
		return BooleanFunctionDomain.getForNumDimensions( 2 );
	}
}
