package com.indago.playground;

import io.scif.img.ImgOpener;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.Dimensions;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedIntType;

import com.indago.segment.LabelingBuilder;
import com.indago.segment.LabelingForest;
import com.indago.segment.LabelingPlus;
import com.indago.segment.XmlIoLabelingPlus;
import com.indago.segment.filteredcomponents.FilteredComponentTree;
import com.indago.segment.filteredcomponents.FilteredComponentTree.Filter;
import com.indago.segment.filteredcomponents.FilteredComponentTree.MaxGrowthPerStep;
import com.indago.segment.visualization.ColorStream;
import com.indago.segment.visualization.VisualizeLabeling;

public class SerializeLabelingSegmentsPlayground {

	public static void main( final String[] args ) throws Exception {
		final List< String > filenames = new ArrayList< String >();
		filenames.add( "src/main/resources/forest1.tif" );
		filenames.add( "src/main/resources/forest2.tif" );
		filenames.add( "src/main/resources/forest3.tif" );
		doIt( filenames, new UnsignedIntType() );
	}

	public static < T extends RealType< T > & NativeType< T > > void doIt( final List< String > filenames, final T type ) throws Exception {
		final int minComponentSize = 10;
		final int maxComponentSize = 10000 - 1;
		final Filter maxGrowthPerStep = new MaxGrowthPerStep( 1 );
		final boolean darkToBright = false;

		final List< Img< T > > imgs = new ArrayList<>();
		for ( final String filename : filenames ) {
			final Img< T > img = new ImgOpener().openImg( filename, new ArrayImgFactory< T >(), type );
			imgs.add( img );
		}
		final Dimensions dims = imgs.get( 0 );

		final List< FilteredComponentTree< T > > fctrees = new ArrayList<>();
		final LabelingBuilder labelingBuilder = new LabelingBuilder( dims );

		for ( final Img< T > img : imgs ) {
			final FilteredComponentTree< T > newtree = FilteredComponentTree.buildComponentTree( img, type, minComponentSize, maxComponentSize, maxGrowthPerStep, darkToBright );
			fctrees.add( newtree );
		}

		final long t0 = System.currentTimeMillis();

//		int i = 1;
		final List< LabelingForest > labelingForests = new ArrayList<>();
		for ( final FilteredComponentTree< T > fctree : fctrees ) {
			labelingForests.add( labelingBuilder.buildLabelingForest( fctree ) );
		}

		final Img< ARGBType > labels = ArrayImgs.argbs( dims.dimension( 0 ), dims.dimension( 1 ) );
		VisualizeLabeling.colorLabels( labelingBuilder.getLabeling(), ColorStream.iterator(), labels );
		ImageJFunctions.show( labels, "Labels" );

		new XmlIoLabelingPlus().save( labelingBuilder, "/Users/pietzsch/Desktop/labelingbuilder.xml" );

		final LabelingPlus labelingPlusLoaded =
				new XmlIoLabelingPlus().load( "/Users/pietzsch/Desktop/labelingbuilder.xml" );
		final Img< ARGBType > labelsLoaded = ArrayImgs.argbs( dims.dimension( 0 ), dims.dimension( 1 ) );
		VisualizeLabeling.colorLabels(
				labelingPlusLoaded.getLabeling(),
				ColorStream.iterator(),
				labelsLoaded );
		ImageJFunctions.show( labelsLoaded, "Labels loaded" );

		System.out.println( "done" );
	}
}
