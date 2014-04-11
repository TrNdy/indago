package com.indago.segment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.imglib2.tree.Util;

public class SegmentForest implements HypothesisForest< Segment >
{
	public static SegmentForest fromLabelingForest( final LabelingForest labelingForest )
	{
		return new SegmentForestBuilder( labelingForest ).getSegmentForest();
	}

	private final HashSet< Segment > roots;

	SegmentForest( final Collection< ? extends Segment > roots )
	{
		this.roots = new HashSet< Segment >();
		this.roots.addAll( roots );
	}

	@Override
	public Set< Segment > roots()
	{
		return roots;
	}

	/**
	 * This method recomputes the conflict cliques. This is potentially
	 * expensive -- call it once and cache the result!
	 */
	@Override
	public Collection< ? extends Collection< Segment > > getConflictGraphCliques()
	{
		final ArrayList< Segment > leafs = Util.getLeafs( this );
		final ArrayList< ArrayList< Segment > > cliques = new ArrayList< ArrayList< Segment > >();
		for ( final Segment leaf : leafs )
		{
			final ArrayList< Segment > clique = new ArrayList< Segment >();
			clique.add( leaf );
			clique.addAll( leaf.getConflictingHypotheses() );
			if ( clique.size() > 1 )
				cliques.add( clique );
		}
		return cliques;
	}
}
