/**
 *
 */
package com.jug.indago.influit;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ChainedTransformer;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.scijava.InstantiableException;
import org.scijava.plugin.PluginInfo;

import com.jug.indago.GetNodes;
import com.jug.indago.gui.menu.MenuConstants;
import com.jug.indago.influit.edges.InfluitEdge;
import com.jug.indago.influit.gui.dialog.AboutDialog;
import com.jug.indago.influit.nodes.InfluitNode;
import com.jug.indago.influit.transformer.InfluitNodeShapeRenderer;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.AbstractVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer;

/**
 * @author jug
 */
public class InfluitPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -6410785901686147594L;

	private final Graph< InfluitNode, InfluitEdge > g;

	private List< PluginInfo< InfluitNode >> nodeInfos;

	private JMenuBar menuBar;
	private JMenu menuFile;
	private JMenu menuEdit;
	private JMenu menuNodes;
	private JMenu menuWindow;
	private JMenu menuHelp;
	private JMenuItem menuItemHelpAbout;
	private JMenuItem menuItemFileLoad;
	private JMenuItem menuItemFileSave;

	private FRLayout< InfluitNode, InfluitEdge > jungLayout;
	private InfluitNodeShapeRenderer nodeShapeRenderer;
	private DefaultModalGraphMouse< InfluitNode, InfluitEdge > graphMouse;
	private DefaultVisualizationModel< InfluitNode, InfluitEdge > visualizationModel;
	private VisualizationViewer< InfluitNode, InfluitEdge > visualizationViewer;


	// TODO This is only an example and the InfluitPanel should of course be unrelated to Indago in any way!!!
	public InfluitPanel( final Dimension preferredSize, final boolean showMenu ) {
		super( new BorderLayout() );

		this.g = new DirectedSparseMultigraph< InfluitNode, InfluitEdge >();
		initJungGraph( preferredSize );

		loadPlugins();
		buildMenu();
		if ( showMenu ) {
			this.add( menuBar, BorderLayout.NORTH );
		}
	}

	/**
	 * @param preferredSize
	 */
	public void initJungGraph( final Dimension preferredSize ) {
		jungLayout = new FRLayout< InfluitNode, InfluitEdge >( this.g );

		visualizationModel = new DefaultVisualizationModel< InfluitNode, InfluitEdge >( jungLayout, preferredSize );
		visualizationViewer = new VisualizationViewer< InfluitNode, InfluitEdge >( visualizationModel, preferredSize );

		// this class will provide both label drawing and vertex shapes
		nodeShapeRenderer = new InfluitNodeShapeRenderer( visualizationViewer.getRenderContext() );

		// customize the render context
		visualizationViewer.getRenderContext().setVertexLabelTransformer(
				// this chains together Transformers so that the html tags
				// are prepended to the toString method output
				new ChainedTransformer< InfluitNode, String >( new Transformer[] { new ToStringLabeller< String >(), new Transformer< String, String >() {

					@Override
					public String transform( final String input ) {
						return "<html><center>" + input;
					}
				} } ) );
		visualizationViewer.getRenderContext().setVertexShapeTransformer( nodeShapeRenderer );
		visualizationViewer.getRenderContext().setVertexLabelRenderer( new DefaultVertexLabelRenderer( Color.red ) );
		visualizationViewer.getRenderContext().setEdgeDrawPaintTransformer( new ConstantTransformer( Color.black ) );
		visualizationViewer.getRenderContext().setEdgeStrokeTransformer( new ConstantTransformer( new BasicStroke( 2.5f ) ) );

		// customize the renderer
		visualizationViewer.getRenderer().setVertexRenderer( new GradientVertexRenderer< InfluitNode, InfluitEdge >( Color.white, Color.white.darker(), false ) );
		visualizationViewer.getRenderer().setVertexLabelRenderer( nodeShapeRenderer );

		visualizationViewer.setBackground( Color.white );

		// add a listener for ToolTips
		visualizationViewer.setVertexToolTipTransformer( new ToStringLabeller< InfluitNode >() );

		// Create a graph mouse and add it to the visualization component
		graphMouse = new DefaultModalGraphMouse< InfluitNode, InfluitEdge >();
		graphMouse.setMode( ModalGraphMouse.Mode.TRANSFORMING );
		visualizationViewer.setGraphMouse( graphMouse );
		// Add the mouses mode key listener to work it needs to be added to the visualization component
		visualizationViewer.addKeyListener( graphMouse.getModeKeyListener() );

		this.add( visualizationViewer, BorderLayout.CENTER );
	}

	/**
	 * Builds the menu structure that can be shown as part of the panel or can
	 * be used within the menu of the enclosing GUI.
	 */
	private void buildMenu() {
		this.menuBar = new JMenuBar();

		this.menuFile = new JMenu( MenuConstants.FILE_LABEL );
		menuFile.setMnemonic( MenuConstants.FILE_MNEMONIC );

		this.menuEdit = new JMenu( MenuConstants.EDIT_LABEL );
		menuEdit.setMnemonic( MenuConstants.EDIT_MNEMONIC );
		final JMenu temp = graphMouse.getModeMenu();
		temp.setText( "Mouse Mode" );
		temp.setPreferredSize( null );
		menuEdit.add( temp );

		this.menuNodes = new JMenu( MenuConstants.NODES_LABEL );
		menuNodes.setMnemonic( MenuConstants.NODES_MNEMONIC );
//
		this.menuWindow = new JMenu( MenuConstants.WINDOW_LABEL );

		this.menuHelp = new JMenu( MenuConstants.HELP_LABEL );
		menuHelp.setMnemonic( MenuConstants.HELP_MNEMONIC );

		menuBar.add( menuFile );
		menuBar.add( menuEdit );
		menuBar.add( menuNodes );
//		menuBar.add( menuWindow );
		menuBar.add( menuHelp );

		for ( final PluginInfo< InfluitNode > info : this.nodeInfos ) {
			final JMenuItem node = new JMenuItem( info.getLabel() );
			node.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( final ActionEvent evt ) {
					InfluitNode instance;
					try {
						instance = info.createInstance();
						addNode( instance );
						repaint();
					} catch ( final InstantiableException e ) {
						System.out.println( "ERROR: InfluitNode could not be instantiated!" );
						e.printStackTrace();
					}
				}
			} );
			menuNodes.add( node );
		}

		menuItemFileLoad = new JMenuItem( "Load Influit Network..." );
		menuItemFileLoad.addActionListener( this );
		menuFile.add( menuItemFileLoad );

		menuItemFileSave = new JMenuItem( "Save Influit Network..." );
		menuItemFileSave.addActionListener( this );
		menuFile.add( menuItemFileSave );

		menuItemHelpAbout = new JMenuItem( "About..." );
		menuItemHelpAbout.addActionListener( this );
		menuHelp.add( menuItemHelpAbout );
	}

	/**
	 * Loads all plugins of type InfluitNode and stores a List<PluginInfo> named
	 * <code>nodeInfos</code>.
	 */
	private void loadPlugins() {
		this.nodeInfos = GetNodes.getInfluitNodePlugins();
	}

	/**
	 * Controls the shape and size for each vertex.
	 */
	private final static class InfluitVertexShape< V extends InfluitNode, E extends InfluitEdge > extends AbstractVertexShapeTransformer< V > implements Transformer< V, Shape > {

		protected Graph< V, E > graph;

		public InfluitVertexShape( final Graph< V, E > graphIn ) {
			this.graph = graphIn;
		}

		@Override
		public Shape transform( final V v ) {
			return factory.getRoundRectangle( v );
		}
	}

	/**
	 * @param node
	 *            the InfluitNode to be added.
	 */
	public void addNode( final InfluitNode node ) {
		this.g.addVertex( node );
	}

	/**
	 * @param edge
	 *            intance of InfluitEdge to be added.
	 * @param nodeFrom
	 *            the source node adjacent to <code>edge</code>.
	 * @param nodeTo
	 *            the target node adjacent to <code>edge</code>.
	 */
	public void addEdge( final InfluitEdge edge, final InfluitNode nodeFrom, final InfluitNode nodeTo ) {
		this.g.addEdge( edge, nodeFrom, nodeTo );
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed( final ActionEvent e ) {
		if ( e.getSource().equals( this.menuItemHelpAbout ) ) {
			AboutDialog.show( this );
		} else
		if ( e.getSource().equals( this.menuItemFileLoad ) ) {
			// not yet implemented
		} else
		if ( e.getSource().equals( this.menuItemFileSave ) ) {
			// not yet implemented
		}
	}
}
