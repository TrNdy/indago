/**
 *
 */
package com.indago.examples.serialization;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

import net.imagej.ops.Op;
import net.imagej.ops.OpRef;
import net.imagej.ops.features.AbstractAutoResolvingFeatureSet;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.Regions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import weka.core.Instances;

import com.indago.segment.LabelingBuilder;
import com.indago.segment.LabelingSegment;
import com.indago.segment.filteredcomponents.FilteredComponentTree;
import com.indago.segment.filteredcomponents.FilteredComponentTree.Filter;
import com.indago.segment.filteredcomponents.FilteredComponentTree.MaxGrowthPerStep;
import com.indago.weka.ArffBuilder;
import com.indago.weka.ArffWriterFactory;


/**
 * Module to collect all the feature data later used to train or feed into a
 * classifier other (most likely) weka thing.
 *
 * Input:
 * ------
 * (i) image data
 * (ii) labeling of true instances
 * (iii) labeling of false instances
 * (iv) specification of a feature-set to be used
 *
 * Output:
 * -------
 * (i) ARFF data collection (can be stored in file) fit for further processing
 * in weka algorithms.
 *
 * Part of: IIa
 *
 * @author jug
 */
public class WekaDataInstanceAccumulator< T extends RealType< T > & NativeType< T >, L extends IntegerType< L > & NativeType< L > > {

	private final AbstractAutoResolvingFeatureSet< IterableInterval< T >, DoubleType > featureSet;
	private final ArffBuilder arffData;

	public WekaDataInstanceAccumulator(
			final AbstractAutoResolvingFeatureSet< IterableInterval< T >, DoubleType > featureSet ) {

		this.featureSet = featureSet;
		arffData = ArffWriterFactory.getArffBuilderFor( featureSet );
	}

	public void addData(
			final RandomAccessibleInterval< T > img,
			final RandomAccessibleInterval< L > labeling,
			final L labeltype,
			final String classLabel ) {

		computeFeaturesOnAllSegments(
				img,
				labeling,
				labeltype,
				classLabel );
	}

	/**
	 * Saves collected training data to an ARFF file.
	 *
	 * @param filename
	 *            if does not end in '.arff' this suffix will be added.
	 * @return An weka <code>Instances</code> object contianing all the
	 *         accumulated training data.
	 */
	public Instances saveArff( String filename ) {
		if ( !filename.toLowerCase().endsWith( ".arff" ) ) {
			filename += ".arff";
		}
		try {
			arffData.write( filename );
		} catch ( final FileNotFoundException e ) {
			System.err.println( "Cannot write into specified location: " + filename );
			e.printStackTrace();
		}
		return getDataInstances();
	}

	/**
	 * @return An weka <code>Instances</code> object contianing all the
	 *         accumulated training data.
	 */
	public Instances getDataInstances() {
		return arffData.getData();
	}

	/**
	 * Private helper that computes all features for all segments given in
	 * <code>sumImg</code> and adds them to the stored data collection.
	 *
	 * @param img
	 *            raw image data.
	 * @param sumImg
	 *            note: this will change, currently it is a sum image as known
	 *            from para-maxflow.
	 * @param labeltype
	 *            instance of the labeling type <code>L</code>.
	 * @param classIdentifier
	 *            One of <code>ArffBuilder.POSITIVE_INSTANCE</code>,
	 *            <code>ArffBuilder.NEGATIVE_INSTANCE</code>.
	 */
	private void computeFeaturesOnAllSegments(
			final RandomAccessibleInterval< T > img,
			final RandomAccessibleInterval< L > sumImg,
			final L labeltype,
			final String classIdentifier ) {

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
		final ArrayList< LabelingSegment > segments = builder.getSegments();

		for ( final LabelingSegment segment : segments ) {

			final IterableInterval< T > pixels = Regions.sample( segment.getRegion(), img );
			final Map< OpRef< ? extends Op >, DoubleType > features = featureSet.compute( pixels );

			arffData.addData( features, classIdentifier );
		}
	}
}
