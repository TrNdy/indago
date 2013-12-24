/**
 *
 */
package com.jug.indago.influit.nodes;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.Field;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.scijava.plugin.Parameter;


/**
 * @author jug
 */
public class InfluitNodePropertiesFactory {

	/**
	 * @param imagePlusNode
	 * @return
	 */
	public static JPanel generatePanelFromAnnotations( final InfluitNode node ) {
		final JPanel ret = new JPanel( new GridBagLayout() );

		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.weightx = 0.5;
	    c.gridx = 0;
	    c.gridy = 0;

		for ( final Field f : node.getClass().getDeclaredFields() ) {
			final Parameter param = f.getAnnotation( Parameter.class );
			if ( param != null ) {
				final JLabel label = new JLabel( param.label() );
				label.setToolTipText( param.description() );
				ret.add( label, c );

				c.gridx++;
				ret.add( new JTextField(), c );

				c.gridy++;
				c.gridx = 0;
			}
		}

		return ret;
	}

}
