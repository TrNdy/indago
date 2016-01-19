/**
 * 
 */
package com.indago.old_fg.function;

import com.indago.old_fg.domain.BooleanDomain;
import com.indago.old_fg.domain.BooleanFunctionDomain;
import com.indago.old_fg.value.BooleanValue;

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
	 * @see com.indago.old_fg.function.Function#getDomain()
	 */
	@Override
	public BooleanFunctionDomain getDomain() {
		return new BooleanFunctionDomain( 2 );
	}
}
