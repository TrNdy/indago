package com.indago.data.segmentation;

import java.util.Collection;
import java.util.HashMap;

import net.imglib2.algorithm.tree.Forest;

/**
 * Utility to print hypothesis forests and conlfict sets.
 * Assigns integer ids to nodes from one or more hypothesis forests and uses
 * these for printing.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class HypothesisPrinter {

	private final HashMap< Object, Integer > segmentToId;

	private int idGenerator = 0;

	public HypothesisPrinter() {
		segmentToId = new HashMap<>();
	}

	public < T extends HypothesisTreeNode< T, ? > > void assignIds( final Forest< T > forest ) {
		for ( final T node : forest.roots() )
			assignIds( node );
	}

	public < T extends HypothesisTreeNode< T, ? > > void assignIds( final T node ) {
		assignId( node.getSegment() );
		for ( final T child : node.getChildren() )
			assignIds( child );
	}

	public void assignId( final Object segment ) {
		Integer id = segmentToId.get( segment );
		if ( id == null ) {
			id = new Integer( idGenerator++ );
			segmentToId.put( segment, id );
		}
	}

	public < T extends HypothesisTreeNode< T, ? > > void printHypothesisForest( final Forest< T > forest ) {
		for ( final T node : forest.roots() )
			printHypothesisTreeNode( "", node );
	}

	private < T extends HypothesisTreeNode< T, ? > > void printHypothesisTreeNode( final String prefix, final T node ) {
		final Integer id = segmentToId.get( node.getSegment() );
		if ( id == null ) {
			assignIds( node );
		}
		System.out.println( prefix + id );
		for ( final T child : node.getChildren() )
			printHypothesisTreeNode( prefix + "  ", child );
	}

	public void printConflictGraphCliques( final ConflictGraph< ? extends Segment > conflictGraph ) {
		final Collection< ? extends Collection< ? extends Segment > > cliques = conflictGraph.getConflictGraphCliques();
		for ( final Collection< ? extends Segment > clique : cliques ) {
			System.out.print( "( " );
			for ( final Segment segment : clique )
				System.out.print( segmentToId.get( segment ) + " " );
			System.out.println( ")" );
		}
	}

}