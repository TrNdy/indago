/**
 * 
 */
package com.indago.fg.function;

import com.indago.fg.domain.IntLabelDomain;
import com.indago.fg.domain.IntLabelFunctionDomain;

/**
 * @author jug
 */
public class IntLabelNandFunction extends PottsFunction< Integer, IntLabelDomain > implements IntLabelFunction {

	/**
	 * @param cost
	 */
	public IntLabelNandFunction( final double cost ) {
		super( cost );
	}

	/**
	 * @see com.indago.fg.function.Function#getDomain()
	 */
	@Override
	public IntLabelFunctionDomain getDomain() {
		return IntLabelFunctionDomain.getArbitrary( 2 );
	}
}
