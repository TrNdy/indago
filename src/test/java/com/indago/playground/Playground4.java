package com.indago.playground;

import ij.ImageJ;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;

import com.indago.segment.LabelingBuilder;
import com.indago.segment.LabelingForest;
import com.indago.segment.filteredcomponents.FilteredComponentTree;
import com.indago.segment.filteredcomponents.FilteredComponentTree.Filter;
import com.indago.segment.filteredcomponents.FilteredComponentTree.MaxGrowthPerStepRatio;
import com.indago.segment.groundtruth.FlatForest;
import com.indago.segment.visualization.ColorStream;
import com.indago.segment.visualization.VisualizeForest;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.view.Views;

public class Playground4 {

	public static void main( final String[] args ) throws Exception {
		final String inputFileName = "src/main/resources/synthetic/0001_z63/image-final_0001-z63.tif";
		final UnsignedIntType inputType = new UnsignedIntType();

		final String groundtruthFileName = "src/main/resources/synthetic/0001_z63/image-labels_0001-z63.tif";
		final UnsignedIntType groundtruthType = new UnsignedIntType();

		final int minComponentSize = 100;
		final int maxComponentSize = 100000;
		final Filter maxGrowthPerStepRatio = new MaxGrowthPerStepRatio( 0.1 );
		final boolean darkToBright = false;

		final boolean show = true;

		if ( show ) {
			ImageJ.main( args );
		}

		final Img input = load( inputFileName, inputType );

		if ( show ) {
			ImageJFunctions.show( ( RandomAccessibleInterval ) input );
		}

		final LabelingBuilder builder = new LabelingBuilder( input );

		final FilteredComponentTree tree = buildComponentTree( input, 3.0, minComponentSize, maxComponentSize, maxGrowthPerStepRatio, darkToBright, show );
		final LabelingForest ctForest = builder.buildLabelingForest( tree );



		final Img groundtruth = load( groundtruthFileName, groundtruthType );

		final UnsignedIntType groundtruthBackground = groundtruthType.createVariable();
		groundtruthBackground.setZero();
		final FlatForest groundTruthForest = new FlatForest( groundtruth, groundtruthBackground );
		final LabelingForest gtForest = builder.buildLabelingForest( groundTruthForest );

		if ( show ) {
			final Img< ARGBType > nodes = ArrayImgs.argbs( groundtruth.dimension( 0 ), groundtruth.dimension( 1 ) );
			VisualizeForest.colorNodes( gtForest, ColorStream.iterator(), nodes );
			ImageJFunctions.show( nodes, "ground truth" );
		}

	}

	public static < T extends RealType< T > & NativeType< T > > Img< T > load( final String filename, final T type ) throws ImgIOException {
		final Img< T > img = new ImgOpener().openImgs( filename, new ArrayImgFactory< T >(), type ).get( 0 );
		return img;
	}

	public static < T extends RealType< T > & NativeType< T > > FilteredComponentTree< T > buildComponentTree(
			final Img< T > img,
			final double preSmoothSigma,
			final int minComponentSize,
			final int maxComponentSize,
			final Filter filter,
			final boolean darkToBright,
			final boolean visualize )
	{
		final T type = img.firstElement();
		final Img< T > smoothed = img.factory().create( img, type );
		try {
			Gauss3.gauss( preSmoothSigma, Views.extendMirrorSingle( img ), smoothed );
		} catch ( final IncompatibleTypeException e ) {}

		final FilteredComponentTree< T > tree = FilteredComponentTree.buildComponentTree( smoothed, type, minComponentSize, maxComponentSize, filter, darkToBright );

		if ( visualize ) {
			final Img< ARGBType > levels = ArrayImgs.argbs( img.dimension( 0 ), img.dimension( 1 ) );
			final Img< ARGBType > nodes = ArrayImgs.argbs( img.dimension( 0 ), img.dimension( 1 ) );
			VisualizeForest.colorLevels( tree, ColorStream.iterator(), levels );
			VisualizeForest.colorNodes( tree, ColorStream.iterator(), nodes );

			ImageJFunctions.show( smoothed, "smoothed input (sigma=" + preSmoothSigma + ")" );
			ImageJFunctions.show( levels, "levels (sigma=" + preSmoothSigma + ")"  );
			ImageJFunctions.show( nodes, "nodes (sigma=" + preSmoothSigma + ")" );
		}

		return tree;
	}

}
