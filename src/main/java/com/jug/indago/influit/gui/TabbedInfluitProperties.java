/**
 *
 */
package com.jug.indago.influit.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.jug.indago.influit.nodes.InfluitNode;

/**
 * @author jug
 */
public class TabbedInfluitProperties extends JTabbedPane implements ItemListener {

	private static final long serialVersionUID = -3648133817929245171L;

	public TabbedInfluitProperties() {
	}

	/**
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged( final ItemEvent e ) {
		if ( e.getItem() instanceof InfluitNode ) {
			if ( e.getStateChange() == e.SELECTED ) {
				final JPanel p = ( ( InfluitNode ) e.getItem() ).getPropertiesPanel();
				if ( p != null ) {
					this.add( ( ( InfluitNode ) e.getItem() ).toString(), p );
				}
			} else {
				final JPanel p = ( ( InfluitNode ) e.getItem() ).getPropertiesPanel();
				if ( p != null ) {
					this.remove( p );
				}
			}
		}
	}
}
