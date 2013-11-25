/*
 * EditingGraphViewer.java
 * 
 * Created on March 8, 2007, 7:49 PM; Updated May 29, 2007
 * 
 * Copyright March 8, 2007 Grotto Networking
 */

package com.jug.indago.gui.samples;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

/**
 * 
 * @author Dr. Greg M. Bernstein
 */
public class EditingGraphViewer1 {

	Graph< Integer, String > g;
	int nodeCount, edgeCount;
	Factory< Integer > vertexFactory;
	Factory< String > edgeFactory;

	/** Creates a new instance of SimpleGraphView */
	public EditingGraphViewer1() {
		// Graph<V, E> where V is the type of the vertices and E is the type of the edges
		g = new SparseMultigraph< Integer, String >();
		nodeCount = 0;
		edgeCount = 0;
		vertexFactory = new Factory< Integer >() { // My vertex factory

			@Override
			public Integer create() {
				return nodeCount++;
			}
		};
		edgeFactory = new Factory< String >() { // My edge factory

			@Override
			public String create() {
				return "E" + edgeCount++;
			}
		};
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main( final String[] args ) {
		final EditingGraphViewer1 sgv = new EditingGraphViewer1();
		// Layout<V, E>, VisualizationViewer<V,E>
		final Layout< Integer, String > layout = new StaticLayout( sgv.g );
		layout.setSize( new Dimension( 300, 300 ) );
		final VisualizationViewer< Integer, String > vv = new VisualizationViewer< Integer, String >( layout );
		vv.setPreferredSize( new Dimension( 350, 350 ) );
		// Show vertex and edge labels
		vv.getRenderContext().setVertexLabelTransformer( new ToStringLabeller() );
		vv.getRenderContext().setEdgeLabelTransformer( new ToStringLabeller() );
		// Create a graph mouse and add it to the visualization viewer
		// Our Vertices are going to be Integer objects so we need an Integer factory
		final EditingModalGraphMouse gm = new EditingModalGraphMouse( vv.getRenderContext(), sgv.vertexFactory, sgv.edgeFactory );
		vv.setGraphMouse( gm );

		final JFrame frame = new JFrame( "Editing Graph Viewer 1" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.getContentPane().add( vv );

		// Let's add a menu for changing mouse modes
		final JMenuBar menuBar = new JMenuBar();
		final JMenu modeMenu = gm.getModeMenu();
		modeMenu.setText( "Mouse Mode" );
		modeMenu.setIcon( null ); // I'm using this in a main menu
		modeMenu.setPreferredSize( new Dimension( 80, 20 ) ); // Change the size so I can see the text

		menuBar.add( modeMenu );
		frame.setJMenuBar( menuBar );
		gm.setMode( ModalGraphMouse.Mode.EDITING ); // Start off in editing mode
		frame.pack();
		frame.setVisible( true );

	}

}