/**
 *
 */
package com.jug.indago.influit.nodes.imglib2;

import ij.ImagePlus;

import java.util.ArrayList;
import java.util.List;

import org.scijava.plugin.Plugin;

import com.jug.indago.influit.data.GenericInfluitDatum;
import com.jug.indago.influit.data.InfluitDatum;
import com.jug.indago.influit.exception.InfluitFormatException;
import com.jug.indago.influit.nodes.InfluitNode;


/**
 * @author jug
 */
@Plugin( type = FilteredComponentTreeNode.class, label = "Filtered Comp.Tree", menuPath = "Segmentation Hypotheses" )
public class FilteredComponentTreeNode implements InfluitNode {

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FilteredComponentTreeNode";
	}

	/**
	 * @see com.jug.indago.influit.nodes.InfluitNode#getSupportedInputFormats()
	 */
	@Override
	public List< InfluitDatum > getSupportedInputFormats() {
		final List< InfluitDatum > ret = new ArrayList< InfluitDatum >();
		ret.add( new GenericInfluitDatum< ImagePlus >( new ImagePlus() ) );
		return ret;
	}

	/**
	 * @see com.jug.indago.influit.nodes.InfluitNode#getSupportedOutputFormats()
	 */
	@Override
	public List< InfluitDatum > getSupportedOutputFormats() {
		final List< InfluitDatum > ret = new ArrayList< InfluitDatum >();
		ret.add( new GenericInfluitDatum< ImagePlus >( new ImagePlus() ) );
		return ret;
	}

	/**
	 * @see com.jug.indago.influit.nodes.InfluitNode#canEvaluate()
	 */
	@Override
	public boolean canEvaluate() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see com.jug.indago.influit.nodes.InfluitNode#evaluate()
	 */
	@Override
	public void evaluate() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see com.jug.indago.influit.nodes.InfluitNode#getOutput(com.jug.indago.influit.data.InfluitDatum)
	 */
	@Override
	public void getOutput( final InfluitDatum data ) throws InfluitFormatException {
		// TODO Auto-generated method stub

	}

}
