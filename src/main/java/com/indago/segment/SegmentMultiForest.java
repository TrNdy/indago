package com.indago.segment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class SegmentMultiForest implements HypothesisMultiForest< Segment > {

	public static SegmentMultiForest fromLabelingForests( final List< LabelingForest > labelingForests ) {
		return new SegmentMultiForestBuilder( labelingForests ).getSegmentMultiForest();
	}

	private final List< Set< Segment > > multiRoots;

	SegmentMultiForest( final List< SegmentForest > segmentForests ) {
		this.multiRoots = new ArrayList< Set< Segment > >();
		for ( final SegmentForest segmentForest : segmentForests ) {
			this.multiRoots.add( segmentForest.roots() );
		}
	}

	SegmentMultiForest( final Collection< Collection< ? extends Segment > > rootsCollection ) {
		this.multiRoots = new ArrayList< Set< Segment > >();
		for ( final Collection< ? extends Segment > roots : rootsCollection ) {
			final HashSet< Segment > newRootSet = new HashSet< Segment >();
			newRootSet.addAll( roots );
			this.multiRoots.add( newRootSet );
		}
	}

	public Set< Segment > roots() {
		final HashSet< Segment > roots = new HashSet< Segment >();
		for ( final Collection< ? extends Segment > rootCollection : multiRoots ) {
			roots.addAll( rootCollection );
		}
		return roots;
	}

	/**
	 * This method computes the conflict cliques.
	 * This can be done somewhat efficiently since we know that our segments
	 * come from multiple forests.
	 */
	@Override
	public Collection< ? extends Collection< Segment > > getConflictGraphCliques() {
		final List< Collection< Segment > > leavesPerRoot = new ArrayList< Collection< Segment >>();
		final List< Collection< Segment > > cliques = new ArrayList< Collection< Segment >>();

		// Adding the 'trivial' cliques (the ones given by each leave-root path in all trees)
		for ( final Segment root : roots() ) {
			leavesPerRoot.add( root.getLeaves() );

			for ( final Segment leave : root.getLeaves() ) {
				final ArrayList< Segment > newClique = new ArrayList< Segment >();
				newClique.add( leave );
				newClique.addAll( leave.getAncestors() );
				cliques.add( newClique );
			}
		}

		// Adding the overlap cliques (for each pair of forests in multiRoots)
		for ( int idxA = 0; idxA < multiRoots.size() - 1; idxA++ ) {
			for ( int idxB = idxA + 1; idxB < multiRoots.size(); idxB++ ) {
				final Set< Segment > A = multiRoots.get( idxA );
				final Set< Segment > B = multiRoots.get( idxB );

				cliques.addAll( getInterForestCliques( A, B ) );
			}
		}

		return cliques;
	}

	/**
	 * Adds all 'visible' cliques between all paths from any leave to
	 * corresponding root node existing between two forests (given by set of
	 * root nodes).
	 * 
	 * @param rootsA
	 *            roots of one forest
	 * @param rootsB
	 *            roots of the other forest
	 */
	private List< Collection< Segment > > getInterForestCliques( final Set< Segment > rootsA, final Set< Segment > rootsB ) {

		final List< Collection< Segment > > cliques = new ArrayList< Collection< Segment >>();

		// Determine all leave nodes in A and B
		final Set< Collection< Segment > > leavesPerRootInA = new HashSet< Collection< Segment >>();
		final Set< Collection< Segment > > leavesPerRootInB = new HashSet< Collection< Segment >>();
		for ( final Segment root : rootsA ) {
			leavesPerRootInA.add( root.getLeaves() );
		}
		for ( final Segment root : rootsB ) {
			leavesPerRootInB.add( root.getLeaves() );
		}

		// ACCUMULATE visible edges (as pairs of connected segments)...
		final Set< Pair< Segment, Segment > > accumulatedSegmentPairs = new HashSet< Pair< Segment, Segment > >();
		// ...between each pair of paths from (A,B)
		for ( final Collection< Segment > currLeavesA : leavesPerRootInA ) {
			for ( final Segment currLeaveA : currLeavesA ) {
				final ArrayList< Segment > currPathA = new ArrayList< Segment >();
				currPathA.add( currLeaveA );
				currPathA.addAll( currLeaveA.getAncestors() );

				for ( final Collection< Segment > currLeavesB : leavesPerRootInB ) {
					for ( final Segment currLeaveB : currLeavesB ) {
						final ArrayList< Segment > currPathB = new ArrayList< Segment >();
						currPathB.add( currLeaveB );
						currPathB.addAll( currLeaveB.getAncestors() );

						accumulateVisibleInterPathEdges( accumulatedSegmentPairs, currPathA, currPathB );
					}
				}
			}
		}

		// Finally add cliques corresponding to accumulated 'visible' edges
		for ( final Pair< Segment, Segment > visibleEdge : accumulatedSegmentPairs ) {
			final List< Segment > newClique = new ArrayList< Segment >();
			newClique.add( visibleEdge.getA() );
			newClique.addAll( visibleEdge.getA().getAncestors() );
			newClique.add( visibleEdge.getB() );
			newClique.addAll( visibleEdge.getB().getAncestors() );
			cliques.add( newClique );
		}

		return cliques;
	}

	/**
	 * Adds all 'visible' cliques between given paths.
	 * 
	 * @param edgeAccumulator
	 *            data structure holding all 'visible' edges found so far (to
	 *            avoid adding duplicates)
	 * @param currPathA
	 *            list containing path A starting from leave node, ending in
	 *            root.
	 * @param currPathB
	 *            list containing path B starting from leave node, ending in
	 *            root.
	 */
	private void accumulateVisibleInterPathEdges( final Set< Pair< Segment, Segment > > edgeAccumulator, final ArrayList< Segment > currPathA, final ArrayList< Segment > currPathB ) {
		int maxIdxB = currPathB.size();

		for ( final Segment segmentA : currPathA ) {

			for ( int idxPathB = 0; idxPathB < maxIdxB; idxPathB++ ) {

				final Segment segmentB = currPathB.get( idxPathB );
				if ( segmentA.conflictsWith( segmentB ) ) {
					maxIdxB = idxPathB;
					edgeAccumulator.add( new ValuePair< Segment, Segment >( segmentA, segmentB ) );
				}
			}
		}
	}
}
