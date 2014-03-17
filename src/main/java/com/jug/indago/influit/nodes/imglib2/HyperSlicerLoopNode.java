/**
 *
 */
package com.jug.indago.influit.nodes.imglib2;

import ij.ImagePlus;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.scijava.plugin.Plugin;

import com.jug.indago.influit.data.GenericInfluitDatum;
import com.jug.indago.influit.data.InfluitDatum;
import com.jug.indago.influit.data.InfluitFormatIdentifyer;
import com.jug.indago.influit.exception.InfluitFormatException;
import com.jug.indago.influit.nodes.InfluitNode;


/**
 * @author jug
 */
@Plugin( type = HyperSlicerLoopNode.class, label = "HyperSlicer Loop", menuPath = "Loops" )
public class HyperSlicerLoopNode implements InfluitNode {

	private JScrollPane propPanel = null;

	private int dimension;
	private int min_index;
	private int max_index;

	private JTextField txtDimension;

	private JTextField txtFrom;

	private JTextField txtTo;

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
	 * @see com.jug.indago.influit.nodes.InfluitNode#getPropertiesPane()
	 */
	@Override
	public JScrollPane getPropertiesPane() {
		if ( propPanel != null ) return propPanel;

		final JPanel p = new JPanel( new GridBagLayout() );
		propPanel = new JScrollPane( p );
		p.setBorder( BorderFactory.createEmptyBorder( 0, 10, 0, 10 ) );

		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
//		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;

		p.add( new JLabel( "Dimension:" ), c );

		c.gridx++;
		txtDimension = new JTextField( 2 );
		p.add( txtDimension, c );

		c.gridy++;
		c.gridx = 0;
		p.add( new JLabel( "From slice:" ), c );

		c.gridx++;
		txtFrom = new JTextField( 5 );
		p.add( txtFrom, c );

		c.gridy++;
		c.gridx = 0;
		p.add( new JLabel( "To slice:" ), c );

		c.gridx++;
		txtTo = new JTextField( 5 );
		p.add( txtTo, c );

		return propPanel;
	}

}
