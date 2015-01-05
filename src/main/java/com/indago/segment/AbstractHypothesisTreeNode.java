package com.indago.segment;

import java.util.ArrayList;
import java.util.Collection;

import net.imglib2.tree.TreeNode;

public abstract class AbstractHypothesisTreeNode< T extends AbstractHypothesisTreeNode< T > > implements TreeNode< T > {

	/**
	 * child nodes in the {@link Forest}.
	 */
	private final ArrayList< T > children;

	/**
	 * parent node in the {@link SegmentForest}.
	 */
	protected T parent;

	private ArrayList< T > descendants;

	private ArrayList< T > ancestors;

	private ArrayList< T > conflicting;

	private ArrayList< T > leaves;

	protected AbstractHypothesisTreeNode() {
		children = new ArrayList< T >();
		parent = null;
		descendants = null;
		ancestors = null;
		conflicting = null;
	}

	@Override
	public T getParent() {
		return parent;
	}

	@Override
	public ArrayList< T > getChildren() {
		return children;
	}

	@SuppressWarnings( "unchecked" )
	protected void addChild( final T node ) {
		children.add( node );
		node.parent = ( T ) this;
		node.invalidateCachedAncestors();
		invalidateCachedDescendants();
	}

	/**
	 * @return a list of all ancestors and descendants of this node.
	 */
	public Collection< T > getConflictingHypotheses() {
		if ( conflicting == null ) {
			conflicting = new ArrayList< T >();
			conflicting.addAll( getAncestors() );
			conflicting.addAll( getDescendants() );
		}
		return conflicting;
	}

	/**
	 * Invalidate {@link #ancestors} and {@link #conflicting} for this node and
	 * all of its descendants.
	 */
	protected void invalidateCachedAncestors() {
		ancestors = null;
		conflicting = null;
		for ( final T c : children )
			c.invalidateCachedAncestors();
	}

	/**
	 * Invalidate {@link #descendants}, {@link #leaves}, and
	 * {@link #conflicting} for this node and all of its ancestors.
	 */
	protected void invalidateCachedDescendants() {
		descendants = null;
		leaves = null;
		conflicting = null;
		if ( parent != null ) parent.invalidateCachedDescendants();
	}

	protected ArrayList< T > getDescendants() {
		if ( descendants == null ) {
			descendants = new ArrayList< T >();
			for ( final T c : children ) {
				descendants.add( c );
				descendants.addAll( c.getDescendants() );
			}
		}
		return descendants;
	}

	protected ArrayList< T > getAncestors() {
		if ( ancestors == null ) {
			ancestors = new ArrayList< T >();
			if ( parent != null ) {
				ancestors.add( parent );
				ancestors.addAll( parent.getAncestors() );
			}
		}
		return ancestors;
	}

	protected ArrayList< T > getLeaves() {
		if ( leaves == null ) {
			leaves = new ArrayList< T >();
			for ( final T t : getDescendants() )
				if ( t.getChildren().size() == 0 ) leaves.add( t );
		}
		return leaves;
	}
}
