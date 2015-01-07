package com.indago.segment;

import io.scif.img.ImgOpener;
import net.imglib2.Dimensions;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.BenchmarkHelper;

import com.indago.segment.filteredcomponents.FilteredComponentTree;
import com.indago.segment.visualization.ColorStream;
import com.indago.segment.visualization.VisualizeForest;
import com.indago.segment.visualization.VisualizeLabeling;

public class PlayGround3 {

	public static void main( final String[] args ) throws Exception {
//		doIt( "src/main/resources/components.tif", new UnsignedByteType() );
		doIt( "/Users/pietzsch/Downloads/SumImgs/mRuby-PCNA_800_BGsubtracted_t20.tif", new IntType() );
	}

	public static < T extends Type< T > > void benchmark( final Dimensions dims, final FilteredComponentTree< T > tree ) {
		BenchmarkHelper.benchmarkAndPrint( 50, true, new Runnable() {

			@Override
			public void run() {
				final LabelingBuilder builder = new LabelingBuilder( dims );
				builder.buildLabelingForest( tree );
			}
		} );
	}

	public static < T extends RealType< T > & NativeType< T > > void doIt( final String filename, final T type ) throws Exception {
		final Img< T > img = new ImgOpener().openImg( filename, new ArrayImgFactory< T >(), type );
		final Dimensions dims = img;

		final int minComponentSize = 10;
		final int maxComponentSize = 10000;
		final int maxGrowthPerStep = 1;
		final boolean darkToBright = false;
		final FilteredComponentTree< T > tree = FilteredComponentTree.buildComponentTree( img, type, minComponentSize, maxComponentSize, maxGrowthPerStep, darkToBright );

		System.out.println( "go" );
		benchmark( dims, tree );

		final LabelingBuilder builder = new LabelingBuilder( dims );
		final LabelingForest< ? > labelingForest = builder.buildLabelingForest( tree );

		final Img< ARGBType > components = ArrayImgs.argbs( img.dimension( 0 ), img.dimension( 1 ) );
		VisualizeForest.colorLevels( tree, ColorStream.iterator(), components );

		final Img< ARGBType > labels = ArrayImgs.argbs( dims.dimension( 0 ), dims.dimension( 1 ) );
		VisualizeLabeling.colorLabels( builder.getLabeling(), ColorStream.iterator(), labels );

		ImageJFunctions.show( img, "Input" );
		ImageJFunctions.show( components, "FilteredComponentTree" );
		ImageJFunctions.show( labels, "LabelingForest" );
	}
}
