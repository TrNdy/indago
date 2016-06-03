package com.indago.data.segmentation;

import java.util.ArrayList;

import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelingMapping;

/**
 * A fragment is a set of pixels that share the same set of {@link LabelData labels}.
 * A {@link LabelingFragment} does not store those pixels, only the set of labels.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class LabelingFragment {

	private final int index;

	private final int labelingMappingIndex;

	private final ArrayList< LabelData > segments;

	LabelingFragment(
			final int index,
			final int labelingMappingIndex
			) {
		this.index = index;
		this.labelingMappingIndex = labelingMappingIndex;
		this.segments = new ArrayList<>();
	}

	/**
	 * Get the index of this fragment. Indices <em>0, 1, 2, ...</em> are
	 * assigned by {@link LabelingBuilder} and used in ordered lists of
	 * fragments (for each segment).
	 *
	 * @return the fragment index of this fragment.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * The index in the {@link LabelingMapping} that corresponds to this
	 * fragment (see {@link LabelingMapping#labelsAtIndex(int)}). This is the
	 * value that pixels of this fragment have in the
	 * {@link ImgLabeling#getIndexImg()}.
	 *
	 * @return the {@link LabelingMapping} index that corresponds to this
	 *         fragment.
	 */
	public int getLabelingMappingIndex() {
		return labelingMappingIndex;
	}

	// TODO: rename getSegmentLabels
	public ArrayList< LabelData > getSegments() {
		return segments;
	}
}