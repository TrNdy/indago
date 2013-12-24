/**
 *
 */
package com.jug.indago.influit.nodes.imglib2;

import ij.ImagePlus;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.jug.indago.influit.data.GenericInfluitDatum;
import com.jug.indago.influit.data.InfluitDatum;
import com.jug.indago.influit.data.InfluitFormatIdentifyer;
import com.jug.indago.influit.exception.InfluitFormatException;
import com.jug.indago.influit.nodes.InfluitNode;
import com.jug.indago.influit.nodes.InfluitNodePropertiesFactory;


/**
 * @author jug
 */
@Plugin( type = HyperSlicerLoopNode.class, label = "HyperSlicer Loop", menuPath = "Loops" )
public class HyperSlicerLoopNode implements InfluitNode {

	private JPanel propPanel = null;

	@Parameter( label = "Dimension:" )
	private int dimension;
	@Parameter( label = "From (index):" )
	private int min_index;
	@Parameter( label = "To (index):" )
	private int max_index;

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HyperSliceLoopNode";
	}

	/**
	 *
	 */
	public HyperSlicerLoopNode() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see com.jug.indago.influit.nodes.InfluitNode#getSupportedInputFormats()
	 */
	@Override
	public List< InfluitFormatIdentifyer > getSupportedInputFormats() {
		final List< InfluitFormatIdentifyer > ret = new ArrayList< InfluitFormatIdentifyer >();
		ret.add( new GenericInfluitDatum< ImagePlus >( new ImagePlus() ).getFormatIdentifyer() );
		return ret;
	}

	/**
	 * @see com.jug.indago.influit.nodes.InfluitNode#getSupportedOutputFormats()
	 */
	@Override
	public List< InfluitFormatIdentifyer > getSupportedOutputFormats() {
		final List< InfluitFormatIdentifyer > ret = new ArrayList< InfluitFormatIdentifyer >();
		ret.add( new GenericInfluitDatum< ImagePlus >( new ImagePlus() ).getFormatIdentifyer() );
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

	/**
	 * @see com.jug.indago.influit.nodes.InfluitNode#getPropertiesPanel()
	 */
	@Override
	public JPanel getPropertiesPanel() {
		if ( propPanel == null )
			propPanel = InfluitNodePropertiesFactory.generatePanelFromAnnotations( this );
		return propPanel;
	}

}
