/**
 *
 */
package com.jug.indago.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jug.indago.Indago;
import com.jug.indago.influit.InfluitPanel;
import com.jug.indago.influit.edges.GenericInfluitEdge;
import com.jug.indago.influit.nodes.ij.ImagePlusNode;
import com.jug.indago.influit.nodes.imglib2.FilteredComponentTreeNode;
import com.jug.indago.influit.nodes.imglib2.HyperSlicerLoopNode;
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
	 * The horizontal JSplitPane.
	 */
	private JSplitPane horizontalSplitPane;
	/**
	 * The vertical JSplitPane.
	 */
	private JSplitPane verticalSplitPane;

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

		influitPanel = new InfluitPanel( new Dimension( 400, 400 ), true );
		influitPanel.setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ) );

		// @TODO this must be replaced at some point!
		final ImagePlusNode imgPlusNode = new ImagePlusNode( model.getImgPlus() );
		final HyperSlicerLoopNode slicerLoopNode = new HyperSlicerLoopNode();
		final FilteredComponentTreeNode compTreeNode = new FilteredComponentTreeNode();

		final GenericInfluitEdge< ImagePlusNode, HyperSlicerLoopNode > edge1 = new GenericInfluitEdge< ImagePlusNode, HyperSlicerLoopNode >( imgPlusNode, slicerLoopNode );
		final GenericInfluitEdge< HyperSlicerLoopNode, FilteredComponentTreeNode > edge2 = new GenericInfluitEdge< HyperSlicerLoopNode, FilteredComponentTreeNode >( slicerLoopNode, compTreeNode );

		influitPanel.addNode( imgPlusNode );
		influitPanel.addNode( slicerLoopNode );
		influitPanel.addNode( compTreeNode );
		influitPanel.addEdge( edge1, imgPlusNode, slicerLoopNode );
		influitPanel.addEdge( edge2, slicerLoopNode, compTreeNode );

		tabsProps = new JTabbedPane();
		tabsProps.add( "None", new JButton( "no props" ) );
		tabsViewer = new JTabbedPane();
		tabsViewer.add( "Console", model.getConsole() );

		verticalSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT, influitPanel, tabsProps );
		verticalSplitPane.setOneTouchExpandable( true );
		verticalSplitPane.setDividerLocation( Indago.VERTICAL_DIVIDER_LOCATION );

		horizontalSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, verticalSplitPane, tabsViewer );
		horizontalSplitPane.setOneTouchExpandable( true );
		horizontalSplitPane.setDividerLocation( Indago.HORIZONTAL_DIVIDER_LOCATION );

		this.add( horizontalSplitPane, BorderLayout.CENTER );
	}

	public int getHorizontalDividerLocation() {
		return this.horizontalSplitPane.getDividerLocation();
	}

	public int getVerticalDividerLocation() {
		return this.verticalSplitPane.getDividerLocation();
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
