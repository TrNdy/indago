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
	 * Creates an <code>GenericInfluitEdge</code> if possible.
	 *
	 * @param from
	 *            source vertex of type <code>InfluitNode</code>
	 * @param to
	 *            target vertex of type <code>InfluitNode</code>
	 * @return <code>null</code> in case <code>from</code> and <code>to</code>
	 *         have no common InfluitDatum-type.
	 */
	@SuppressWarnings( "rawtypes" )
	public GenericInfluitEdge< ?, ? > createGenericInfluitEdge( final InfluitNode from, final InfluitNode to ) {
		GenericInfluitEdge ret = new GenericInfluitEdge( from, to );
		if ( ret.getCommonFormats().size() == 0) {
			ret = null;
		}
		return ret;
	}

}
