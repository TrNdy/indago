/**
 *
 */
package com.indago.ui.bdv;

import java.util.List;

import com.indago.util.ImglibUtil;

import bdv.util.Bdv;
import bdv.util.BdvFunctions;
import bdv.util.BdvHandlePanel;
import bdv.util.BdvSource;
import bdv.viewer.DisplayMode;
import bdv.viewer.VisibilityAndGrouping;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * @author jug
 */
public interface BdvOwner {

	public BdvHandlePanel bdvGetHandlePanel();

	public void bdvSetHandlePanel( BdvHandlePanel bdvHandlePanel );

	public List< BdvSource > bdvGetSources();

	public < T extends RealType< T > & NativeType< T > > BdvSource bdvGetSourceFor( final RandomAccessibleInterval< T > img );

	public default < T extends RealType< T > & NativeType< T > > void bdvAdd( final RandomAccessibleInterval< T > img, final String title ) {
		bdvAdd( img, title, true );
	}

	public default < T extends RealType< T > & NativeType< T > > void bdvAdd(
			final RandomAccessibleInterval< T > img,
			final String title,
			final boolean isActive ) {
		final BdvSource source = BdvFunctions.show(
				img,
				title,
				Bdv.options().addTo( bdvGetHandlePanel() ) );
		bdvGetSources().add( source );

		final T min = img.randomAccess().get().copy();
		final T max = min.copy();
		ImglibUtil.computeMinMax( Views.iterable( img ), min, max );
		source.setDisplayRangeBounds( Math.min( min.getRealDouble(), 0 ), max.getRealDouble() );
		source.setDisplayRange( min.getRealDouble(), max.getRealDouble() );
		source.setActive( isActive );
	}

	public default < T extends RealType< T > & NativeType< T > > void bdvAdd(
			final RandomAccessibleInterval< T > img,
			final String title,
			final ARGBType color,
			final boolean isActive ) {
		final BdvSource source = BdvFunctions.show(
				img,
				title,
				Bdv.options().addTo( bdvGetHandlePanel() ) );
		bdvGetSources().add( source );

		final T min = img.randomAccess().get().copy();
		final T max = min.copy();
		ImglibUtil.computeMinMax( Views.iterable( img ), min, max );
		source.setDisplayRangeBounds( Math.min( min.getRealDouble(), 0 ), max.getRealDouble() );
		source.setDisplayRange( min.getRealDouble(), max.getRealDouble() );

		source.setColor( color );
		source.setActive( isActive );
	}

	public default < T extends RealType< T > & NativeType< T > > void bdvAdd(
			final RandomAccessibleInterval< T > img,
			final String title,
			final double minVal,
			final double maxVal,
			final ARGBType color,
			final boolean isActive ) {
		final BdvSource source = BdvFunctions.show(
				img,
				title,
				Bdv.options().addTo( bdvGetHandlePanel() ) );
		bdvGetSources().add( source );

		source.setDisplayRangeBounds( 0, maxVal );
		source.setDisplayRange( minVal, maxVal );
		source.setColor( color );
		source.setActive( isActive );
	}

	public default < T extends RealType< T > & NativeType< T > > void bdvRemove( final RandomAccessibleInterval< T > img ) {
		final BdvSource source = bdvGetSourceFor( img );
		source.removeFromBdv();
		bdvGetSources().remove( source );
	}

	public default void bdvRemoveAll() {
		for ( final BdvSource source : bdvGetSources()) {
			source.removeFromBdv();
		}
		bdvGetSources().clear();
	}

	public default < T extends RealType< T > & NativeType< T > > void bdvShowOnly( final RandomAccessibleInterval< T > img ) {
		final VisibilityAndGrouping vg = bdvGetHandlePanel().getViewerPanel().getVisibilityAndGrouping();
		vg.setDisplayMode( DisplayMode.SINGLE );
		final BdvSource source = bdvGetSourceFor( img );
		if ( source != null ) source.setCurrent();
	}

	public default < T extends RealType< T > & NativeType< T > > void bdvShowAll( final RandomAccessibleInterval< T > img ) {
		final VisibilityAndGrouping vg = bdvGetHandlePanel().getViewerPanel().getVisibilityAndGrouping();
		vg.setDisplayMode( DisplayMode.FUSED );
		for ( final BdvSource source : bdvGetSources() ) {
			source.setActive( true );
		}
	}
}
