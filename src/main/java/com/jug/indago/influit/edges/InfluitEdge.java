/**
 *
 */
package com.jug.indago.influit.edges;

import java.util.List;

import com.jug.indago.influit.data.InfluitFormatIdentifyer;

/**
 * @author jug
 */
public interface InfluitEdge {

	@Override
	public String toString();

	public InfluitFormatIdentifyer getFormat();

	public List< InfluitFormatIdentifyer > getCommonFormats();
}
