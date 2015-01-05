package com.indago.segment;

import java.util.HashSet;

import net.imglib2.Dimensions;
import net.imglib2.Localizable;
import net.imglib2.newlabeling.ImgLabeling;
import net.imglib2.tree.Forest;
import net.imglib2.tree.TreeNode;
import net.imglib2.type.numeric.integer.IntType;


public class LabelingForest implements Forest< LabelingTreeNode >
{
	public static < T extends TreeNode< T > & Iterable< ? extends Localizable > >
	LabelingForest fromForest( final Forest< T > forest, final ImgLabeling< Integer, IntType > sharedLabeling )
	{
		return new LabelingForestBuilder< T >( forest, sharedLabeling, null ).getLabelingForest();
	}

	public static < T extends TreeNode< T > & Iterable< ? extends Localizable > >
	LabelingForest fromForest( final Forest< T > forest, final Dimensions dimensions )
	{
		return new LabelingForestBuilder< T >( forest, null, dimensions ).getLabelingForest();
	}

	private final ImgLabeling< Integer, IntType > labeling;

	private final HashSet< LabelingTreeNode > roots;

	public LabelingForest( final ImgLabeling< Integer, IntType > labeling, final HashSet< LabelingTreeNode > roots )
	{
		this.labeling = labeling;
		this.roots = roots;
	}

	public ImgLabeling< Integer, IntType > getLabeling()
	{
		return labeling;
	}

	@Override
	public HashSet< LabelingTreeNode > roots()
	{
		return roots;
	}
}
