package com.indago.data.segmentation;

/**
 * A node in a segment hypothesis tree. This is an
 * {@link AbstractHypothesisTreeNode} with an associated {@link Segment}.
 *
 * @param <T>
 *            recursive type of this {@link HypothesisTreeNode}
 * @param <S>
 *            type of segment associated with this node.
 *
 * @author Tobias Pietzsch
 */
public class HypothesisTreeNode< T extends HypothesisTreeNode< T, S >, S extends Segment > extends AbstractHypothesisTreeNode< T > {

	private final S segment;

	public HypothesisTreeNode( final S segment ) {
		this.segment = segment;
	}

	public S getSegment() {
		return segment;
	}
}
