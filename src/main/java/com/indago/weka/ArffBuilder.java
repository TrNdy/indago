/**
 *
 */
package com.indago.weka;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import net.imagej.ops.Op;
import net.imagej.ops.OpRef;
import net.imglib2.type.numeric.real.DoubleType;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;


/**
 * @author jug
 */
public class ArffBuilder {

	public static String POSITIVE_INSTANCE = "+";
	public static String NEGATIVE_INSTANCE = "-";
	public static String UNKNOWN_INSTANCE = "?";

	private final ArrayList< Attribute > fvAttributes;
	private final Instances data;

	/**
	 * Initializes an ArffWriter that can collect and write data that complies
	 * to the given <code>FastVector</code>.
	 *
	 * @param fvAttributes
	 */
	public ArffBuilder( final String name, final ArrayList< Attribute > fvAttributes ) {
		this.fvAttributes = fvAttributes;
		data = new Instances( name, fvAttributes, 100 );
		data.setClassIndex( 0 );
	}

	/**
	 * Adds one data-point to the instances stored in this
	 * <code>ArffWriter</code>.
	 * All class values of added instances will be set to
	 * <code>UNKNOWN_INSTANCE</code>.
	 *
	 * @param features
	 *            Features as they are spit out of an feature set (like e.g.
	 *            <code>DefaultAutoResolvingFeatureSet</code>
	 * @return the number of features that could be matched to the columns of
	 *         this <code>ArffWriter</code>.
	 */
	public int addData( final Map< OpRef< ? extends Op >, DoubleType > features ) {
		return addData( features, UNKNOWN_INSTANCE );
	}

	/**
	 * Adds one data-point to the instances stored in this
	 * <code>ArffWriter</code>.
	 *
	 * @param features
	 *            Features as they are spit out of an feature set (like e.g.
	 *            <code>DefaultAutoResolvingFeatureSet</code>
	 * @param classIdentifier
	 *            must be one of ArffBuilder.POSITIVE_INSTANCE,
	 *            ArffBuilder.NEGATIVE_INSTANCE, or
	 *            ArffBuilder.UNKNOWN_INSTANCE.
	 * @return the number of features that could be matched to the columns of
	 *         this <code>ArffWriter</code>.
	 */
	public int addData(
			final Map< OpRef< ? extends Op >, DoubleType > features,
			final String classIdentifier ) {
		final Instance newrow = new DenseInstance(fvAttributes.size());
		int fieldCount = 0;

		if ( !classIdentifier.equals( UNKNOWN_INSTANCE ) ) {
			if ( !classIdentifier.equals( POSITIVE_INSTANCE ) && !classIdentifier.equals( NEGATIVE_INSTANCE ) ) { throw new IllegalArgumentException( "Given classIdentifier must be one of ArffBuilder.POSITIVE_INSTANCE, ArffBuilder.NEGATIVE_INSTANCE, or ArffBuilder.UNKNOWN_INSTANCE." ); }
			newrow.setValue( fvAttributes.get( 0 ), classIdentifier );
		}

		for ( final OpRef< ? extends Op > oOp : features.keySet() ) {
			for ( final Attribute attribute : fvAttributes ) {
				if ( attribute.name().equals( oOp.getLabel() ) ) {
					newrow.setValue( attribute, features.get( oOp ).get() );
					fieldCount++;
				}
			}
		}

		// add the instance
		data.add(newrow);

		return fieldCount;
	}

	/**
	 * @return all added data instances.
	 */
	public Instances getData() {
		return data;
	}

	/**
	 * @param filename
	 *            the name of the file to write the ARFF to.
	 *            If the given filename does not end in ".arff", this extension
	 *            will be added.
	 * @throws FileNotFoundException
	 */
	public void write( String filename ) throws FileNotFoundException {
		if ( !filename.toLowerCase().endsWith( ".arff" ) ) {
			filename += ".arff";
		}
		final PrintWriter out = new PrintWriter( filename );
		out.println( data.toString() );
		out.close();
	}

	/**
	 * @return the class attribute definition required to be at attribute index
	 *         0.
	 */
	public static Attribute getClassAttribute() {
		final ArrayList< String > fvClassVal = new ArrayList< String >( 2 );
		fvClassVal.add( ArffBuilder.POSITIVE_INSTANCE );
		fvClassVal.add( ArffBuilder.NEGATIVE_INSTANCE );
		return new Attribute( "class", fvClassVal );
	}

}
