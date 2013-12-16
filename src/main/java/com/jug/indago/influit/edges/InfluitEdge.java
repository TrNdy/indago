/**
 *
 */
package com.jug.indago.influit.edges;

import java.util.List;

/**
 * @author jug
 */
public interface InfluitEdge {

	@Override
	public String toString();

	public String getFormat();

	public List< String > getCommonFormats();
}
