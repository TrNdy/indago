package com.indago.segment;

import java.util.Collection;

import net.imglib2.tree.TreeNode;

public interface HypothesisTreeNode< T extends HypothesisTreeNode< T > > extends TreeNode< T >
{
	public Collection< T > getConflictingHypotheses();
}
