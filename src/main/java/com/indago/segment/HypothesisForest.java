package com.indago.segment;

import net.imglib2.tree.Forest;

public interface HypothesisForest< T extends HypothesisTreeNode< T > > extends Forest< T >, ConflictGraph< T >
{
}
