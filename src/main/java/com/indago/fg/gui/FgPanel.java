/**
 *
 */
package com.indago.fg.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;

import com.indago.fg.FactorGraph;
import com.indago.fg.factor.Factor;
import com.indago.fg.io.Scalar;
import com.indago.fg.variable.Variable;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;

/**
 * @author jug
 */
public class FgPanel extends JPanel {

	private final Graph< FgNode, FgEdge > g;
	private Layout< FgNode, FgEdge > jungLayout;
	private DefaultVisualizationModel< FgNode, FgEdge > visualizationModel;
	private VisualizationViewer< FgNode, FgEdge > visualizationViewer;

	public FgPanel( final FactorGraph fg ) {
		super( new BorderLayout() );

		g = new UndirectedSparseGraph< FgNode, FgEdge >();

		System.out.println( fg.getVariables().size() + ", " + fg.getFactors().size() );
		buildJungGraph( fg );
		initJungGraph();
	}

	/**
	 * @param fg
	 */
	private void buildJungGraph( final FactorGraph fg ) {
		for ( final Variable< ? > v : fg.getVariables() ) {
			g.addVertex( v );
		}
		for ( final Factor< ?, ?, ? > f : fg.getFactors() ) {
			g.addVertex( f );
			for ( final Variable< ? > v : f.getVariables() ) {
				g.addEdge( new FgEdge(), f, v );
			}
		}
	}

	/**
	 * @param preferredSize
	 */
	private void initJungGraph() {
		jungLayout = new SpringLayout< FgNode, FgEdge >( this.g );
//		jungLayout = new StaticLayout< FgNode, FgEdge >( this.g );

		visualizationModel = new DefaultVisualizationModel< FgNode, FgEdge >( jungLayout, new Dimension( 800, 600 ) );
		visualizationViewer = new VisualizationViewer< FgNode, FgEdge >( visualizationModel, new Dimension( 800, 600 ) );

		final RenderContext< FgNode, FgEdge > c = visualizationViewer.getRenderContext();
		c.setVertexFillPaintTransformer( new Transformer< FgNode, Paint >()
		{
			@Override
			public Paint transform( final FgNode input )
			{
				return Variable.class.isInstance( input ) ? Color.blue : Color.red;
			}
		} );
		c.setVertexShapeTransformer( new Transformer< FgNode, Shape >()
		{
			private final Shape variableShape = new Rectangle( -10, -10, 20, 20 );

			private final Shape defaultShape = new Ellipse2D.Double( -10, -10, 20, 20 );

			@Override
			public Shape transform( final FgNode input )
			{
				return Variable.class.isInstance( input ) ? variableShape : defaultShape;
			}
		} );

		this.add( visualizationViewer, BorderLayout.CENTER );
	}

	/**
	 * To test this panel...
	 *
	 * @param args
	 */
	public static void main( final String[] args ) {
		final JFrame guiFrame = new JFrame( "FgPanel - test" );

		// Set window-closing action...
		guiFrame.addWindowListener( new WindowAdapter() {

			@Override
			public void windowClosing( final WindowEvent we ) {
				System.exit( 0 );
			}
		} );

		guiFrame.getContentPane().setLayout( new BorderLayout() );

//		final String fn = "src/main/resources/sopnet-test-minimal.txt";
		final String fn = "src/main/resources/min-gap.txt";
//		final String fn = "/Users/pietzsch/Desktop/sopnet-subproblems/factor_graph.txt";

		try {
			guiFrame.getContentPane().add( new FgPanel( Scalar.load( fn ) ), BorderLayout.CENTER );
		} catch ( final FileNotFoundException e ) {
			e.printStackTrace();
		} catch ( final IOException e ) {
			e.printStackTrace();
		}
		guiFrame.setSize( 800, 600 );
		guiFrame.setVisible( true );
	}
}
