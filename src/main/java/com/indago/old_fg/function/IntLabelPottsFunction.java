/**
 * 
 */
package com.indago.old_fg.function;

import com.indago.old_fg.domain.IntLabelDomain;
import com.indago.old_fg.domain.IntLabelFunctionDomain;

/**
 * @author jug
 */
public class IntLabelPottsFunction extends PottsFunction< Integer, IntLabelDomain > implements IntLabelFunction {

	/**
	 * @param cost
	 */
	public IntLabelPottsFunction( final double cost ) {
		super( cost );
	}

	/**
	 * @see com.indago.old_fg.function.Function#getDomain()
	 */
	@Override
	public IntLabelFunctionDomain getDomain() {
		return IntLabelFunctionDomain.getArbitrary( 2 );
	}
}
