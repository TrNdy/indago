package com.indago.segment;

public class HypothesisTreeNode< T extends HypothesisTreeNode< T, S >, S > extends AbstractHypothesisTreeNode< T > {

	private final S segment;

	public HypothesisTreeNode( final S segment ) {
		this.segment = segment;
	}

	public S getSegment() {
		return segment;
	}
}
