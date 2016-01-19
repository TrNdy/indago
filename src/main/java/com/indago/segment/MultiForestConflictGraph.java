package com.indago.segment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.indago.tracking.seg.ConflictGraph;
import com.indago.tracking.seg.ConflictSet;
import com.indago.tracking.seg.SegmentVar;

import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class MultiForestConflictGraph implements ConflictGraph {

	private Collection< ConflictSet > conflictCliques;

	private final Collection< LabelingForest > forests;

	public MultiForestConflictGraph( final Collection< LabelingForest > forests ) {
		this.forests = forests;
	}

	@Override
	public Collection< ConflictSet > getConflictCliques() {
		if ( conflictCliques == null )
			conflictCliques = getConflictCliques( forests );
		return conflictCliques;
	}

	/**
	 * This method computes the conflict cliques. This can be done somewhat
	 * efficiently since we know that our segments come from multiple forests.
	 */
	public static Collection< ConflictSet > getConflictCliques(
			final Collection< LabelingForest > forests ) {
		final Collection< ConflictSet > cliques = new ArrayList< ConflictSet >();

		// Adding the 'trivial' cliques (the ones given by each leave-root path in all trees)
		for ( final LabelingForest forest : forests )
			cliques.addAll( forest.getConflictCliques() );

		// Adding the overlap cliques (for each pair of forests in multiRoots)
		final ArrayList< LabelingForest > forestList = new ArrayList<>( forests );
		final int numForests = forestList.size();
		for ( int idxA = 0; idxA < numForests - 1; idxA++ ) {
			for ( int idxB = idxA + 1; idxB < numForests; idxB++ ) {
				cliques.addAll( getInterForestCliques( forestList.get( idxA ).roots(), forestList.get( idxB ).roots() ) );
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
	private static Collection< ConflictSet > getInterForestCliques(
			final Set< LabelingTreeNode > rootsA,
			final Set< LabelingTreeNode > rootsB ) {

		final Collection< ConflictSet > cliques = new ArrayList< ConflictSet >();

		// Determine all leave nodes in A and B
		final Set< Collection< LabelingTreeNode > > leavesPerRootInA = new HashSet< Collection< LabelingTreeNode >>();
		final Set< Collection< LabelingTreeNode > > leavesPerRootInB = new HashSet< Collection< LabelingTreeNode >>();
		for ( final LabelingTreeNode root : rootsA ) {
			leavesPerRootInA.add( root.getLeaves() );
		}
		for ( final LabelingTreeNode root : rootsB ) {
			leavesPerRootInB.add( root.getLeaves() );
		}

		// ACCUMULATE visible edges (as pairs of connected segments)...
		final Set< Pair< LabelingTreeNode, LabelingTreeNode > > accumulatedSegmentPairs = new HashSet< Pair< LabelingTreeNode, LabelingTreeNode > >();
		// ...between each pair of paths from (A,B)
		for ( final Collection< LabelingTreeNode > currLeavesA : leavesPerRootInA ) {
			for ( final LabelingTreeNode currLeaveA : currLeavesA ) {
				final ArrayList< LabelingTreeNode > currPathA = new ArrayList< LabelingTreeNode >();
				currPathA.add( currLeaveA );
				LabelingTreeNode p = currLeaveA.getParent();
				while ( p != null ) {
					currPathA.add( p );
					p = p.getParent();
				}

				for ( final Collection< LabelingTreeNode > currLeavesB : leavesPerRootInB ) {
					for ( final LabelingTreeNode currLeaveB : currLeavesB ) {
						final ArrayList< LabelingTreeNode > currPathB = new ArrayList< LabelingTreeNode >();
						currPathB.add( currLeaveB );
						p = currLeaveB.getParent();
						while ( p != null ) {
							currPathB.add( p );
							p = p.getParent();
						}

						accumulateVisibleInterPathEdges( accumulatedSegmentPairs, currPathA, currPathB );
					}
				}
			}
		}

		// Finally add cliques corresponding to accumulated 'visible' edges
		for ( final Pair< LabelingTreeNode, LabelingTreeNode > visibleEdge : accumulatedSegmentPairs ) {
			final ConflictSet newClique = new ConflictSet();
			newClique.add( new SegmentVar( visibleEdge.getA().getSegment() ) );
			for ( final LabelingTreeNode ancestor : visibleEdge.getA().getAncestors() )
				newClique.add( new SegmentVar( ancestor.getSegment() ) );
			newClique.add( new SegmentVar( visibleEdge.getB().getSegment() ) );
			for ( final LabelingTreeNode ancestor : visibleEdge.getB().getAncestors() )
				newClique.add( new SegmentVar( ancestor.getSegment() ) );
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
	private static void accumulateVisibleInterPathEdges( final Set< Pair< LabelingTreeNode, LabelingTreeNode > > edgeAccumulator, final ArrayList< LabelingTreeNode > currPathA, final ArrayList< LabelingTreeNode > currPathB ) {
		int maxIdxB = currPathB.size();

		for ( final LabelingTreeNode segmentA : currPathA ) {

			for ( int idxPathB = 0; idxPathB < maxIdxB; idxPathB++ ) {

				final LabelingTreeNode segmentB = currPathB.get( idxPathB );
				if ( segmentA.getSegment().conflictsWith( segmentB.getSegment() ) ) {
					maxIdxB = idxPathB;
					edgeAccumulator.add( new ValuePair< LabelingTreeNode, LabelingTreeNode >( segmentA, segmentB ) );
				}
			}
		}
	}

}
