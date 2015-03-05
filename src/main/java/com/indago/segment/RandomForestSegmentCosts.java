package com.indago.segment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.imagej.ops.Op;
import net.imagej.ops.OpRef;
import net.imagej.ops.features.DefaultAutoResolvingFeatureSet;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.Regions;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.DoubleType;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;

import com.indago.segment.filteredcomponents.FilteredComponentTree;
import com.indago.segment.filteredcomponents.FilteredComponentTree.Filter;
import com.indago.segment.filteredcomponents.FilteredComponentTree.MaxGrowthPerStep;
import com.indago.weka.ArffBuilder;
import com.indago.weka.ArffWriterFactory;

/**
 * Assigns random costs to all segments in a SegmentMultiForest.
 * This is useful for testing artificial setups...
 *
 * @author jug
 */
public class RandomForestSegmentCosts< L extends IntegerType< L > & NativeType< L >, T extends RealType< T > & NativeType< T >>
		implements
		SegmentCosts {

	private final HashMap< Segment, Double > segmentToCost = new HashMap< Segment, Double >();

	private ArrayList< LabelingSegment > segments;
	private MinimalOverlapConflictGraph conflictGraph;
	private ImgLabeling< SegmentLabel, IntType > labeling;

	private RandomForest forest;
	private final ArffBuilder arff;

	private static < L extends IntegerType< L > & NativeType< L >> LabelingBuilder
			getSegmentsFromSumImg(
					final RandomAccessibleInterval< L > sumImg, final L labeltype ) {
		final int minComponentSize = 10;
		final int maxComponentSize = 10000;
		final Filter maxGrowthPerStep = new MaxGrowthPerStep( 1 );
		final boolean darkToBright = false;
		final FilteredComponentTree< L > tree =
				FilteredComponentTree.buildComponentTree(
						sumImg,
						labeltype,
						minComponentSize,
						maxComponentSize,
						maxGrowthPerStep,
						darkToBright );

		final LabelingBuilder builder = new LabelingBuilder( sumImg );
		builder.buildLabelingForest( tree );

		return builder;
	}

	public RandomForestSegmentCosts(
			final RandomAccessibleInterval< L > sumImg,
			final RandomAccessibleInterval< T > img,
			final RandomForest trainedForest,
			final DefaultAutoResolvingFeatureSet< IterableInterval< T >, DoubleType > featureSet,
			final L labeltype ) {

		this( getSegmentsFromSumImg( sumImg, labeltype ), img, trainedForest, featureSet, labeltype );
	}

	public RandomForestSegmentCosts(
			final LabelingBuilder labelingBuilder,
			final RandomAccessibleInterval< T > img,
			final RandomForest trainedForest,
			final DefaultAutoResolvingFeatureSet< IterableInterval< T >, DoubleType > featureSet,
			final L labeltype ) {

		this.labeling = labelingBuilder.getLabeling();
		this.segments = labelingBuilder.getSegments();
		arff = ArffWriterFactory.getArffBuilderFor( featureSet );
		forest = trainedForest;

		conflictGraph = new MinimalOverlapConflictGraph( labelingBuilder );

		for ( final Segment segment : segments ) {

			final IterableInterval< T > pixels = Regions.sample( segment.getRegion(), img );
			final Map< OpRef< ? extends Op >, DoubleType > features = featureSet.compute( pixels );

			arff.addData( features, ArffBuilder.UNKNOWN_INSTANCE );
			final Instance latestInstance = arff.getLatestAddedInstance();
			try {
				final int predictedClass = ( int ) forest.classifyInstance( latestInstance );
				final double[] distrib = forest.distributionForInstance( latestInstance );
				segmentToCost.put( segment, -distrib[ 0 ] );
			} catch ( final Exception e ) {
				System.err.println( "Costs for segment " + segment.toString() + " could not be determined!" );
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param segment
	 * @return the cost assigned to the given segment or Double.MAX_VALUE if
	 *         this segment is unknown.
	 */
	@Override
	public double getCost( final Segment segment ) {
		final Double muh = segmentToCost.get( segment );
		if ( muh != null ) { return muh.doubleValue(); }
		return Double.MAX_VALUE;
	}

	/**
	 * @return The segments given or created during construction of this
	 *         <code>RandomForestSegmentationCosts</code> instance.
	 */
	public ArrayList< LabelingSegment > getSegments() {
		return segments;
	}

	/**
	 * @return the <code>MinimalOverlapConflictGraph</code> built during
	 *         construction of this <code>RandomForestSegmentationCosts</code>
	 *         instance.
	 */
	public MinimalOverlapConflictGraph getConflictGraph() {
		return conflictGraph;
	}

	public ImgLabeling< SegmentLabel, IntType > getLabeling() {
		return labeling;
	}
}