package com.indago.models;

import java.util.List;

public interface TrackingProblem
{

	List< ? extends SegmentationProblem > getTimepoints();
}
