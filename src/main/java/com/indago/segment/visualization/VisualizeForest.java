package com.indago.segment.visualization;

import java.util.ArrayList;
import java.util.Iterator;

import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.tree.Forest;
import net.imglib2.tree.TreeNode;
import net.imglib2.type.Type;

public class VisualizeForest
{
	public static < C extends Type< C >, T extends TreeNode< T > & Iterable< Localizable > > void colorLevels(
			final Forest< T > forest,
			final Iterator< C > colors,
			final RandomAccessibleInterval< C > img )
	{
		ArrayList< T > currentLevel = new ArrayList< T >( forest.roots() );
		int level = 0;
		final RandomAccess< C > a = img.randomAccess();
		while ( ! currentLevel.isEmpty() )
		{
			final ArrayList< T > nextLevel = new ArrayList< T >();
			final C color = colors.next();
			for ( final T component : currentLevel )
			{
				for ( final Localizable l : component )
				{
					a.setPosition( l );
					a.get().set( color );
				}
				nextLevel.addAll( component.getChildren() );
			}
			currentLevel = nextLevel;
			++level;
		}
	}

	public static < C extends Type< C >, T extends TreeNode< T > & Iterable< Localizable > > void colorNodes(
			final Forest< T > forest,
			final Iterator< C > colors,
			final RandomAccessibleInterval< C > img )
	{
		ArrayList< T > currentLevel = new ArrayList< T >( forest.roots() );
		final RandomAccess< C > a = img.randomAccess();
		while ( ! currentLevel.isEmpty() )
		{
			final ArrayList< T > nextLevel = new ArrayList< T >();
			for ( final T component : currentLevel )
			{
				final C color = colors.next();
				for ( final Localizable l : component )
				{
					a.setPosition( l );
					a.get().set( color );
				}
				nextLevel.addAll( component.getChildren() );
			}
			currentLevel = nextLevel;
		}
	}

}
