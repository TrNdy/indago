/**
 *
 */
package com.jug.indago.influit;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import com.jug.indago.influit.edges.GenericInfluitEdge;
import com.jug.indago.influit.edges.InfluitEdge;
import com.jug.indago.influit.nodes.FilteredComponentTreeNode;
import com.jug.indago.influit.nodes.InfluitNode;
import com.jug.indago.influit.nodes.ij.ImagePlusNode;
import com.jug.indago.influit.nodes.imglib2.HyperSlicerLoopNode;
import com.jug.indago.model.IndagoModel;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

/**
 * @author jug
 */
public class InfluitPanel extends JPanel {

	private static final long serialVersionUID = -6410785901686147594L;

	private final Graph< InfluitNode, InfluitEdge > g;

	public InfluitPanel(final IndagoModel model) {
		super( new BorderLayout() );

		this.g = new DirectedSparseMultigraph< InfluitNode, InfluitEdge >();

		final ImagePlusNode imgPlusNode = new ImagePlusNode( model.getImgPlus() );
		final HyperSlicerLoopNode slicerLoopNode = new HyperSlicerLoopNode ( 4 );
		final FilteredComponentTreeNode compTreeNode = new FilteredComponentTreeNode ();

		final GenericInfluitEdge< ImagePlusNode, HyperSlicerLoopNode > edge1 = new GenericInfluitEdge< ImagePlusNode, HyperSlicerLoopNode >( imgPlusNode, slicerLoopNode );
		final GenericInfluitEdge< HyperSlicerLoopNode, FilteredComponentTreeNode > edge2 = new GenericInfluitEdge< HyperSlicerLoopNode, FilteredComponentTreeNode >( slicerLoopNode, compTreeNode );

		g.addVertex( imgPlusNode );
		g.addVertex( slicerLoopNode );
		g.addVertex( compTreeNode );
		g.addEdge( edge1, imgPlusNode, slicerLoopNode );
		g.addEdge( edge2, slicerLoopNode, compTreeNode );

		final Layout< InfluitNode, InfluitEdge > layout = new CircleLayout< InfluitNode, InfluitEdge >( this.g );
		layout.setSize( new Dimension( 300, 300 ) );
		final VisualizationViewer< InfluitNode, InfluitEdge > vv = new VisualizationViewer< InfluitNode, InfluitEdge >( layout );
		vv.setPreferredSize( new Dimension( 350, 350 ) );
		// Show vertex and edge labels
		vv.getRenderContext().setVertexLabelTransformer( new ToStringLabeller< InfluitNode >() );
		vv.getRenderContext().setEdgeLabelTransformer( new ToStringLabeller< InfluitEdge >() );
		// Create a graph mouse and add it to the visualization component
		final DefaultModalGraphMouse< InfluitNode, InfluitEdge > gm = new DefaultModalGraphMouse< InfluitNode, InfluitEdge >();
		gm.setMode( ModalGraphMouse.Mode.PICKING );
		vv.setGraphMouse( gm );
		// Add the mouses mode key listener to work it needs to be added to the visualization component
		vv.addKeyListener( gm.getModeKeyListener() );

		this.add( vv, BorderLayout.CENTER );
	}
}
