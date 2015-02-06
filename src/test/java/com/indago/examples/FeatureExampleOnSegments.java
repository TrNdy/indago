/**
 *
 */
package com.indago.examples;

import java.util.ArrayList;
import java.util.Map;

import net.imagej.ops.Op;
import net.imagej.ops.OpRef;
import net.imagej.ops.OpService;
import net.imagej.ops.features.AutoResolvingFeatureSet;
import net.imagej.ops.features.OpResolverService;
import net.imagej.ops.features.firstorder.FirstOrderFeatures.MeanFeature;
import net.imagej.ops.features.firstorder.FirstOrderFeatures.SumFeature;
import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.roi.Regions;
import net.imglib2.roi.labeling.LabelRegion;
import net.imglib2.roi.labeling.LabelRegions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.real.DoubleType;

import org.scijava.Context;

import com.indago.benchmarks.RandomCostBenchmarks.Parameters;
import com.indago.segment.LabelingBuilder;
import com.indago.segment.RandomForestFactory;
import com.indago.segment.SegmentLabel;
import com.indago.segment.filteredcomponents.FilteredComponentTree;
import com.indago.segment.filteredcomponents.FilteredComponentTree.Filter;
import com.indago.segment.filteredcomponents.FilteredComponentTree.MaxGrowthPerStep;

/**
 * @author jug
 */
public class FeatureExampleOnSegments {

	private static int baseSeed = 4711;
	private static int numImgsPerParameterSet = 10;

	public static void main(final String[] args) {
		genericMain( args, new UnsignedIntType(), new DoubleType() );
	}

	public static < S extends IntegerType< S > & NativeType< S >, T extends RealType< T > & NativeType< T > > void genericMain( final String[] args, final S sumtype, final T type ) {

		final ArrayList< Parameters > parameterSets = new ArrayList< Parameters >();
		parameterSets.add( new Parameters( 256, 256, 1, 20.0, 0.5, 6.0, 1.0, 0.0, 0, 25 ) ); // first one we used
//		parameterSets.add( new Parameters( 256, 256, 25, 20.0, 0.5, 6.0, 1.0, 0.0, 0, 25 ) ); // denser one

		// create service & context
		// ------------------------

		final Context c = new Context();
		final OpResolverService ors = c.service(OpResolverService.class);
		final OpService ops = c.service(OpService.class);

		// create our own feature set
		// ------------------------

		final AutoResolvingFeatureSet< IterableInterval< T >, DoubleType > featureSet = new AutoResolvingFeatureSet< IterableInterval< T >, DoubleType >();
		c.inject( featureSet );

		@SuppressWarnings( "rawtypes" )
		final OpRef< MeanFeature > oprefMean = new OpRef< MeanFeature >( MeanFeature.class );

		@SuppressWarnings( "rawtypes" )
		final OpRef< SumFeature > oprefSum = new OpRef< SumFeature >( SumFeature.class );

		featureSet.addOutputOp( oprefMean );
		featureSet.addOutputOp( oprefSum );

		// loop over parameter sets, build images and segments + throw features at it
		// --------------------------------------------------------------------------
		for ( int i = 0; i < parameterSets.size(); i++ ) {
			System.out.print( String.format( "Generating random synthetic dataset %d of %d... ", i + 1, parameterSets.size() ) );
			final ArrayList< Img< S > > sumimgs = ( ArrayList ) generateRandomSyntheticImgs( numImgsPerParameterSet, baseSeed + i, parameterSets.get( i ) );
			System.out.println( "done!" );

			int j = 0;
			for ( final Img< S > sumimg : sumimgs ) {

				final Img< T > img = new ArrayImgFactory< T >().create( sumimg, type );
				for ( final T t : img )
					t.setReal( 1.0 );

				System.out.println( String.format( "Working on image %3d of parameter set %2d:", j + 1, i + 1 ) );
				// get via ref. OpRef is required as you may want to add the same Op
				// several times, but with different parameters (e.g. percentile)
//				final MeanFeature< DoubleType > opMean = ( MeanFeature< DoubleType > ) featureSet.compute( img ).get( oprefMean );
//				System.out.println( String.format( "\tMean:\t%10.2f", opMean.getOutput().get() ) );
//				final SumFeature< DoubleType > opSum = ( SumFeature< DoubleType > ) featureSet.compute( img ).get( oprefSum );
//				System.out.println( String.format( "\tSum:\t%10.2f", opSum.getOutput().get() ) );

				final int minComponentSize = 10;
				final int maxComponentSize = 10000;
				final Filter maxGrowthPerStep = new MaxGrowthPerStep( 1 );
				final boolean darkToBright = false;
				final FilteredComponentTree< S > tree = FilteredComponentTree.buildComponentTree( sumimg, sumtype, minComponentSize, maxComponentSize, maxGrowthPerStep, darkToBright );
				final LabelingBuilder builder = new LabelingBuilder( sumimg );
				builder.buildLabelingForest( tree );
				final LabelRegions< SegmentLabel > regions = new LabelRegions< SegmentLabel >( builder.getLabeling() );
				for ( final LabelRegion< SegmentLabel > region : regions ) {
					final IterableInterval< T > segment = Regions.sample( region, img );
					final Map< OpRef< ? extends Op >, Op > features = featureSet.compute( segment );
					final SumFeature< DoubleType > opSum = ( SumFeature< DoubleType > ) features.get( oprefSum );
					final MeanFeature< DoubleType > opMean = ( MeanFeature< DoubleType > ) features.get( oprefMean );
					System.out.println( String.format( "\tSum:\t%10.2f  \tMean:\t%10.2f  \tSize:\t%d", opSum.getOutput().get(), opMean.getOutput().get(), region.size() ) );
				}

				j++;
			}
		}
	}

	public static ArrayList< Img< UnsignedIntType >> generateRandomSyntheticImgs( final int numImgs, final int seed, final Parameters p ) {

		final ArrayList< Img< UnsignedIntType >> imgs = new ArrayList< Img< UnsignedIntType >>();
		for ( int f = 0; f < numImgs; f++ ) {
			imgs.add( RandomForestFactory.getForestImg( p.width, p.height, p.numSeedPixels, p.maxRadius, p.minDeltaR, p.maxDeltaR, p.meanDeltaR, p.sdDeltaR, p.minIntensity, p.maxIntensity, seed + f ) );
		}

		return imgs;
	}
}
