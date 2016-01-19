package com.indago.tracking;

import java.util.List;

public interface TrackingProblem
{

	List< ? extends SegmentationProblem > getTimepoints();
}
