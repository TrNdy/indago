/**
 * 
 */
package com.indago.segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.UnsignedIntType;

/**
 * @author jug
 */
public class RandomForestFactory {

	private static class SeededCone {

		int x;
		int y;
		double minR;
		double maxR;
		double deltaR;
		int minI;
		int maxI;

		public SeededCone( final int x, final int y, final double maxR, final double deltaR, final int minIntensity, final int maxIntensity ) {
			this.x = x;
			this.y = y;
			this.minR = minR;
			this.maxR = maxR;
			this.deltaR = deltaR;
			this.minI = minIntensity;
			this.maxI = maxIntensity;
		}

		public int getColorAt( final int x, final int y ) {
			final double d = Math.sqrt( ( x - this.x ) * ( x - this.x ) + ( y - this.y ) * ( y - this.y ) );
			int ret = maxI - ( int ) ( d / deltaR );
			if ( d > maxR ) ret = 0;
			return ret;
		}
	}

	/**
	 * @param width
	 * @param height
	 * @param numSeedPixels
	 * @param minRadius
	 * @param minDeltaR
	 * @param meanDeltaR
	 * @param sdDeltaR
	 * @param minIntensity
	 * @param maxIntensity
	 * @return
	 */
	public static Img< UnsignedIntType > getForestImg( final int width, final int height, final int numSeedPixels, final double maxRadius, final double minDeltaR, final double maxDeltaR, final double meanDeltaR, final double sdDeltaR, final int minIntensity, final int maxIntensity, final int seed ) {
		final ImgFactory< UnsignedIntType > imgFactory = new ArrayImgFactory< UnsignedIntType >();
		final Img< UnsignedIntType > forestImg = imgFactory.create( new long[] { width, height }, new UnsignedIntType() );

		final Random rand = new Random( seed );
		final List< SeededCone > cones = new ArrayList< SeededCone >();
		for ( int i = 0; i < numSeedPixels; i++ ) {
			final int x = ( int ) ( rand.nextDouble() * width );
			final int y = ( int ) ( rand.nextDouble() * height );
			double deltaR = minDeltaR - 1;
			while ( deltaR < minDeltaR || deltaR > maxDeltaR )
				deltaR = meanDeltaR + rand.nextGaussian() * sdDeltaR;

			cones.add( new SeededCone( x, y, maxRadius, deltaR, minIntensity, maxIntensity ) );
		}
		final Cursor< UnsignedIntType > c = forestImg.cursor();
		while ( c.hasNext() ) {
			int max = Integer.MIN_VALUE;
			int sum = 0;
			for ( final SeededCone cone : cones ) {
				final int value = cone.getColorAt( c.getIntPosition( 0 ), c.getIntPosition( 1 ) );
				max = Math.max( max, value );
				sum += value;
			}
			c.next().set( max );
		}

		return forestImg;
	}
}
