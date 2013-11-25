/**
 *
 */
package com.jug.indago.influit.edges;

import com.jug.indago.influit.data.InfluitDatum;

/**
 * @author jug
 */
public interface InfluitEdge {

	@Override
	public String toString();

	public InfluitDatum getFormat();
}
