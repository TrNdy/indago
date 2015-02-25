/**
 *
 */
package com.indago.examples;

import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.imagej.ops.Op;
import net.imagej.ops.OpRef;
import net.imagej.ops.OpService;
import net.imagej.ops.features.DefaultAutoResolvingFeatureSet;
import net.imagej.ops.features.OpResolverService;
import net.imagej.ops.features.firstorder.FirstOrderFeatures.MeanFeature;
import net.imagej.ops.features.firstorder.FirstOrderFeatures.SumFeature;
import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.roi.Regions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.real.DoubleType;

import org.scijava.Context;

import com.indago.benchmarks.RandomCostBenchmarks.Parameters;
import com.indago.segment.LabelingBuilder;
import com.indago.segment.LabelingSegment;
import com.indago.segment.RandomForestFactory;
import com.indago.segment.filteredcomponents.FilteredComponentTree;
import com.indago.segment.filteredcomponents.FilteredComponentTree.Filter;
import com.indago.segment.filteredcomponents.FilteredComponentTree.MaxGrowthPerStep;
import com.indago.weka.ArffWriter;
import com.indago.weka.ArffWriterFactory;

/**
 * @author jug
 */
public class FeatureExampleOnRealSegments {

	public static void main(final String[] args) {

		try {
			final List< String > filenamesImgs = new ArrayList< String >();
			filenamesImgs.add( "src/main/resources/synthetic/0001_z63/image-final_0001-z63.tif" );
			final List< Img< DoubleType >> imgs = loadImages( filenamesImgs, new DoubleType() );

			final List< String > filenamesLbls = new ArrayList< String >();
			filenamesLbls.add( "src/main/resources/synthetic/0001_z63/image-labels_0001-z63.tif" );
			final List< Img< UnsignedIntType >> segments =
					loadImages( filenamesLbls, new UnsignedIntType() );

			genericMain( imgs, segments, new DoubleType(), new UnsignedIntType() );
		} catch ( final ImgIOException iioe ) {
			iioe.printStackTrace();
		}
	}

	/**
	 * @return
	 * @throws ImgIOException
	 */
	public static < T extends RealType< T > & NativeType< T > > List< Img< T >>
			loadImages( final List< String > filenames, final T type ) throws ImgIOException {
		final List< Img< T >> imgs = new ArrayList< Img< T >>();

		for ( final String filename : filenames ) {
			final Img< T > img =
					new ImgOpener().openImg( filename, new ArrayImgFactory< T >(), type );
			imgs.add( img );
		}
		return imgs;
	}

	/**
	 * @param imgs
	 * @param labelings
	 * @param imgtype
	 * @param labeltype
	 */
	public static < T extends RealType< T > & NativeType< T >, L extends IntegerType< L > & NativeType< L > > void genericMain( final List< Img< T >> imgs, final List< Img< L >> labelings, final T imgtype, final L labeltype ) {

		// Check matching length of images and labelings
		if ( imgs.size() != labelings.size() ) { throw new IllegalArgumentException( "Given list of images and labellings must match in length!" ); }

		// create service & context
		// ------------------------

		final Context c = new Context();
		final OpResolverService ors = c.service(OpResolverService.class);
		final OpService ops = c.service(OpService.class);

		// create our own feature set
		// ------------------------

		final DefaultAutoResolvingFeatureSet< IterableInterval< T >, DoubleType > featureSet =
				new DefaultAutoResolvingFeatureSet< IterableInterval< T >, DoubleType >();
		c.inject( featureSet );

		@SuppressWarnings( "rawtypes" )
		final OpRef< MeanFeature > oprefMean = new OpRef< MeanFeature >( "mean", MeanFeature.class );

		@SuppressWarnings( "rawtypes" )
		final OpRef< SumFeature > oprefSum = new OpRef< SumFeature >( "sum", SumFeature.class );

		featureSet.addOutputOp( oprefMean );
		featureSet.addOutputOp( oprefSum );

		// create an ArffWriter that can later save all computed features for all segments
		final ArffWriter arffWriter = ArffWriterFactory.getArffWriterFor( featureSet );

		// loop over loaded data (images)
		// ------------------------------
		for ( int i = 0; i < imgs.size(); i++ ) {
			final Img< T > img = imgs.get( i );
			final Img< L > labels = labelings.get( i );

			System.out.println( String.format( "Working on given image/labeling pair #%3d:", i + 1 ) );

			final int minComponentSize = 10;
			final int maxComponentSize = 10000;
			final Filter maxGrowthPerStep = new MaxGrowthPerStep( 1 );
			final boolean darkToBright = false;
			final FilteredComponentTree< L > tree = FilteredComponentTree.buildComponentTree( labels, labeltype, minComponentSize, maxComponentSize, maxGrowthPerStep, darkToBright );

			final LabelingBuilder builder = new LabelingBuilder( labels );
			builder.buildLabelingForest( tree );
			final ArrayList< LabelingSegment > segments = builder.getSegments();

			for ( final LabelingSegment segment : segments ) {

				final IterableInterval< T > pixels = Regions.sample( segment.getRegion(), img );

				final Map< OpRef< ? extends Op >, DoubleType > features = featureSet.compute( pixels );

				System.out.println( String.format( "\tSum:\t%10.2f  \tMean:\t%10.2f  \tSize:\t%d", features.get( oprefSum ).get(), features.get( oprefMean ).get(), segment.getRegion().size() ) );
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
