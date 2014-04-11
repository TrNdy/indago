package com.indago.segment;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.tree.TreeNode;

public class LabelingTreeNode implements TreeNode< LabelingTreeNode >
{
	private final Integer label;

	private final ArrayList< LabelingTreeNode > children;

	private LabelingTreeNode parent;

	public LabelingTreeNode( final Integer label )
	{
		this.label = label;
		children = new ArrayList< LabelingTreeNode >();
	}

	public void addChild( final LabelingTreeNode node )
	{
		children.add( node );
		node.parent = this;
	}

	public void removeChild( final LabelingTreeNode node )
	{
		children.remove( node );
		node.parent = null;
	}

	@Override
	public LabelingTreeNode getParent()
	{
		return parent;
	}

	@Override
	public List< LabelingTreeNode > getChildren()
	{
		return children;
	}

	public Integer getLabel()
	{
		return label;
	}
}