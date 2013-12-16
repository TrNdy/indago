package com.jug.indago.influit.jung.visualization.control;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import com.jug.indago.influit.edges.InfluitEdge;
import com.jug.indago.influit.edges.InfluitEdgeFactory;
import com.jug.indago.influit.nodes.InfluitNode;
import com.jug.indago.influit.nodes.InfluitNodeVertexFactory;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * a plugin that uses popup menus to create vertices and edges within the
 * InfluitPanel.
 *
 * Adapted from EditingPopupGraphMousePlugin that comes with JUNG
 *
 * @author jug
 *
 */
public class InfluitPanelEditingPopupGraphMousePlugin extends AbstractPopupGraphMousePlugin {

	protected InfluitNodeVertexFactory vertexFactory;
	protected InfluitEdgeFactory edgeFactory;
	protected JPopupMenu popup = new JPopupMenu();

	public InfluitPanelEditingPopupGraphMousePlugin( final InfluitNodeVertexFactory vertexFactory, final InfluitEdgeFactory edgeFactory ) {
		this.vertexFactory = vertexFactory;
		this.edgeFactory = edgeFactory;
	}

	@Override
	@SuppressWarnings( { "unchecked", "serial" } )
	protected void handlePopup( final MouseEvent e ) {
		final VisualizationViewer< InfluitNode, InfluitEdge > vv = ( VisualizationViewer< InfluitNode, InfluitEdge > ) e.getSource();
		final Layout< InfluitNode, InfluitEdge > layout = vv.getGraphLayout();
		final Graph< InfluitNode, InfluitEdge > graph = layout.getGraph();
		final Point2D p = e.getPoint();
		final Point2D ivp = p;
		final GraphElementAccessor< InfluitNode, InfluitEdge > pickSupport = vv.getPickSupport();
		if ( pickSupport != null ) {

			popup.removeAll();

			final InfluitNode vertex = pickSupport.getVertex( layout, ivp.getX(), ivp.getY() );
			final InfluitEdge edge = pickSupport.getEdge( layout, ivp.getX(), ivp.getY() );
			final PickedState< InfluitNode > pickedVertexState = vv.getPickedVertexState();
			final PickedState< InfluitEdge > pickedEdgeState = vv.getPickedEdgeState();

			if ( vertex != null ) {
				final Set< InfluitNode > picked = pickedVertexState.getPicked();
				if ( picked.size() > 0 ) {
					if ( graph instanceof UndirectedGraph == false ) {
						final JMenu directedMenu = new JMenu( "Create Directed Edge" );
						popup.add( directedMenu );
						for ( final InfluitNode other : picked ) {
							directedMenu.add( new AbstractAction( "[" + other + "," + vertex + "]" ) {

								@Override
								public void actionPerformed( final ActionEvent e ) {
									graph.addEdge( edgeFactory.createGenericInfluitEdge( other, vertex ), other, vertex, EdgeType.DIRECTED );
									vv.repaint();
								}
							} );
						}
					}
//					if ( graph instanceof DirectedGraph == false ) {
//						final JMenu undirectedMenu = new JMenu( "Create Undirected Edge" );
//						popup.add( undirectedMenu );
//						for ( final InfluitNode other : picked ) {
//							undirectedMenu.add( new AbstractAction( "[" + other + "," + vertex + "]" ) {
//
//								@Override
//								public void actionPerformed( final ActionEvent e ) {
//									graph.addEdge( edgeFactory.create(), other, vertex );
//									vv.repaint();
//								}
//							} );
//						}
//					}
				}
				popup.add( new AbstractAction( "Delete Vertex" ) {

					@Override
					public void actionPerformed( final ActionEvent e ) {
						pickedVertexState.pick( vertex, false );
						graph.removeVertex( vertex );
						vv.repaint();
					}
				} );
			} else if ( edge != null ) {
				popup.add( new AbstractAction( "Delete Edge" ) {

					@Override
					public void actionPerformed( final ActionEvent e ) {
						pickedEdgeState.pick( edge, false );
						graph.removeEdge( edge );
						vv.repaint();
					}
				} );
			} else {
				final JMenu submenu = new JMenu( "Add..." );
				for ( final Component item : vertexFactory.getInfluitNodeSelectionPopup( e ).getComponents() ) {
					submenu.add( item );
				}
				popup.add( submenu );
			}
			if ( popup.getComponentCount() > 0 ) {
				popup.show( vv, e.getX(), e.getY() );
			}
		}
	}
}

