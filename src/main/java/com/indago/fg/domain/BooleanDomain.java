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
	protected BooleanDomain() {
		super( Boolean.FALSE, Boolean.TRUE );
	}

	protected static BooleanDomain theBooleanDomain = new BooleanDomain();

	public static BooleanDomain get()
	{
		return theBooleanDomain;
	}

}
