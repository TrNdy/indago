package com.indago.segment;

import java.util.ArrayList;

/**
 * A fragment is a set of pixels that share the same set of {@link LabelData labels}.
 * A {@link LabelingFragment} does not store those pixels, only the set of labels.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class LabelingFragment {

	private final int index;

	private final ArrayList< LabelData > segments;

	LabelingFragment( final int index ) {
		this.index = index;
		this.segments = new ArrayList<>();
	}

	public int getIndex() {
		return index;
	}

	// TODO: rename getSegmentLabels
	public ArrayList< LabelData > getSegments() {
		return segments;
	}
}