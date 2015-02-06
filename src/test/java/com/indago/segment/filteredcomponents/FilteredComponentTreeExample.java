package com.indago.segment.filteredcomponents;

import ij.ImageJ;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.UnsignedIntType;

import com.indago.segment.RandomForestFactory;
import com.indago.segment.visualization.ColorStream;
import com.indago.segment.visualization.VisualizeForest;

public class FilteredComponentTreeExample {

	public static void main( final String[] args ) {

		final int width = 256;
		final int height = 256;
		final int numSeedPixels = 25;
		final double maxRadius = 50.0;
		final double minDeltaR = 0.5;
		final double maxDeltaR = 6.0;
		final double meanDeltaR = 1.0;
		final double sdDeltaR = 0.0;
		final int minIntensity = 0;
		final int maxIntensity = 50;
		final int seed = 123123;

		final Img< UnsignedIntType > sumimg = RandomForestFactory.getForestImg( width, height, numSeedPixels, maxRadius, minDeltaR, maxDeltaR, meanDeltaR, sdDeltaR, minIntensity, maxIntensity, seed );

		testFiltering( sumimg, new UnsignedIntType() );

	}

	public static < S extends IntegerType< S > & NativeType< S > > void testFiltering( final RandomAccessibleInterval< S > sumimg, final S sumtype ) {
		final int minComponentSize = 10;
		final int maxComponentSize = 10000;
		final int maxGrowthPerStep = 1000;
		final boolean darkToBright = false;
		final FilteredComponentTree< S > tree = FilteredComponentTree.buildComponentTree( sumimg, sumtype, minComponentSize, maxComponentSize, maxGrowthPerStep, darkToBright );


		final Img< ARGBType > levels = ArrayImgs.argbs( sumimg.dimension( 0 ), sumimg.dimension( 1 ) );
		final Img< ARGBType > nodes = ArrayImgs.argbs( sumimg.dimension( 0 ), sumimg.dimension( 1 ) );
		VisualizeForest.colorLevels( tree, ColorStream.iterator(), levels );
		VisualizeForest.colorNodes( tree, ColorStream.iterator(), nodes );

		printFilteredComponentForest( tree );

		new ImageJ();
		ImageJFunctions.show( sumimg, "suming" );
		ImageJFunctions.show( levels, "levels" );
		ImageJFunctions.show( nodes, "nodes" );
	}

	public static void printFilteredComponentForest( final FilteredComponentTree< ? > forest ) {
		for ( final FilteredComponent< ? > node : forest.roots() )
			printFilteredComponentTreeNode( "", node );
	}

	private static void printFilteredComponentTreeNode( final String prefix, final FilteredComponent< ? > node ) {
		System.out.println( prefix + "(size:" + node.minSize() + " - " + node.maxSize() + ", value:" + node.minValue() + " - " + node.maxValue() + ")" );
		for ( final FilteredComponent< ? > child : node.getChildren() )
			printFilteredComponentTreeNode( prefix + "  ", child );
	}

}
