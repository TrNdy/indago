/**
 *
 */
package com.jug.indago.influit.edges;

import java.util.ArrayList;
import java.util.List;

import com.jug.indago.influit.nodes.InfluitNode;


/**
 * @author jug
 */
public class GenericInfluitEdge< T1 extends InfluitNode, T2 extends InfluitNode > implements InfluitEdge {

	T1 nodeFrom;
	T2 nodeTo;
	String selectedFormat;

	/**
	 * @param imgPlusNode
	 * @param slicerLoopNode
	 */
	public GenericInfluitEdge( final T1 sourceNode, final T2 sinkNode ) {
		nodeFrom = sourceNode;
		nodeTo = sinkNode;

		final List< String > possibleFormats = getCommonFormats();
		if ( possibleFormats.size() > 0 ) {
			selectedFormat = possibleFormats.get( 0 );
		} else {
			selectedFormat = null;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if ( getFormat() == null ) return "???";
		return getFormat().toString();
	}

	/**
	 * @see com.jug.indago.influit.edges.InfluitEdge#getFormat()
	 */
	@Override
	public String getFormat() {
		return selectedFormat;
	}

	/**
	 * Creates and returns a list of formats (of type <code>InfluitDatum</code>)
	 * that the two <code>InfluitNode</code>s adjacent to this edge both
	 * support.
	 *
	 * @see com.jug.indago.influit.edges.InfluitEdge#getCommonFormats()
	 */
	@Override
	public List< String > getCommonFormats() {
		final List< String > sourceFormats = nodeFrom.getSupportedOutputFormats();
		final List< String > sinkFormats = nodeTo.getSupportedInputFormats();
		final List< String > intersection = new ArrayList< String >();
		intersection.addAll( sourceFormats );
		intersection.retainAll( sinkFormats );  // Here I need some smarter .equals or so... anyways... continue coding HERE!
		return intersection;
	}

}
