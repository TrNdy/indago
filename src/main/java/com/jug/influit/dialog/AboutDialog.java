/**
 *
 */
package com.jug.influit.dialog;

import javax.swing.JFrame;
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
	public static void show(final InfluitPanel influitPanel) {
		JOptionPane.showMessageDialog( ( JFrame ) SwingUtilities.getWindowAncestor( influitPanel ),
			    "Eggs are not supposed to be green.",
			    "Inane custom dialog",
			    JOptionPane.INFORMATION_MESSAGE);
	}

}
