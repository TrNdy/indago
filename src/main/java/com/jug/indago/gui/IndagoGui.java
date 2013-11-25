/**
 *
 */
package com.jug.indago.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jug.indago.Indago;
import com.jug.indago.influit.InfluitPanel;
import com.jug.indago.model.IndagoModel;


/**
 * @author jug
 */
public class IndagoGui extends JPanel implements ChangeListener, ActionListener {

	private static final long serialVersionUID = 707408717528907315L;

	/**
	 * The frame this GUI is in.
	 */
	private final JFrame frame;

	/**
	 * The MVC model of the IndagoGui.
	 */
	private final IndagoModel model;

	/**
	 * The hirzontal JSplitPane.
	 */
	private JSplitPane splitPane;

	/**
	 * The <code>InfluitPanel</code> hosts the data-flow diagram.
	 */
	private InfluitPanel influitPanel;

	/**
	 * Panel showing the properties of the currently selected and all pinned
	 * influit elements.
	 */
	private JTabbedPane tabsProps;

	/**
	 * Panel showing the viewer-view of the currently selected and all pinned
	 * influit elements.
	 */
	private JTabbedPane tabsViewer;

	/**
	 * @param model
	 */
	public IndagoGui( final JFrame frame, final IndagoModel model ) {
		this.frame = frame;
		this.model = model;
		buildGui();
	}

	/**
	 * Builds the GUI.
	 */
	private void buildGui() {
		this.setLayout( new BorderLayout() );
		final JPanel panelLeft = new JPanel( new BorderLayout() );

		influitPanel = new InfluitPanel( model );
		tabsProps = new JTabbedPane();
		tabsProps.add( "None", new JButton( "no props" ) );
		tabsViewer = new JTabbedPane();
		tabsViewer.add( "None", new JButton( "no viewers" ) );

		panelLeft.add( influitPanel, BorderLayout.NORTH );
		panelLeft.add( tabsProps, BorderLayout.CENTER );

		splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, panelLeft, tabsViewer );
		splitPane.setOneTouchExpandable( true );
		splitPane.setDividerLocation( Indago.DIVIDER_LOCATION );

		this.add( splitPane, BorderLayout.CENTER );
	}

	public int getDividerLocation() {
		return this.splitPane.getDividerLocation();
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed( final ActionEvent e ) {
	}

	/**
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged( final ChangeEvent e ) {
	}
}
