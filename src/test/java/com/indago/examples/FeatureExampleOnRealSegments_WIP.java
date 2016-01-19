/**
 *
 */
package com.indago.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.scijava.Context;

import com.indago.benchmarks.RandomCostBenchmarks.Parameters;
import com.indago.data.segmentation.LabelData;
import com.indago.data.segmentation.LabelingBuilder;
import com.indago.data.segmentation.LabelingSegment;
import com.indago.data.segmentation.RandomForestFactory;
import com.indago.data.segmentation.RandomForestSegmentCosts;
import com.indago.data.segmentation.features.FeatureSet;
import com.indago.data.segmentation.filteredcomponents.FilteredComponentTree;
import com.indago.data.segmentation.filteredcomponents.FilteredComponentTree.Filter;
import com.indago.data.segmentation.filteredcomponents.FilteredComponentTree.MaxGrowthPerStep;
import com.indago.data.segmentation.ui.ARGBCompositeAlphaBlender;
import com.indago.data.segmentation.ui.AlphaMixedSegmentLabelSetColor;
import com.indago.data.segmentation.ui.SegmentLabelColor;
import com.indago.data.segmentation.ui.SegmentLabelSetARGBConverter;
import com.indago.examples.serialization.WekaDataInstanceAccumulator;
import com.indago.fg.Assignment;
import com.indago.fg.AssignmentMapper;
import com.indago.fg.FactorGraphFactory;
import com.indago.fg.FactorGraphFactory.MappedFactorGraph;
import com.indago.fg.UnaryCostConstraintGraph;
import com.indago.fg.Variable;
import com.indago.ilp.SolveGurobi;
import com.indago.models.IndicatorVar;
import com.indago.models.ModelGraphFactory;
import com.indago.models.SegmentationModel;
import com.indago.models.segments.SegmentVar;
import com.indago.weka.ArffBuilder;

import gurobi.GRBException;
import ij.ImageJ;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import net.imagej.ops.OpService;
import net.imagej.ops.Ops.Stats.Mean;
import net.imagej.ops.Ops.Stats.Sum;
import net.imagej.ops.Ops.Stats.Variance;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.converter.TypeIdentity;
import net.imglib2.display.RealARGBColorConverter;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.roi.Regions;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelingType;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.ui.viewer.InteractiveViewer2D;
import net.imglib2.util.Pair;
import net.imglib2.view.StackView.StackAccessMode;
import net.imglib2.view.Views;
import net.imglib2.view.composite.NumericComposite;
import weka.classifiers.trees.RandomForest;

/**
 * @author jug
 */
public class FeatureExampleOnRealSegments_WIP {

	private static String pathprefix = "src/main/resources/synthetic/0001_z63";

	public static void main(final String[] args) {
		new ImageJ();
		try {
			final List< String > filenamesImgs = new ArrayList< String >();
			filenamesImgs.add( pathprefix + "/image-final_0001-z63.tif" );
			final List< Img< DoubleType >> imgs = loadImages( filenamesImgs, new DoubleType() );

			final List< String > filenamesGroundTruth = new ArrayList< String >();
			filenamesGroundTruth.add( pathprefix + "/image-labels_0001-z63.tif" );
			final List< Img< UnsignedIntType >> gt =
					loadImages( filenamesGroundTruth, new UnsignedIntType() );

			final List< String > filenamesLbls = new ArrayList< String >();
			filenamesLbls.add( pathprefix + "/ParamaxFlowSumImg.tif" );
			final List< Img< UnsignedIntType >> segments =
					loadImages( filenamesLbls, new UnsignedIntType() );

			genericMain( imgs, segments, gt, new DoubleType(), new UnsignedIntType() );
		} catch ( final ImgIOException | GRBException e ) {
			e.printStackTrace();
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
	 *            raw image data
	 * @param labelings
	 *            segment hypotheses
	 * @param gt
	 *            ground truth labels
	 * @param imgtype
	 * @param labeltype
	 * @throws GRBException
	 */
	public static
			< T extends RealType< T > & NativeType< T >, L extends IntegerType< L > & NativeType< L > >
			void genericMain(
					final List< Img< T >> imgs,
					final List< Img< L >> labelings,
					final List< Img< L >> gt,
					final T imgtype,
					final L labeltype ) throws GRBException {

		// Check matching length of images and labelings
		if ( imgs.size() != labelings.size() ) { throw new IllegalArgumentException( "Given list of images and labellings must match in length!" ); }

		// create service & context
		// ------------------------
		final Context c = new Context();
		final OpService ops = c.service(OpService.class);

		// create our own feature set
		// ------------------------
		@SuppressWarnings( "unchecked" )
		final FeatureSet< IterableInterval< T >, DoubleType > ourFeatureSet =
				( FeatureSet< IterableInterval< T >, DoubleType > ) ( Object ) new FeatureSet< >(
						ops,
						new DoubleType(),
						IterableInterval.class,
						Mean.class,
						Sum.class,
						Variance.class );

		// create training- and testset
		final WekaDataInstanceAccumulator< T, L > trainingData =
				new WekaDataInstanceAccumulator< T, L >( ourFeatureSet );
		final WekaDataInstanceAccumulator< T, L > testData =
				new WekaDataInstanceAccumulator< T, L >( ourFeatureSet );

		// loop over loaded data (images)
		// ------------------------------
		for ( int i = 0; i < imgs.size(); i++ ) {
			System.out.print( String.format(
					"Working on given image/labeling pair #%3d...",
					i + 1 ) );

			trainingData.addData(
					imgs.get( i ),
					gt.get( i ),
					labeltype,
					ArffBuilder.POSITIVE_INSTANCE );
			trainingData.addData(
					imgs.get( i ),
					Views.interval(
							Views.translate( Views.extendZero( gt.get( i ) ), 10, 10 ),
							gt.get( i ) ),
					labeltype,
					ArffBuilder.NEGATIVE_INSTANCE );

			testData.addData(
					imgs.get( i ),
					labelings.get( i ),
					labeltype,
					ArffBuilder.UNKNOWN_INSTANCE );
		}

		trainingData.saveArff( pathprefix + "/FeatureExampleOnRealSegments_traindata.arff" );
		testData.saveArff( pathprefix + "/FeatureExampleOnRealSegments_testdata.arff" );

		System.out.println( "...done!" );

		// -----------------------------------------------------------------------------------------------

		System.out.print( "Training random forest classifier..." );
		final RandomForest tree = new RandomForest();
		try {
			tree.buildClassifier( trainingData.getDataInstances() );
		} catch ( final Exception e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
		System.out.println( "...done!" );

		// -----------------------------------------------------------------------------------------------

		System.out.println( "Applying trained random forest classifier to each test-image..." );
		for ( int i = 0; i < imgs.size(); i++ ) {
			final Img< T > img = imgs.get( i );
			final Img< L > lblImg = labelings.get( i );

			final RandomForestSegmentCosts< L, T > costs =
					new RandomForestSegmentCosts< L, T >( lblImg, img, tree, ourFeatureSet, labeltype );

			System.out.print( "\tOptimum search for Image " + i + ": Finding optimum..." );

			final Pair< SegmentationModel, AssignmentMapper< SegmentVar, LabelingSegment > > problemAndMapper =
					ModelGraphFactory.createSegmentationProblem(
							costs.getSegments(),
							costs.getConflictGraph(),
							costs );
			final SegmentationModel problem = problemAndMapper.getA();
			final AssignmentMapper< SegmentVar, LabelingSegment > problemMapper = problemAndMapper.getB();


			final MappedFactorGraph fgAndMapper =
					FactorGraphFactory.createFactorGraph( problem );
			final UnaryCostConstraintGraph fg = fgAndMapper.getFg();
			final AssignmentMapper< Variable, IndicatorVar > fgMapper = fgAndMapper.getMapper();

			final Assignment< Variable > fgSolution = SolveGurobi.staticSolve( fg );
			final Assignment< IndicatorVar > problemSolution = fgMapper.map( fgSolution );
			final Assignment< LabelingSegment > assignment = problemMapper.map( problemSolution );

//			final FactorGraphPlus< LabelingSegment > fgplus = FactorGraphFactory.createFromConflictGraph(
//			final FactorGraph fg = fgplus.getFactorGraph();

			final ImgLabeling< LabelData, IntType > labeling = costs.getLabeling();

			final AssignmentLabelColor labelColor = new AssignmentLabelColor( assignment);
			final AlphaMixedSegmentLabelSetColor labelSetColor = new AlphaMixedSegmentLabelSetColor( labelColor );
			final RealARGBColorConverter< T > imageConverter = new RealARGBColorConverter.Imp0< T >( 0, 255 );
			imageConverter.setColor( new ARGBType( 0xffffffff ) );
			final SegmentLabelSetARGBConverter labelingConverter = new SegmentLabelSetARGBConverter( labelSetColor, costs.getLabeling().getMapping() );

			final RandomAccessibleInterval< ARGBType > argbImage = Converters.convert( ( RandomAccessibleInterval< T > ) img, imageConverter, new ARGBType() );
			final RandomAccessibleInterval< ARGBType > argbLabeling = Converters.convert( ( RandomAccessibleInterval< LabelingType< LabelData > > ) labeling, labelingConverter, new ARGBType() );

			final RandomAccessibleInterval< ARGBType > stack = Views.stack( StackAccessMode.MOVE_ALL_SLICE_ACCESSES, argbImage, argbLabeling );
			final RandomAccessibleInterval< NumericComposite< ARGBType > > composite = Views.collapseNumeric( stack );

			final RandomAccessibleInterval< ARGBType > blended = Converters.convert( composite, new ARGBCompositeAlphaBlender(), new ARGBType() );
			ImageJFunctions.show( blended );
			new InteractiveViewer2D< ARGBType >( 800, 600, Views.extendZero( blended ), new TypeIdentity< ARGBType >() );

			System.out.println( "\t...done!" );
		}
		System.out.println( "...done!" );

		// -----------------------------------------------------------------------------------------------

	}

	public static class AssignmentLabelColor implements SegmentLabelColor
	{
		private final Assignment< LabelingSegment > assignment;

		Random rand = new Random();

		public AssignmentLabelColor( final Assignment< LabelingSegment > assignment )
		{
			this.assignment = assignment;
		}

		@Override
		public int getSegmentLabelColor( final LabelData label ) {
			final boolean isSelected = assignment.getAssignment( label.getSegment() ) > 0;
//			final int randColor = 0x44000000 | ( rand.nextInt( 0x00ff ) << 16 ) | rand.nextInt( 0xffff );
			return isSelected ? 0x88ff00ff : 0;
		}
	}


	/**
	 * @param img
	 * @param sumImg
	 * @param labeltype
	 * @param featureSet
	 * @param arffBuilder
	 * @param classIdentifier
	 */
	public static
			< L extends IntegerType< L > & NativeType< L >, T extends RealType< T > & NativeType< T >>
			void
			computeFeaturesOnAllSegments(
					final RandomAccessibleInterval< T > img,
					final RandomAccessibleInterval< L > sumImg,
					final L labeltype,
					final FeatureSet< IterableInterval< T >, DoubleType > featureSet,
					final ArffBuilder arffBuilder,
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
			featureSet.compute( pixels );
			arffBuilder.addData( featureSet.getNamedOutputs(), classIdentifier );
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
