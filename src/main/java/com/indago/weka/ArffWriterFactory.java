/**
 *
 */
package com.indago.weka;

import java.util.ArrayList;

import com.indago.data.segmentation.features.FeatureSet;

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
			final FeatureSet< IterableInterval< T >, DoubleType > featureSet ) {

		// Declare the feature vector
		final ArrayList< Attribute > fvWekaAttributes = new ArrayList< Attribute >();

		// Create the class label attribute
		fvWekaAttributes.add( ArffBuilder.getClassAttribute() );

		for ( final String name : featureSet.getNamedOutputs().keySet() ) {
			final Attribute attrFeature = new Attribute( name );
			fvWekaAttributes.add( attrFeature );
		}

		return new ArffBuilder( "FeatureSet data", fvWekaAttributes );
	}
}
