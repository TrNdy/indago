/**
 *
 */
package com.jug.indago.influit.gui.dialog;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.jug.indago.influit.InfluitPanel;


/**
 * @author jug
 */
public class AboutDialog {

	/**
	 * Shows the about dialog.
	 *
	 * @param influitPanel
	 */
	public static void show( final InfluitPanel influitPanel ) {
		JOptionPane.showMessageDialog( SwingUtilities.getWindowAncestor( influitPanel ), "Trial attempt to get reasonable workflows going in hopefully quite easy way...\n\nAuthors: Florian Jug & Tobias Pietzsch", "About Influit...", JOptionPane.INFORMATION_MESSAGE );
	}
}
