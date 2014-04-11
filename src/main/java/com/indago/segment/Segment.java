package com.indago.segment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.labeling.LabelRegions;
import net.imglib2.labeling.LabelRegions.LabelStatistics;
import net.imglib2.newroi.Regions;
import net.imglib2.type.logic.BoolType;

public class Segment implements HypothesisTreeNode< Segment >, Iterable< Localizable >
{
	private final LabelStatistics statistics;

	private final RandomAccessibleInterval< BoolType > mask;

	/**
	 * child nodes in the {@link SegmentForest}.
	 */
	private final ArrayList< Segment > children;

	/**
	 * parent node in the {@link SegmentForest}.
	 */
	private Segment parent;

	private ArrayList< Segment > descendants;

	private ArrayList< Segment > ancestors;

	private ArrayList< Segment > conflicting;

	protected < T extends Comparable< T > > Segment( final LabelRegions< T > regions, final T label )
	{
		statistics = regions.getStatistics( label );
		mask = regions.getLabelRegion( label );
		children = new ArrayList< Segment >();
		parent = null;
		descendants = null;
		ancestors = null;
		conflicting = null;
	}

	protected void addChild( final Segment segment )
	{
		children.add( segment );
		segment.parent = this;
		segment.invalidateCachedAncestors();
		invalidateCachedDescendants();
	}

	@Override
	public Segment getParent()
	{
		return parent;
	}

	@Override
	public ArrayList< Segment > getChildren()
	{
		return children;
	}

	@Override
	public Collection< Segment > getConflictingHypotheses()
	{
		if ( conflicting == null )
		{
			conflicting = new ArrayList< Segment >();
			conflicting.addAll( getAncestors() );
			conflicting.addAll( getDescendants() );
		}
		return conflicting;
	}

	public long getArea()
	{
		return statistics.getArea();
	}

	public Localizable getCenterOfMass()
	{
		return statistics.getCenterOfMass();
	}

	@Override
	public Iterator< Localizable > iterator()
	{
		final Cursor< BoolType > c = Regions.iterable( mask ).cursor();
		return new Iterator< Localizable >()
		{
			@Override
			public boolean hasNext()
			{
				return c.hasNext();
			}

			@Override
			public Localizable next()
			{
				c.fwd();
				return c;
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	private void invalidateCachedAncestors()
	{
		ancestors = null;
		conflicting = null;
		for ( final Segment c : children )
			c.invalidateCachedAncestors();
	}

	private void invalidateCachedDescendants()
	{
		descendants = null;
		conflicting = null;
		if ( parent != null )
			parent.invalidateCachedDescendants();
	}

	private ArrayList< Segment > getDescendants()
	{
		if ( descendants == null )
		{
			descendants = new ArrayList< Segment >();
			for ( final Segment c : children )
			{
				descendants.add( c );
				descendants.addAll( c.getDescendants() );
			}
		}
		return descendants;
	}

	private ArrayList< Segment > getAncestors()
	{
		if ( ancestors == null )
		{
			ancestors = new ArrayList< Segment >();
			if ( parent != null )
			{
				ancestors.add( parent );
				ancestors.addAll( parent.getAncestors() );
			}
		}
		return ancestors;
	}
}
