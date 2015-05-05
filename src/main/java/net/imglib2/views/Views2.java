package net.imglib2.views;

import java.util.Arrays;
import java.util.List;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.views.StackView.StackAccessMode;

public class Views2
{
	/**
	 * Form a <em>(n+1)</em>-dimensional {@link RandomAccessibleInterval} by
	 * stacking <em>n</em>-dimensional {@link RandomAccessibleInterval}s.
	 *
	 * @param hyperslices
	 *            a list of <em>n</em>-dimensional
	 *            {@link RandomAccessibleInterval} of identical sizes.
	 * @return a <em>(n+1)</em>-dimensional {@link RandomAccessibleInterval}
	 *         where the final dimension is the index of the hyperslice.
	 */
	public static < T > RandomAccessibleInterval< T > stack( final List< RandomAccessibleInterval< T > > hyperslices )
	{
		return new StackView< T >( hyperslices );
	}

	/**
	 * Form a <em>(n+1)</em>-dimensional {@link RandomAccessibleInterval} by
	 * stacking <em>n</em>-dimensional {@link RandomAccessibleInterval}s.
	 *
	 * @param hyperslices
	 *            a list of <em>n</em>-dimensional
	 *            {@link RandomAccessibleInterval} of identical sizes.
	 * @return a <em>(n+1)</em>-dimensional {@link RandomAccessibleInterval}
	 *         where the final dimension is the index of the hyperslice.
	 */
	@SafeVarargs
	public static < T > RandomAccessibleInterval< T > stack( final RandomAccessibleInterval< T >... hyperslices )
	{
		return new StackView< T >( Arrays.asList( hyperslices ) );
	}

	/**
	 * Form a <em>(n+1)</em>-dimensional {@link RandomAccessibleInterval} by
	 * stacking <em>n</em>-dimensional {@link RandomAccessibleInterval}s.
	 *
	 * @param stackAccessMode
	 *            describes how a {@link RandomAccess} on the <em>(n+1)</em>
	 *            -dimensional {@link StackView} maps position changes into
	 *            position changes of the underlying <em>n</em>-dimensional
	 *            {@link RandomAccess}es.
	 * @param hyperslices
	 *            a list of <em>n</em>-dimensional
	 *            {@link RandomAccessibleInterval} of identical sizes.
	 * @return a <em>(n+1)</em>-dimensional {@link RandomAccessibleInterval}
	 *         where the final dimension is the index of the hyperslice.
	 */
	public static < T > RandomAccessibleInterval< T > stack( final StackAccessMode stackAccessMode, final List< RandomAccessibleInterval< T > > hyperslices )
	{
		return new StackView< T >( hyperslices, stackAccessMode );
	}

	/**
	 * Form a <em>(n+1)</em>-dimensional {@link RandomAccessibleInterval} by
	 * stacking <em>n</em>-dimensional {@link RandomAccessibleInterval}s.
	 *
	 * @param stackAccessMode
	 *            describes how a {@link RandomAccess} on the <em>(n+1)</em>
	 *            -dimensional {@link StackView} maps position changes into
	 *            position changes of the underlying <em>n</em>-dimensional
	 *            {@link RandomAccess}es.
	 * @param hyperslices
	 *            a list of <em>n</em>-dimensional
	 *            {@link RandomAccessibleInterval} of identical sizes.
	 * @return a <em>(n+1)</em>-dimensional {@link RandomAccessibleInterval}
	 *         where the final dimension is the index of the hyperslice.
	 */
	@SafeVarargs
	public static < T > RandomAccessibleInterval< T > stack( final StackAccessMode stackAccessMode, final RandomAccessibleInterval< T >... hyperslices )
	{
		return new StackView< T >( Arrays.asList( hyperslices ), stackAccessMode );
	}
}