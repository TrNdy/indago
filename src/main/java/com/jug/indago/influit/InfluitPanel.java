/**
 *
 */
package com.jug.indago.influit;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ChainedTransformer;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.scijava.plugin.PluginInfo;

import com.jug.indago.GetNodes;
import com.jug.indago.gui.menu.MenuConstants;
import com.jug.indago.influit.edges.InfluitEdge;
import com.jug.indago.influit.nodes.InfluitNode;
import com.jug.indago.influit.transformer.InfluitNodeShapeRenderer;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationModel;
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
public class InfluitPanel extends JPanel {

	private static final long serialVersionUID = -6410785901686147594L;

	private final Graph< InfluitNode, InfluitEdge > g;

	private List< PluginInfo< InfluitNode >> nodeInfos;

	private JMenuBar menuBar;

	private JMenu menuFile;

	private JMenu menuEdit;

	private JMenu menuNodes;

	private JMenu menuWindow;

	private JMenu menuHelp;

	// TODO This is only an example and the InfluitPanel should of course be unrelated to Indago in any way!!!
	public InfluitPanel( final Dimension preferredSize, final boolean showMenu ) {
		super( new BorderLayout() );

		loadPlugins();
		buildMenu();
		if ( showMenu ) {
			this.add( menuBar, BorderLayout.NORTH );
		}

		this.g = new DirectedSparseMultigraph< InfluitNode, InfluitEdge >();

		final Layout< InfluitNode, InfluitEdge > layout = new FRLayout< InfluitNode, InfluitEdge >( this.g );

		final VisualizationModel< InfluitNode, InfluitEdge > visualizationModel = new DefaultVisualizationModel< InfluitNode, InfluitEdge >( layout, preferredSize );
		final VisualizationViewer< InfluitNode, InfluitEdge > vv = new VisualizationViewer< InfluitNode, InfluitEdge >( visualizationModel, preferredSize );

		// this class will provide both label drawing and vertex shapes
		final InfluitNodeShapeRenderer vlasr = new InfluitNodeShapeRenderer( vv.getRenderContext() );

		// customize the render context
		vv.getRenderContext().setVertexLabelTransformer(
				// this chains together Transformers so that the html tags
				// are prepended to the toString method output
				new ChainedTransformer< InfluitNode, String >( new Transformer[] { new ToStringLabeller< String >(), new Transformer< String, String >() {

					@Override
					public String transform( final String input ) {
						return "<html><center>" + input;
					}
				} } ) );
		vv.getRenderContext().setVertexShapeTransformer( vlasr );
		vv.getRenderContext().setVertexLabelRenderer( new DefaultVertexLabelRenderer( Color.red ) );
		vv.getRenderContext().setEdgeDrawPaintTransformer( new ConstantTransformer( Color.black ) );
		vv.getRenderContext().setEdgeStrokeTransformer( new ConstantTransformer( new BasicStroke( 2.5f ) ) );

		// customize the renderer
		vv.getRenderer().setVertexRenderer( new GradientVertexRenderer< InfluitNode, InfluitEdge >( Color.white, Color.white.darker(), false ) );
		vv.getRenderer().setVertexLabelRenderer( vlasr );

		vv.setBackground( Color.white );

		// add a listener for ToolTips
		vv.setVertexToolTipTransformer( new ToStringLabeller< InfluitNode >() );

		// Create a graph mouse and add it to the visualization component
		final DefaultModalGraphMouse< InfluitNode, InfluitEdge > gm = new DefaultModalGraphMouse< InfluitNode, InfluitEdge >();
		gm.setMode( ModalGraphMouse.Mode.PICKING );
		vv.setGraphMouse( gm );
		// Add the mouses mode key listener to work it needs to be added to the visualization component
		vv.addKeyListener( gm.getModeKeyListener() );

		this.add( vv, BorderLayout.CENTER );
	}

	/**
	 * Builds the menu structure that can be shown as part of the panel or can
	 * be used within the menu of the enclosing GUI.
	 */
	private void buildMenu() {
		this.menuBar = new JMenuBar();
		this.menuFile = new JMenu( MenuConstants.FILE_LABEL );
		this.menuEdit = new JMenu( MenuConstants.EDIT_LABEL );
		this.menuNodes = new JMenu( MenuConstants.NODES_LABEL );
		this.menuWindow = new JMenu( MenuConstants.WINDOW_LABEL );
		this.menuHelp = new JMenu( MenuConstants.HELP_LABEL );
		menuBar.add( menuFile );
		menuBar.add( menuEdit );
		menuBar.add( menuNodes );
		menuBar.add( menuWindow );
		menuBar.add( menuHelp );

		for ( final PluginInfo< InfluitNode > info : this.nodeInfos ) {
//			InfluitNode plugin;
//			try {
//				plugin = info.createInstance();
//			} catch ( final InstantiableException e ) {
//				System.err.println( String.format( "Plugin could not be instatiated -- %s", info.getClassName() ) );
//				e.printStackTrace();
//			}
//			System.out.println( info );
//			System.out.println( info.getLabel() );
			menuNodes.add( new JMenuItem( info.getLabel() ) );
		}

//		final SwingJMenuBarCreator menuBarCreator = new SwingJMenuBarCreator();
//		menuBarCreator.createMenus( root, this.menuBar );
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

//        protected AffineTransform scaleTransform = new AffineTransform();

		public InfluitVertexShape( final Graph< V, E > graphIn ) {
			this.graph = graphIn;
//			setSizeTransformer( new Transformer< V, Integer >() {
//
//				@Override
//				public Integer transform( final V v ) {
//					if ( scale )
//						return ( int ) ( voltages.transform( v ) * 30 ) + 20;
//					else
//						return 20;
//
//				}
//			} );
//			setAspectRatioTransformer( new Transformer< V, Float >() {
//
//				@Override
//				public Float transform( final V v ) {
//					if ( stretch ) {
//						return ( float ) ( graph.inDegree( v ) + 1 ) / ( graph.outDegree( v ) + 1 );
//					} else {
//						return 1.0f;
//					}
//				}
//			} );
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
}
