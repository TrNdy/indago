/**
 *
 */
package com.indago.fkt;

import java.io.Serializable;

/**
 * @author jug
 */
public interface Function1D< T > extends Serializable {

	public T evaluate( double x );
}
