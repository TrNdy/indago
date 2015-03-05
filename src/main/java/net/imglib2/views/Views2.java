package net.imglib2.views;

import java.util.Arrays;
import java.util.List;

import net.imglib2.RandomAccessibleInterval;

public class Views2
{
	public static < T > RandomAccessibleInterval< T > stack( final List< RandomAccessibleInterval< T > > hyperslices )
	{
		return new RandomAccessibleIntervalStack< T >( hyperslices );
	}

	@SafeVarargs
	public static < T > RandomAccessibleInterval< T > stack( final RandomAccessibleInterval< T >... hyperslices )
	{
		return new RandomAccessibleIntervalStack< T >( Arrays.asList( hyperslices ) );
	}
}