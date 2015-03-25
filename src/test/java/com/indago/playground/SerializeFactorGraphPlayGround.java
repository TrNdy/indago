package com.indago.playground;

import java.util.ArrayList;
import java.util.List;

import com.indago.fg.Assignment;
import com.indago.fg.FactorGraph;
import com.indago.ilp.SolveBooleanFGGurobi;
import com.indago.segment.HypothesisPrinter;
import com.indago.segment.LabelingForest;
import com.indago.segment.LabelingPlus;
import com.indago.segment.LabelingSegment;
import com.indago.segment.MinimalOverlapConflictGraph;
import com.indago.segment.RandomSegmentCosts;
import com.indago.segment.XmlIoLabelingPlus;
import com.indago.segment.fg.FactorGraphFactory;

public class SerializeFactorGraphPlayGround {

	public static void main( final String[] args ) throws Exception {
		final String labelingDataFilename = "/Users/pietzsch/Desktop/labeling.xml";
		final String fgDataFilename = "/Users/pietzsch/Desktop/factorgraph.xml";

		final LabelingPlus labelingPlus = new XmlIoLabelingPlus().load( labelingDataFilename );
		final List< LabelingForest > labelingForestsLoaded = labelingPlus.getLabelingForests();

		final MinimalOverlapConflictGraph conflictGraph = new MinimalOverlapConflictGraph( labelingPlus );
		conflictGraph.getConflictGraphCliques();

		final HypothesisPrinter hp = new HypothesisPrinter();
		for ( final LabelingForest labelingForest : labelingForestsLoaded ) {
			hp.assignIds( labelingForest );
			hp.printHypothesisForest( labelingForest );
		}
		System.out.println();
		hp.printConflictGraphCliques( conflictGraph );

		final ArrayList< LabelingSegment > segments = labelingPlus.getSegments();
		final RandomSegmentCosts costs = new RandomSegmentCosts( segments, 815 ); // assign random costs to segments in MultiForest (for testing purposes)
		final FactorGraph fg = FactorGraphFactory.createFromConflictGraph( segments, conflictGraph, costs ).getFactorGraph();



		final SolveBooleanFGGurobi solver = new SolveBooleanFGGurobi();
		final Assignment assignment = solver.solve( fg );
		System.out.println( assignment );
	}
}
