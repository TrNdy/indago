package com.indago.models;

import java.util.List;

public interface TrackingModel
{

	List< ? extends SegmentationModel > getTimepoints();
}
