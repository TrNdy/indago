package com.indago.segment;

import ij.ImageJ;
import io.scif.img.ImgOpener;

import java.util.Collection;
import java.util.HashMap;

import net.imglib2.Dimensions;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import com.indago.segment.filteredcomponents.FilteredComponentTree;
import com.indago.segment.visualization.ColorStream;
import com.indago.segment.visualization.VisualizeForest;
import com.indago.segment.visualization.VisualizeLabeling;

public class PlayGround
{
	public static void main( final String[] args ) throws Exception
	{
		doIt( "src/main/resources/components.tif", new UnsignedByteType() );
//		doIt( "/Users/pietzsch/Downloads/demo_sumImg.tif", new UnsignedIntType() );
	}

	public static < T extends RealType< T > & NativeType< T > > void doIt( final String filename, final T type ) throws Exception
	{
		final Img< T > img = new ImgOpener().openImg( filename, new ArrayImgFactory< T >(), type );
		final Dimensions dims = img;

		final int minComponentSize = 10;
		final int maxComponentSize = 10000;
		final int maxGrowthPerStep = 1;
		final boolean darkToBright = false;
		final FilteredComponentTree< T > tree = FilteredComponentTree.buildComponentTree(
				img, type, minComponentSize, maxComponentSize, maxGrowthPerStep, darkToBright );

		final LabelingForest labelingForest = LabelingForest.fromForest( tree, dims );

		final SegmentForest segmentForest = SegmentForest.fromLabelingForest( labelingForest );

		new ShowConflicts( segmentForest );

		final Img< ARGBType > components = ArrayImgs.argbs( img.dimension( 0 ), img.dimension( 1 ) );
		VisualizeForest.colorLevels( tree, ColorStream.iterator(), components );

		final Img< ARGBType > labels = ArrayImgs.argbs( dims.dimension( 0 ), dims.dimension( 1 ) );
		VisualizeLabeling.colorLabels( labelingForest.getLabeling(), ColorStream.iterator(), labels );

		final Img< ARGBType > segments = ArrayImgs.argbs( dims.dimension( 0 ), dims.dimension( 1 ) );
		VisualizeForest.colorLevels( segmentForest, ColorStream.iterator(), segments );

		new ImageJ();
		ImageJFunctions.show( img, "Input" );
		ImageJFunctions.show( components, "FilteredComponentTree" );
		ImageJFunctions.show( labels, "LabelingForest" );
		ImageJFunctions.show( segments, "SegmentForest" );
	}

	static class ShowConflicts
	{
		private final HashMap< Segment, Integer > segmentToId = new HashMap< Segment, Integer >();

		private int idGenerator = 0;

		public ShowConflicts( final SegmentForest segmentForest )
		{
			for ( final Segment segment : segmentForest.roots() )
				printSegment( "", segment );

			System.out.println();
			final Collection< ? extends Collection< Segment > > cliques = segmentForest.getConflictGraphCliques();
			for ( final Collection< Segment > clique : cliques )
			{
				System.out.print( "( " );
				for ( final Segment segment : clique )
					System.out.print( segmentToId.get( segment ) + " " );
				System.out.println( ")" );
			}
		}

		private void printSegment( final String prefix, final Segment segment )
		{
			Integer id = segmentToId.get( segment );
			if ( id == null )
			{
				id = new Integer( idGenerator++ );
				segmentToId.put( segment, id );
			}
			System.out.println( prefix + id );
			for ( final Segment c : segment.getChildren() )
				printSegment( prefix + "  ", c );
		}

	}
}
