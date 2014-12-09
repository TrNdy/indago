package com.indago.segment;

import java.util.HashSet;

import net.imglib2.Dimensions;
import net.imglib2.Localizable;
import net.imglib2.newlabeling.NativeImgLabeling;
import net.imglib2.tree.Forest;
import net.imglib2.tree.TreeNode;
import net.imglib2.type.numeric.integer.IntType;


public class LabelingForestMod implements Forest< LabelingTreeNode >
{
	public static < T extends TreeNode< T > & Iterable< ? extends Localizable > >
	LabelingForestMod fromForest( final Forest< T > forest, final Dimensions dimensions )
	{
		return new LabelingForestBuilderMod< T >( forest, dimensions ).getLabelingForest();
	}

	private final NativeImgLabeling< Integer, IntType > labeling;

	private final HashSet< LabelingTreeNode > roots;

	public LabelingForestMod( final NativeImgLabeling< Integer, IntType > labeling, final HashSet< LabelingTreeNode > roots )
	{
		this.labeling = labeling;
		this.roots = roots;
	}

	public NativeImgLabeling< Integer, IntType > getLabeling()
	{
		return labeling;
	}

	@Override
	public HashSet< LabelingTreeNode > roots()
	{
		return roots;
	}
}
