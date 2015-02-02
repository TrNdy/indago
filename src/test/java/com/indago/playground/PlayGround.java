package com.indago.playground;

import ij.ImageJ;
import io.scif.img.ImgOpener;
import net.imglib2.Dimensions;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import com.indago.segment.HypothesisPrinter;
import com.indago.segment.LabelingBuilder;
import com.indago.segment.LabelingForest;
import com.indago.segment.filteredcomponents.FilteredComponentTree;
import com.indago.segment.visualization.ColorStream;
import com.indago.segment.visualization.VisualizeForest;
import com.indago.segment.visualization.VisualizeLabeling;

public class PlayGround {

	public static void main( final String[] args ) throws Exception {
		doIt( "src/main/resources/components.tif", new UnsignedByteType() );
//		doIt( "/Users/jug/MPI/ProjectMansfeld/Movie01/SumImgs/mRuby-PCNA_800_BGsubtracted_t20.tif", new UnsignedIntType() );
	}

	public static < T extends RealType< T > & NativeType< T > > void doIt( final String filename, final T type ) throws Exception {
		final Img< T > img = new ImgOpener().openImg( filename, new ArrayImgFactory< T >(), type );
		final Dimensions dims = img;

		final int minComponentSize = 10;
		final int maxComponentSize = 10000;
		final int maxGrowthPerStep = 1;
		final boolean darkToBright = false;
		final FilteredComponentTree< T > tree = FilteredComponentTree.buildComponentTree( img, type, minComponentSize, maxComponentSize, maxGrowthPerStep, darkToBright );

		final LabelingBuilder builder = new LabelingBuilder( img );
		final LabelingForest labelingForest = builder.buildLabelingForest( tree );

		final HypothesisPrinter hp = new HypothesisPrinter();
		hp.assignIds( labelingForest );
		hp.printHypothesisForest( labelingForest );
		System.out.println();
		hp.printConflictGraphCliques( labelingForest );

		final Img< ARGBType > components = ArrayImgs.argbs( img.dimension( 0 ), img.dimension( 1 ) );
		VisualizeForest.colorLevels( tree, ColorStream.iterator(), components );

		final Img< ARGBType > labels = ArrayImgs.argbs( dims.dimension( 0 ), dims.dimension( 1 ) );
		VisualizeLabeling.colorLabels( builder.getLabeling(), ColorStream.iterator(), labels );

		final Img< ARGBType > segments = ArrayImgs.argbs( dims.dimension( 0 ), dims.dimension( 1 ) );
		VisualizeForest.colorLevels( labelingForest, ColorStream.iterator(), segments );

		new ImageJ();
		ImageJFunctions.show( img, "Input" );
		ImageJFunctions.show( components, "FilteredComponentTree" );
		ImageJFunctions.show( labels, "Labeling" );
		ImageJFunctions.show( segments, "LabelingForest" );
	}
}
