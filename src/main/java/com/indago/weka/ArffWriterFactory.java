/**
 *
 */
package com.indago.weka;

import java.util.ArrayList;

import net.imagej.ops.Op;
import net.imagej.ops.OpRef;
import net.imagej.ops.features.DefaultAutoResolvingFeatureSet;
import net.imglib2.IterableInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import weka.core.Attribute;

/**
 * @author jug
 */
public class ArffWriterFactory {

	public static < T extends RealType< T > & NativeType< T > > ArffBuilder getArffBuilderFor(
			final DefaultAutoResolvingFeatureSet< IterableInterval< T >, DoubleType > featureSet ) {

		// Declare the feature vector
		final ArrayList< Attribute > fvWekaAttributes = new ArrayList< Attribute >();

		// Create the class label attribute
		fvWekaAttributes.add( ArffBuilder.getClassAttribute() );

		for ( final OpRef< ? extends Op > oOp : featureSet.getOutputOps() ) {
			if ( oOp.getName() == null ) {
				throw new IllegalArgumentException("ArffWriterFactory requires that all OutputOps in a FeatureSet are named. Please name them when adding ops to the feauter set.");
			}
			final Attribute attrFeature = new Attribute( oOp.getLabel() );
			fvWekaAttributes.add( attrFeature );
		}

		return new ArffBuilder( "FeatureSet data", fvWekaAttributes );
	}
}
