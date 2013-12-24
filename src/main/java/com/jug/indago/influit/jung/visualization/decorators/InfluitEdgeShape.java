/**
 * Taken and modified from JUNG.
 */
package com.jug.indago.influit.jung.visualization.decorators;

import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.EdgeIndexFunction;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.decorators.AbstractEdgeShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;


/**
 * An interface for decorators that return a <code>Shape</code> for a specified
 * edge.
 *
 * All edge shapes must be defined so that their endpoints are at
 * (0,0) and (1,0). They will be scaled, rotated and translated into
 * position by the PluggableRenderer.
 *
 * @author Florian Jug
 * @param <Edge>
 */
public class InfluitEdgeShape< V, E > extends EdgeShape< V, E > {

	/**
	 * An edge shape that renders as a straight line between
	 * the vertex endpoints.
	 */
	public static class Line< V, E > extends AbstractEdgeShapeTransformer< V, E > {

		/**
		 * Singleton instance of the Line2D edge shape
		 */
		private static Line2D instance = new Line2D.Float( 0.0f, 0.0f, 1.0f, 0.0f );

		/**
		 * Get the shape for this edge, returning either the
		 * shared instance or, in the case of self-loop edges, the
		 * SimpleLoop shared instance.
		 */
		@Override
		@SuppressWarnings( "unchecked" )
		public Shape transform( final Context< Graph< V, E >, E > context ) {
			final Graph< V, E > graph = context.graph;
			final E e = context.element;

			final Pair< V > endpoints = graph.getEndpoints( e );
			if ( endpoints != null ) {
				final boolean isLoop = endpoints.getFirst().equals( endpoints.getSecond() );
				if ( isLoop ) { return loop.transform( context ); }
			}
			return instance;
		}
	}

	/**
	 * An edge shape that renders as a bent-line between the
	 * vertex endpoints.
	 */
	public static class BentLine< V, E > extends AbstractEdgeShapeTransformer< V, E > implements IndexedRendering< V, E > {

		/**
		 * singleton instance of the BentLine shape
		 */
		private static GeneralPath instance = new GeneralPath();

		protected EdgeIndexFunction< V, E > parallelEdgeIndexFunction;

		@Override
		@SuppressWarnings( "unchecked" )
		public void setEdgeIndexFunction( final EdgeIndexFunction< V, E > parallelEdgeIndexFunction ) {
			this.parallelEdgeIndexFunction = parallelEdgeIndexFunction;
			loop.setEdgeIndexFunction( parallelEdgeIndexFunction );
		}

		/**
		 * @return the parallelEdgeIndexFunction
		 */
		@Override
		public EdgeIndexFunction< V, E > getEdgeIndexFunction() {
			return parallelEdgeIndexFunction;
		}


		/**
		 * Get the shape for this edge, returning either the
		 * shared instance or, in the case of self-loop edges, the
		 * Loop shared instance.
		 */
		@Override
		@SuppressWarnings( "unchecked" )
		public Shape transform( final Context< Graph< V, E >, E > context ) {
			final Graph< V, E > graph = context.graph;
			final E e = context.element;
			final Pair< V > endpoints = graph.getEndpoints( e );
			if ( endpoints != null ) {
				final boolean isLoop = endpoints.getFirst().equals( endpoints.getSecond() );
				if ( isLoop ) { return loop.transform( context ); }
			}

			int index = 1;
			if ( parallelEdgeIndexFunction != null ) {
				index = parallelEdgeIndexFunction.getIndex( graph, e );
//				System.out.println( index );
			}
			final float controlY = 0 + control_offset_increment * index;
			instance.reset();
			instance.moveTo( 0.0f, 0.0f );
			instance.lineTo( 0.5f, controlY );
			instance.lineTo( 1.0f, 1.0f );
			return instance;
		}

	}

	/**
	 * An edge shape that renders as a CubicCurve between vertex
	 * endpoints. The two control points are at
	 * (1/3*length, 2*controlY) and (2/3*length, controlY)
	 * giving a 'spiral' effect.
	 */
	public static class CubicCurve< V, E > extends AbstractEdgeShapeTransformer< V, E > implements IndexedRendering< V, E > {

		/**
		 * singleton instance of the CubicCurve edge shape
		 */
		private static CubicCurve2D instance = new CubicCurve2D.Float();

		protected EdgeIndexFunction< V, E > parallelEdgeIndexFunction;

		@Override
		@SuppressWarnings( "unchecked" )
		public void setEdgeIndexFunction( final EdgeIndexFunction< V, E > parallelEdgeIndexFunction ) {
			this.parallelEdgeIndexFunction = parallelEdgeIndexFunction;
			loop.setEdgeIndexFunction( parallelEdgeIndexFunction );
       }

		/**
		 * @return the parallelEdgeIndexFunction
		 */
		@Override
		public EdgeIndexFunction< V, E > getEdgeIndexFunction() {
			return parallelEdgeIndexFunction;
		}

		/**
		 * Get the shape for this edge, returning either the
		 * shared instance or, in the case of self-loop edges, the
		 * Loop shared instance.
		 */
		@Override
		@SuppressWarnings( "unchecked" )
		public Shape transform( final Context< Graph< V, E >, E > context ) {
			final Graph< V, E > graph = context.graph;
			final E e = context.element;
			final Pair< V > endpoints = graph.getEndpoints( e );
			if ( endpoints != null ) {
				final boolean isLoop = endpoints.getFirst().equals( endpoints.getSecond() );
				if ( isLoop ) { return loop.transform( context ); }
			}

			int index = 1;
			if ( parallelEdgeIndexFunction != null ) {
				index = parallelEdgeIndexFunction.getIndex( graph, e );
			}

			final float controlY = control_offset_increment + control_offset_increment * index;
			instance.setCurve( 0.0f, 0.0f, 0.33f, controlY, .66f, -.3 * controlY, 1.0f, 0.0f );
			return instance;
		}
	}
}

