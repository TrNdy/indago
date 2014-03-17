/**
 *
 */
package com.jug.indago.influit.nodes.ij;

import ij.ImagePlus;
import ij.WindowManager;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.scijava.plugin.Plugin;

import com.jug.indago.influit.data.GenericInfluitDatum;
import com.jug.indago.influit.data.InfluitDatum;
import com.jug.indago.influit.data.InfluitFormatIdentifyer;
import com.jug.indago.influit.exception.InfluitFormatException;
import com.jug.indago.influit.nodes.InfluitNode;


/**
 * @author jug
 */
@Plugin( type = ImagePlusNode.class, label = "ImagePlus Source", menuPath = "Data Sources>ImagePlus Source" )
public class ImagePlusNode implements InfluitNode, ActionListener {

	private ImagePlus imp;

	private JScrollPane propPanel = null;

	private JComboBox combo;

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
	public List< InfluitFormatIdentifyer > getSupportedInputFormats() {
		return new ArrayList< InfluitFormatIdentifyer >();
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

		p.add( new JLabel( "IJ image:" ), c );

		c.gridx++;
		combo = new JComboBox();
		final int[] img_ids = WindowManager.getIDList();
		for ( int idx = 0; idx < img_ids.length; idx++ ) {
			combo.addItem( WindowManager.getImage( img_ids[ idx ] ).getTitle() );
		}
		if (imp != null) {
			combo.setSelectedItem( imp.getTitle() );
		}
		combo.addActionListener( this );
		p.add( combo, c );

		return propPanel;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed( final ActionEvent e ) {
		final String img_name = combo.getSelectedItem().toString();
		imp = WindowManager.getImage( img_name );
//		System.out.println( img_name );
//		System.out.println( imp.getTitle() );
	}

}
