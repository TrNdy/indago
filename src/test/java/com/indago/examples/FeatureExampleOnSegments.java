/**
 *
 */
package com.indago.examples;

import java.util.ArrayList;

import net.imagej.ops.OpService;
import net.imagej.ops.features.OpResolverService;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedIntType;

import org.scijava.Context;

import com.indago.benchmarks.RandomCostBenchmarks.Parameters;
import com.indago.segment.RandomForestFactory;

/**
 * @author jug
 */
public class FeatureExampleOnSegments {

	private static int baseSeed = 4711;
	private static int numImgsPerParameterSet = 10;

	public static void main(final String[] args) {

		final ArrayList< Parameters > parameterSets = new ArrayList< Parameters >();
		parameterSets.add( new Parameters( 256, 256, 10, 20.0, 0.5, 6.0, 1.0, 0.0, 0, 25 ) ); // first one we used
		parameterSets.add( new Parameters( 256, 256, 25, 20.0, 0.5, 6.0, 1.0, 0.0, 0, 25 ) ); // denser one

		// create service & context
		final Context c = new Context();
		final OpResolverService ors = c.service(OpResolverService.class);
		final OpService ops = c.service(OpService.class);

		for ( int i = 0; i < parameterSets.size(); i++ ) {
			System.out.print( String.format( "Generating random synthetic dataset %d of %d... ", i + 1, parameterSets.size() ) );
			final ArrayList< Img< UnsignedIntType >> imgs = generateRandomSyntheticImgs( numImgsPerParameterSet, baseSeed + i, parameterSets.get( i ) );
			System.out.println( "done!" );

		}
	}

	public static ArrayList< Img< UnsignedIntType >> generateRandomSyntheticImgs( final int numImgs, final int seed, final Parameters p ) {

		final ArrayList< Img< UnsignedIntType >> imgs = new ArrayList< Img< UnsignedIntType >>();
		for ( int f = 0; f < numImgs; f++ ) {
			imgs.add( RandomForestFactory.getForestImg( p.width, p.height, p.numSeedPixels, p.maxRadius, p.minDeltaR, p.maxDeltaR, p.meanDeltaR, p.sdDeltaR, p.minIntensity, p.maxIntensity, seed + f ) );
		}

		return imgs;
	}
}
