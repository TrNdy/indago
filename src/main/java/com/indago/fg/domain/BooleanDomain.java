/**
 * 
 */
package com.indago.fg.domain;

/**
 * A boolean domain is simply a BinaryDomain<Boolean> with Boolean.FALSE as zero
 * element and Boolean.TRUE as one element.
 * 
 * @author jug
 */
public class BooleanDomain extends BinaryDomain< Boolean > {

	/**
	 * Creates a BooleanDomain instance.
	 */
	public BooleanDomain() {
		super( Boolean.FALSE, Boolean.TRUE );
	}

}
