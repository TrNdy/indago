/**
 *
 */
package com.jug.indago.influit.data;


/**
 * @author jug
 */
public class GenericInfluitDatum<T> implements InfluitDatum {

	private T instance;

	/**
	 * Returns the hashCode of the class of the instance of T given during
	 * construction.
	 *
	 * @see com.jug.indago.influit.data.InfluitDatum#getFormatUID()
	 */
	@Override
	public int getFormatUID() {
		return instance.getClass().hashCode();
	}

	/**
	 * Constructs a generic Influit datum.
	 *
	 * @param instance
	 *            one instance of type T.
	 */
	public GenericInfluitDatum( final T instance ) {
		this.instance = instance;
	}

	public T getData() {
		return instance;
	}

	public void setData( final T datum ) {
		this.instance = datum;
	}
}
