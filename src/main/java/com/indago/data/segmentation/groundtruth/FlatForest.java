package com.indago.data.segmentation.groundtruth;

import com.indago.data.segmentation.groundtruth.ImageRegions.ImageRegion;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.imglib2.Localizable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.tree.Forest;
import net.imglib2.algorithm.tree.TreeNode;
import net.imglib2.type.Type;

public class FlatForest implements Forest< FlatForest.Node > {

	private final HashSet< Node > nodes;

	public < T extends Type< T > > FlatForest( final RandomAccessibleInterval< T > img, final T background ) {
		nodes = new HashSet< Node >();
		final ImageRegions< T > imageRegions = new ImageRegions< T >( img );
		for ( final T value : imageRegions.getImageValues() )
			if ( !value.equals( background ) )
				nodes.add( new Node( imageRegions.getImageRegion( value ) ) );
	}

	public static class Node implements TreeNode< Node >, Iterable< Localizable > {

		private static final ArrayList< Node > emptyChildren = new ArrayList< Node >();

		private final ImageRegion pixels;

		public Node(final ImageRegion pixels) {
			this.pixels = pixels;
		}

		@Override
		public Iterator< Localizable > iterator() {
			return pixels.iterator();
		}

		public long size()
		{
			return pixels.size();
		}

		@Override
		public Node getParent() {
			return null;
		}

		@Override
		public List< Node > getChildren() {
			return emptyChildren;
		}
	}

	@Override
	public Set< Node > roots() {
		return nodes;
	}
}
