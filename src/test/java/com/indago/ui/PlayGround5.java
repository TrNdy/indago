package com.indago.ui;

import static net.imglib2.view.StackView.StackAccessMode.MOVE_ALL_SLICE_ACCESSES;
import ij.ImageJ;
import io.scif.img.IO;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.converter.TypeIdentity;
import net.imglib2.display.RealARGBColorConverter;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.roi.labeling.LabelingType;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.ui.viewer.InteractiveViewer2D;
import net.imglib2.util.BenchmarkHelper;
import net.imglib2.view.Views;
import net.imglib2.view.composite.NumericComposite;

import com.indago.segment.LabelData;
import com.indago.segment.LabelingBuilder;
import com.indago.segment.LabelingForest;
import com.indago.segment.filteredcomponents.FilteredComponentTree;
import com.indago.segment.filteredcomponents.FilteredComponentTree.Filter;
import com.indago.segment.filteredcomponents.FilteredComponentTree.MaxGrowthPerStep;
import com.indago.segment.ui.ARGBCompositeAlphaBlender;
import com.indago.segment.ui.AlphaMixedSegmentLabelSetColor;
import com.indago.segment.ui.SegmentLabelColor;
import com.indago.segment.ui.SegmentLabelSetARGBConverter;

public class PlayGround5 {

	public static void main( final String[] args ) throws Exception {
		ImageJ.main( args );
		final String image = "src/main/resources/image.tif";
		final String segments = "src/main/resources/forest1.tif";
		doIt( image, segments, new UnsignedIntType() );
	}

	public static < T extends RealType< T > & NativeType< T > > void doIt(
			final String imageFn,
			final String segmentsFn,
			final T type ) throws Exception {
		final int minComponentSize = 10;
		final int maxComponentSize = 10000 - 1;
		final Filter maxGrowthPerStep = new MaxGrowthPerStep( 1 );
		final boolean darkToBright = false;

		final ArrayImgFactory< T > factory = new ArrayImgFactory< T >();
		final Img< T > image = IO.openImgs( imageFn, factory, type ).get( 0 );
		final Img< T > segments = IO.openImgs( segmentsFn, factory, type ).get( 0 );

		final LabelingBuilder labelingBuilder = new LabelingBuilder( segments );
		final LabelingForest labelingForest = labelingBuilder.buildLabelingForest( FilteredComponentTree.buildComponentTree( segments, type, minComponentSize, maxComponentSize, maxGrowthPerStep, darkToBright ) );
		final RandomAccessibleInterval< LabelingType< LabelData > > labeling = labelingBuilder.getLabeling();

		final RealARGBColorConverter< T > imageConverter = new RealARGBColorConverter.Imp0< T >( 0, 255 );
		imageConverter.setColor( new ARGBType( 0xffffffff ) );
		final SegmentLabelSetARGBConverter labelingConverter = new SegmentLabelSetARGBConverter( new AlphaMixedSegmentLabelSetColor( new SegmentLabelColor() {
			Random rand = new Random( 1 );
			@Override
			public int getSegmentLabelColor( final LabelData label ) {
				return ( rand.nextInt( 0xffff ) << 16 ) | rand.nextInt( 0xffff );
			}
		} ), labelingBuilder.getLabeling().getMapping() );

		final RandomAccessibleInterval< ARGBType > argbImage = Converters.convert( ( RandomAccessibleInterval< T > ) image, imageConverter, new ARGBType() );
		final RandomAccessibleInterval< ARGBType > argbLabeling = Converters.convert( labeling, labelingConverter, new ARGBType() );

		final RandomAccessibleInterval< ARGBType > stack = Views.stack( MOVE_ALL_SLICE_ACCESSES, argbImage, argbLabeling );
		final RandomAccessibleInterval< NumericComposite< ARGBType > > composite = Views.collapseNumeric( stack );

		final RandomAccessibleInterval< ARGBType > blended = Converters.convert( composite, new ARGBCompositeAlphaBlender(), new ARGBType() );

		final Img< ARGBType > copy = ArrayImgs.argbs( blended.dimension( 0 ), blended.dimension( 0 ) );
		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable() {
			@Override
			public void run() {
				final Cursor< ARGBType > in = Views.flatIterable( blended ).cursor();
				final Cursor< ARGBType > out = copy.cursor();
				for ( int i = 0; i < 100; ++i )
				{
					in.reset();
					out.reset();
					while ( out.hasNext() )
						out.next().set( in.next() );
				}
			}
		} );

//		ImageJFunctions.show( blended );
		ImageJFunctions.show( copy );

//		new InteractiveViewer2D< NumericComposite< ARGBType > >( 800, 600, Views.extendZero( composite ), new ARGBCompositeAlphaBlender() );
		new InteractiveViewer2D< ARGBType >( 100, 100, Views.extendZero( blended ), new TypeIdentity< ARGBType >() );
	}
}
