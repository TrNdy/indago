/**
 *
 */
package com.jug.indago.influit.nodes.ij;

import ij.ImagePlus;

import java.util.ArrayList;
import java.util.List;

import com.jug.indago.influit.data.GenericInfluitDatum;
import com.jug.indago.influit.data.InfluitDatum;
import com.jug.indago.influit.exception.InfluitFormatException;
import com.jug.indago.influit.nodes.InfluitNode;


/**
 * @author jug
 */
public class ImagePlusNode implements InfluitNode {

	private final ImagePlus imp;

	public ImagePlusNode( final ImagePlus imp ) {
		this.imp = imp;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return imp.getTitle();
	}

	/**
	 * @see com.jug.indago.influit.nodes.InfluitNode#getSupportedInputFormats()
	 */
	@Override
	public List< InfluitDatum > getSupportedInputFormats() {
		return new ArrayList< InfluitDatum >();
	}

	/**
	 * @see com.jug.indago.influit.nodes.InfluitNode#getSupportedOutputFormats()
	 */
	@Override
	public List< InfluitDatum > getSupportedOutputFormats() {
		final List< InfluitDatum > ret = new ArrayList< InfluitDatum >();
		ret.add( new GenericInfluitDatum< ImagePlus >( imp ) );
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
