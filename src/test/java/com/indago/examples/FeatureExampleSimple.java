/**
 *
 */
package com.indago.examples;

import org.scijava.Context;

import net.imagej.ops.OpService;
import net.imagej.ops.Ops.Stats.Mean;
import net.imagej.ops.special.Computers;
import net.imagej.ops.special.UnaryComputerOp;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;


/**
 * @author jug
 */
public class FeatureExampleSimple {
	public static void main(final String[] args) {

		// some rnd data (could actually be an op on its own)
		final Img<FloatType> rndImgA = ArrayImgs
				.floats(new long[] { 100, 100 });
		final Img<FloatType> rndImgB = ArrayImgs
				.floats(new long[] { 500, 150 });

		for (final FloatType type : rndImgA) {
			type.set((float) Math.random());
		}

		for (final FloatType type : rndImgB) {
			type.set((float) Math.random());
		}

		// create service & context
		final Context c = new Context();
		final OpService ops = c.getService( OpService.class );

		/*
		 * Call a single feature.
		 */
		// 1. Create ResolvedOp (my naming sucks, ideas welcome)
		final DoubleType mean = new DoubleType();
		final UnaryComputerOp< Img< FloatType >, DoubleType > meanOp = Computers.unary(ops, Mean.class, DoubleType.class, rndImgA );

		// 2. Calculate and print results
		meanOp.compute1( rndImgA, mean );
		System.out.println("Mean: " + mean.get());
		meanOp.compute1( rndImgB, mean );
		System.out.println("Mean: " + mean.get());
	}
}
