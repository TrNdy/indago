/**
 *
 */
package com.indago.examples.serialization.featuresets;

import java.util.HashSet;
import java.util.Set;

import net.imagej.ops.Op;
import net.imagej.ops.OpRef;
import net.imagej.ops.OpService;
import net.imagej.ops.features.AbstractAutoResolvingFeatureSet;
import net.imagej.ops.features.OpResolverService;
import net.imagej.ops.features.firstorder.FirstOrderFeatures.MeanFeature;
import net.imagej.ops.features.firstorder.FirstOrderFeatures.SumFeature;
import net.imagej.ops.features.firstorder.FirstOrderFeatures.VarianceFeature;
import net.imglib2.IterableInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

import org.scijava.Context;


/**
 * @author jug
 */
public class DemoFeatureSet< T extends RealType< T > & NativeType< T >>
		extends
		AbstractAutoResolvingFeatureSet< IterableInterval< T >, DoubleType > {

	private final HashSet< OpRef< ? >> outputOps;
	private final HashSet< OpRef< ? >> hiddenOps;

	public DemoFeatureSet() {
		this.outputOps = new HashSet< OpRef< ? >>();
		this.hiddenOps = new HashSet< OpRef< ? >>();

		// =======================
		// CREATE AND ADD FEATURES
		// =======================

		@SuppressWarnings( "rawtypes" )
		final OpRef< MeanFeature > oprefMean =
				new OpRef< MeanFeature >( "mean", MeanFeature.class );

		@SuppressWarnings( "rawtypes" )
		final OpRef< SumFeature > oprefSum =
				new OpRef< SumFeature >( "sum", SumFeature.class );

		@SuppressWarnings( "rawtypes" )
		final OpRef< VarianceFeature > oprefVar =
				new OpRef< VarianceFeature >( "variance", VarianceFeature.class );

		this.addOutputOp( oprefMean );
		this.addOutputOp( oprefSum );
		this.addOutputOp( oprefVar );
	}

	@Override
	public Set< OpRef< ? >> getOutputOps() {
		return outputOps;
	}

	@Override
	public Set< OpRef< ? >> getHiddenOps() {
		return hiddenOps;
	}

	public < OP extends Op > void addHiddenOp( final Class< OP > type, final Object... params ) {
		addHiddenOp( new OpRef< OP >( type, params ) );
	}

	public < OP extends Op > void addOutputOp( final Class< OP > type, final Object... params ) {
		addOutputOp( new OpRef< OP >( type, params ) );
	}

	public void addOutputOp( final OpRef< ? > ref ) {
		outputOps.add( ref );
	}

	public void addHiddenOp( final OpRef< ? > ref ) {
		hiddenOps.add( ref );
	}

	/**
	 * Demo for how to set up a feature set...
	 *
	 * @param args
	 */
	public static void main( final String[] args ) {
		final Context c = new Context();
		final OpResolverService ors = c.service( OpResolverService.class );
		final OpService ops = c.service( OpService.class );

		final DemoFeatureSet< DoubleType > featureSet = new DemoFeatureSet< DoubleType >();

		c.inject( featureSet );
	}
}

