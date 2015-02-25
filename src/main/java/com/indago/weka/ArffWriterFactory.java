/**
 *
 */
package com.indago.weka;

import net.imagej.ops.Op;
import net.imagej.ops.OpRef;
import net.imagej.ops.features.DefaultAutoResolvingFeatureSet;
import net.imglib2.IterableInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * @author jug
 */
public class ArffWriterFactory {

	public static < T extends RealType< T > & NativeType< T > > ArffWriter getArffWriterFor(
			final DefaultAutoResolvingFeatureSet< IterableInterval< T >, DoubleType > featureSet ) {
		for ( final OpRef< ? extends Op > oOp : featureSet.getOutputOps() ) {
			System.out.println( ">> " + oOp.getName() );
			System.out.println( "   >> " + oOp.getLabel() );
		}
		return null;
	}
}
