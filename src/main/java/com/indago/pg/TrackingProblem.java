package com.indago.pg;

import java.util.List;

public interface TrackingProblem
{

	List< ? extends SegmentationProblem > getTimepoints();
}
