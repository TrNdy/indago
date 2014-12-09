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

import com.indago.segment.filteredcomponents.FilteredComponent;
import com.indago.segment.filteredcomponents.FilteredComponentTree;
import com.indago.segment.visualization.ColorStream;
import com.indago.segment.visualization.VisualizeForest;
import com.indago.segment.visualization.VisualizeLabelingMod;

public class PlayGround3 {

	public static void main( final String[] args ) throws Exception {
//		doIt( "src/main/resources/components.tif", new UnsignedByteType() );
		doIt( "/Users/pietzsch/Downloads/SumImgs/mRuby-PCNA_800_BGsubtracted_t20.tif", new IntType() );
	}

	public static < T extends Type< T > > void benchmark( final Dimensions dims, final FilteredComponentTree< T > tree )
	{
		BenchmarkHelper.benchmarkAndPrint( 20, true, new Runnable() {
			@Override
			public void run() {
				final LabelingForestBuilder< FilteredComponent< T > > builder = new LabelingForestBuilder< FilteredComponent< T > >( tree, dims );
				builder.getLabelingForest();
			}
		} );
	}

	public static < T extends Type< T > > void benchmarkMod( final Dimensions dims, final FilteredComponentTree< T > tree )
	{
		BenchmarkHelper.benchmarkAndPrint( 50, true, new Runnable() {
			@Override
			public void run() {
				final LabelingForestBuilderMod< FilteredComponent< T > > builder = new LabelingForestBuilderMod< FilteredComponent< T > >( tree, dims );
				builder.getLabelingForest();
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
//		benchmark( dims, tree );
		benchmarkMod( dims, tree );

//		final LabelingForest labelingForest = LabelingForest.fromForest( tree, dims );
		final LabelingForestMod labelingForest = LabelingForestMod.fromForest( tree, dims );

		final Img< ARGBType > components = ArrayImgs.argbs( img.dimension( 0 ), img.dimension( 1 ) );
		VisualizeForest.colorLevels( tree, ColorStream.iterator(), components );

		final Img< ARGBType > labels = ArrayImgs.argbs( dims.dimension( 0 ), dims.dimension( 1 ) );
//		VisualizeLabeling.colorLabels( labelingForest.getLabeling(), ColorStream.iterator(), labels );
		VisualizeLabelingMod.colorLabels( labelingForest.getLabeling(), ColorStream.iterator(), labels );

		ImageJFunctions.show( img, "Input" );
		ImageJFunctions.show( components, "FilteredComponentTree" );
		ImageJFunctions.show( labels, "LabelingForest" );
	}
}

/*
run 0: 211 ms
run 1: 108 ms
run 2: 89 ms
run 3: 78 ms
run 4: 98 ms
run 5: 75 ms
run 6: 75 ms
run 7: 86 ms
run 8: 73 ms
run 9: 76 ms
run 10: 76 ms
run 11: 83 ms
run 12: 90 ms
run 13: 91 ms
run 14: 83 ms
run 15: 73 ms
run 16: 72 ms
run 17: 71 ms
run 18: 72 ms
run 19: 72 ms
run 20: 79 ms
run 21: 71 ms
run 22: 74 ms
run 23: 74 ms
run 24: 75 ms
run 25: 76 ms
run 26: 79 ms
run 27: 84 ms
run 28: 88 ms
run 29: 90 ms
run 30: 89 ms
run 31: 91 ms
run 32: 92 ms
run 33: 74 ms
run 34: 74 ms
run 35: 78 ms
run 36: 77 ms
run 37: 75 ms
run 38: 74 ms
run 39: 74 ms
run 40: 75 ms
run 41: 74 ms
run 42: 74 ms
run 43: 75 ms
run 44: 81 ms
run 45: 71 ms
run 46: 75 ms
run 47: 74 ms
run 48: 76 ms
run 49: 75 ms

median: 75 ms
best: 71 ms
*/