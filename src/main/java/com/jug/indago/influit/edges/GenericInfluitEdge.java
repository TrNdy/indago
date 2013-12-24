/**
 *
 */
package com.jug.indago.influit.edges;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jug.indago.influit.data.InfluitFormatIdentifyer;
import com.jug.indago.influit.nodes.InfluitNode;


/**
 * @author jug
 */
public class GenericInfluitEdge< T1 extends InfluitNode, T2 extends InfluitNode > implements InfluitEdge {

	T1 nodeFrom;
	T2 nodeTo;
	InfluitFormatIdentifyer selectedFormat;

	/**
	 * @param imgPlusNode
	 * @param slicerLoopNode
	 */
	public GenericInfluitEdge( final T1 sourceNode, final T2 sinkNode ) {
		nodeFrom = sourceNode;
		nodeTo = sinkNode;

		final List< InfluitFormatIdentifyer > possibleFormats = getCommonFormats();
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
	public InfluitFormatIdentifyer getFormat() {
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
	public List< InfluitFormatIdentifyer > getCommonFormats() {
		final List< InfluitFormatIdentifyer > sourceFormats = nodeFrom.getSupportedOutputFormats();
		final List< InfluitFormatIdentifyer > sinkFormats = nodeTo.getSupportedInputFormats();
		final List< InfluitFormatIdentifyer > intersection = new ArrayList< InfluitFormatIdentifyer >();
		intersection.addAll( sourceFormats );
		retainCompatible( intersection, sinkFormats );
		return intersection;
	}

	/**
	 * Removes from <code>list</code> all those
	 * <code>InfluitFormatIdentifyer</code> that are NOT contained in
	 * <code>toRetain</code>.
	 *
	 * @see InfluitFormatIdentifyer.isCompatible(r)
	 *
	 * @param list
	 * @param toRetain
	 * @return true if <code>list</code> was modified.
	 */
	private boolean retainCompatible( final List< InfluitFormatIdentifyer > list, final List< InfluitFormatIdentifyer > toRetain ) {
		boolean modified = false;
		final Iterator< InfluitFormatIdentifyer > iterList = list.iterator();
		while ( iterList.hasNext() ) {
			boolean found = false;
			final InfluitFormatIdentifyer currEl = iterList.next();
			for ( final InfluitFormatIdentifyer r : toRetain ) {
				currEl.isCompatible( r );
				found = true;
			}
			if ( !found ) {
				iterList.remove();
				modified = true;
			}
		}
		return modified;

	}

}
