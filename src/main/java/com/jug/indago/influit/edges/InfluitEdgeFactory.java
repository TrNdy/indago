/**
 *
 */
package com.jug.indago.influit.edges;

import com.jug.indago.influit.nodes.InfluitNode;


/**
 * @author jug
 */
public class InfluitEdgeFactory {

	/**
	 *
	 */
	@SuppressWarnings( "rawtypes" )
	public GenericInfluitEdge< ?, ? > createGenericInfluitEdge( final InfluitNode from, final InfluitNode to ) {
		return new GenericInfluitEdge( from, to );
	}

}
