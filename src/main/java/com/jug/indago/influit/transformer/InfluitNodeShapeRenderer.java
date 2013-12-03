/**
 *
 */
package com.jug.indago.influit.transformer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import com.jug.indago.influit.edges.InfluitEdge;
import com.jug.indago.influit.nodes.InfluitNode;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelRenderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import edu.uci.ics.jung.visualization.util.VertexShapeFactory;


/**
 * @author jug
 */
/**
 * Renders Vertex Labels, but can also supply Shapes for vertices.
 * This has the effect of making the vertex label the actual vertex
 * shape. The user will probably want to center the vertex label
 * on the vertex location.
 *
 * @author Tom Nelson
 *
 * @param <InfluitNode>
 * @param <InfluitEdge>
 */
public class InfluitNodeShapeRenderer implements Renderer.VertexLabel< InfluitNode, InfluitEdge >, Transformer< InfluitNode, Shape > {

	protected Map< InfluitNode, Shape > shapes = new HashMap< InfluitNode, Shape >();
	protected RenderContext< InfluitNode, InfluitEdge > rc;

	public InfluitNodeShapeRenderer( final RenderContext< InfluitNode, InfluitEdge > rc ) {
		this.rc = rc;
	}

	public Component prepareRenderer( final RenderContext< InfluitNode, InfluitEdge > rc, final VertexLabelRenderer graphLabelRenderer, final Object value, final boolean isSelected, final InfluitNode vertex ) {
		return rc.getVertexLabelRenderer().< InfluitNode >getVertexLabelRendererComponent( rc.getScreenDevice(), value, rc.getVertexFontTransformer().transform( vertex ), isSelected, vertex );
	}

	/**
	 * Labels the specified vertex with the specified label.
	 * Uses the font specified by this instance's
	 * <code>VertexFontFunction</code>. (If the font is unspecified, the
	 * existing
	 * font for the graphics context is used.) If vertex label centering
	 * is active, the label is centered on the position of the vertex; otherwise
	 * the label is offset slightly.
	 */
	@Override
	public void labelVertex( final RenderContext< InfluitNode, InfluitEdge > rc, final Layout< InfluitNode, InfluitEdge > layout, final InfluitNode v, final String label ) {
		final Graph< InfluitNode, InfluitEdge > graph = layout.getGraph();
		if ( rc.getVertexIncludePredicate().evaluate( Context.< Graph< InfluitNode, InfluitEdge >, InfluitNode >getInstance( graph, v ) ) == false ) { return; }
		final GraphicsDecorator g = rc.getGraphicsContext();
		final Component component = prepareRenderer( rc, rc.getVertexLabelRenderer(), label, rc.getPickedVertexState().isPicked( v ), v );
		final Dimension d = component.getPreferredSize();

		final int h_offset = -d.width / 2;
		final int v_offset = -d.height / 2;

		Point2D p = layout.transform( v );
		p = rc.getMultiLayerTransformer().transform( Layer.LAYOUT, p );

		final int x = ( int ) p.getX();
		final int y = ( int ) p.getY();

		g.draw( component, rc.getRendererPane(), x + h_offset, y + v_offset, d.width, d.height, true );

		final Dimension size = component.getPreferredSize();
		final Rectangle bounds = new Rectangle( -size.width / 2 - 2, -size.height / 2 - 2, size.width + 4, size.height );
		shapes.put( v, bounds );
	}

	@Override
	public Shape transform( final InfluitNode v ) {
		final Component component = prepareRenderer( rc, rc.getVertexLabelRenderer(), rc.getVertexLabelTransformer().transform( v ), rc.getPickedVertexState().isPicked( v ), v );
		final Dimension size = component.getPreferredSize();
		final Rectangle bounds = new Rectangle( -size.width / 2 - 5, -size.height / 2 - 5, size.width + 10, size.height + 10 );

		final VertexShapeFactory< InfluitNode > factory = new VertexShapeFactory< InfluitNode >();
		final RoundRectangle2D shape = factory.getRoundRectangle( v );
		final double arc = Math.min( Math.min( bounds.getWidth(), bounds.getHeight() ) / 1.5, 10.0 );
		shape.setRoundRect( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), arc, arc );

//		return bounds;
		return shape;
	}

	@Override
	public Renderer.VertexLabel.Position getPosition() {
		return Renderer.VertexLabel.Position.CNTR;
	}

	@Override
	public Renderer.VertexLabel.Positioner getPositioner() {
		return new Positioner() {

			@Override
			public Renderer.VertexLabel.Position getPosition( final float x, final float y, final Dimension d ) {
				return Renderer.VertexLabel.Position.CNTR;
			}
		};
	}

	@Override
	public void setPosition( final Renderer.VertexLabel.Position position ) {
		//noop
	}

	@Override
	public void setPositioner( final Renderer.VertexLabel.Positioner positioner ) {
		//noop
	}
}
