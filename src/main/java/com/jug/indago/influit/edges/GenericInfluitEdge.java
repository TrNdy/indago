/**
 *
 */
package com.jug.indago.influit.edges;

import com.jug.indago.influit.data.InfluitDatum;
import com.jug.indago.influit.nodes.InfluitNode;


/**
 * @author jug
 */
public class GenericInfluitEdge< T1 extends InfluitNode, T2 extends InfluitNode > implements InfluitEdge {

	/**
	 * @param imgPlusNode
	 * @param slicerLoopNode
	 */
	public GenericInfluitEdge( final T1 sourceNode, final T2 sinkNode ) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NotImplemented";
//		return getFormat().toString();
	}

	/**
	 * @see com.jug.indago.influit.edges.InfluitEdge#getFormat()
	 */
	@Override
	public InfluitDatum getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

}
