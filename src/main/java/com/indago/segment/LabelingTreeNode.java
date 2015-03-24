package com.indago.segment;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.Localizable;

/**
 * A node in a {@link LabelingForest}.
 * Links to the {@link LabelingSegment} and its {@link LabelData}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class LabelingTreeNode extends HypothesisTreeNode< LabelingTreeNode, LabelingSegment > implements Iterable< Localizable > {

	private final LabelData label;

	public LabelingTreeNode( final LabelingSegment segment, final LabelData label ) {
		super( segment );
		this.label = label;
	}

	public LabelData getLabel() {
		return label;
	}

	@Override
	public Iterator< Localizable > iterator() {
		final Cursor< ? > c = getSegment().getRegion().cursor();
		return new Iterator< Localizable >() {

			@Override
			public boolean hasNext() {
				return c.hasNext();
			}

			@Override
			public Localizable next() {
				c.fwd();
				return c;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
