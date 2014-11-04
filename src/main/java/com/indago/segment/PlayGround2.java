package com.indago.segment;

import ij.ImageJ;
import io.scif.img.ImgOpener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.imglib2.Dimensions;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedIntType;

import com.indago.segment.filteredcomponents.FilteredComponentTree;

public class PlayGround2 {

	public static void main( final String[] args ) throws Exception {
		final List< String > filenames = new ArrayList< String >();
		filenames.add( "src/main/resources/forest1.tif" );
		filenames.add( "src/main/resources/forest2.tif" );
		filenames.add( "src/main/resources/forest3.tif" );
		doIt( filenames, new UnsignedIntType() );
	}

	public static < T extends RealType< T > & NativeType< T > > void doIt( final List< String > filenames, final T type ) throws Exception {
		final int minComponentSize = 10;
		final int maxComponentSize = 10000;
		final int maxGrowthPerStep = 1;
		final boolean darkToBright = false;

		final List< Img< T > > imgs = new ArrayList< Img< T > >();
		final List< FilteredComponentTree< T > > fctrees = new ArrayList< FilteredComponentTree< T > >();
		final List< LabelingForest > labelingForests = new ArrayList< LabelingForest >();
		final List< SegmentForest > segmentForests = new ArrayList< SegmentForest >();

		for ( final String filename : filenames ) {
			final Img< T > img = new ImgOpener().openImg( filename, new ArrayImgFactory< T >(), type );
			final Dimensions dims = img;
			imgs.add( img );

			final FilteredComponentTree< T > newtree = FilteredComponentTree.buildComponentTree( img, type, minComponentSize, maxComponentSize, maxGrowthPerStep, darkToBright );
			fctrees.add( newtree );

			final LabelingForest newLF = LabelingForest.fromForest( newtree, dims );
			labelingForests.add( newLF );

			final SegmentForest newSF = SegmentForest.fromLabelingForest( newLF );
			segmentForests.add( newSF );
		}

		final SegmentMultiForest segmentMultiForest = new SegmentMultiForest( segmentForests );

		System.out.println( "\n>>>> Pairwise Constraints <<<<" );
		new ShowConflicts( segmentMultiForest, false );
		System.out.println( "\n>>>> Compact Clique Constraints <<<<" );
		new ShowConflicts( segmentMultiForest, true );

		new ImageJ();
		for ( final Img< T > img : imgs ) {
			ImageJFunctions.show( img, "Input" );
		}
	}

	static class ShowConflicts {

		private final HashMap< Segment, Integer > segmentToId = new HashMap< Segment, Integer >();

		private int idGenerator = 0;

		public ShowConflicts( final SegmentMultiForest segmentMultiForest, final boolean doCompactConstraints ) {
			for ( final Segment segment : segmentMultiForest.roots() )
				printSegment( "", segment );

			System.out.println();
			final Collection< ? extends Collection< Segment > > cliques;
			if ( doCompactConstraints ) {
				cliques = segmentMultiForest.getConflictGraphCliques();
			} else {
				cliques = segmentMultiForest.getConflictGraphEdges();
			}
			for ( final Collection< Segment > clique : cliques ) {
				System.out.print( "( " );
				for ( final Segment segment : clique )
					System.out.print( String.format( "%2d ", segmentToId.get( segment ) ) );
				System.out.println( ")" );
			}
		}

		private void printSegment( final String prefix, final Segment segment ) {
			Integer id = segmentToId.get( segment );
			if ( id == null ) {
				id = new Integer( idGenerator++ );
				segmentToId.put( segment, id );
			}
			System.out.println( prefix + id );
			for ( final Segment c : segment.getChildren() )
				printSegment( prefix + "  ", c );
		}

	}
}
