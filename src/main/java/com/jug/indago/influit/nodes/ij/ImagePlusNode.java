/**
 *
 */
package com.jug.indago.influit.nodes.ij;

import ij.ImagePlus;

import java.util.ArrayList;
import java.util.List;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.jug.indago.influit.data.GenericInfluitDatum;
import com.jug.indago.influit.data.InfluitDatum;
import com.jug.indago.influit.exception.InfluitFormatException;
import com.jug.indago.influit.nodes.InfluitNode;


/**
 * @author jug
 */
@Plugin( type = ImagePlusNode.class, label = "ImagePlus Source", menuPath = "Data Sources>ImagePlus Source" )
public class ImagePlusNode implements InfluitNode {

	@Parameter
	private ImagePlus imp;

	public ImagePlusNode( final ImagePlus imp ) {
		this.imp = imp;
	}

	public ImagePlusNode() {
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if ( imp == null ) {
			return "ImgPlus: no file loaded";
		} else {
			return "ImgPlus: " + imp.getTitle();
		}
	}

	/**
	 * @see com.jug.indago.influit.nodes.InfluitNode#getSupportedInputFormats()
	 */
	@Override
	public List< String > getSupportedInputFormats() {
		return new ArrayList< String >();
	}

	/**
	 * @see com.jug.indago.influit.nodes.InfluitNode#getSupportedOutputFormats()
	 */
	@Override
	public List< String > getSupportedOutputFormats() {
		final List< String > ret = new ArrayList< String >();
		ret.add( new GenericInfluitDatum< ImagePlus >( new ImagePlus() ).getFormatUID() );
		return ret;
	}

	/**
	 * @see com.jug.indago.influit.nodes.InfluitNode#canEvaluate()
	 */
	@Override
	public boolean canEvaluate() {
		return true;
	}

	/**
	 * @see com.jug.indago.influit.nodes.InfluitNode#evaluate()
	 */
	@Override
	public void evaluate() {
		// nothing has to be done to prepare the data to be given away...
	}

	/**
	 * @see com.jug.indago.influit.nodes.InfluitNode#getOutput(com.jug.indago.influit.data.InfluitDatum)
	 */
	@SuppressWarnings( "unchecked" )
	@Override
	public void getOutput( final InfluitDatum data ) throws InfluitFormatException {
		try {
			final GenericInfluitDatum< ImagePlus > output = ( GenericInfluitDatum< ImagePlus > ) data;
			output.setData( imp );
		} catch ( final ClassCastException e ) {
			throw new InfluitFormatException( e );
		}
	}

}
