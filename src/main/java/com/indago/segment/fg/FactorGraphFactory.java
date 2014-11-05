/**
 * 
 */
package com.indago.segment.fg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.indago.fg.FactorGraph;
import com.indago.fg.domain.BooleanFunctionDomain;
import com.indago.fg.factor.BooleanFactor;
import com.indago.fg.factor.Factor;
import com.indago.fg.function.BooleanConflictConstraint;
import com.indago.fg.function.Function;
import com.indago.segment.Segment;
import com.indago.segment.SegmentCosts;
import com.indago.segment.SegmentMultiForest;

/**
 * @author jug
 */
public class FactorGraphFactory {

	/**
	 * @param segmentMultiForest
	 * @param costs
	 */
	public static FactorGraph createFromSegmentMultiForest( final SegmentMultiForest segmentMultiForest, final SegmentCosts segmentCosts ) {
		return createFromSegmentMultiForest( segmentMultiForest, segmentCosts, true );
	}

	public static FactorGraph createFromSegmentMultiForest( final SegmentMultiForest segmentMultiForest, final SegmentCosts segmentCosts, final boolean doCompactConstraints ) {
		int factorId = 0;

		final Map< Segment, SegmentHypothesisVariable > segmentVariableDict = VariableSetFactory.getSegmentMultiForestVariableDictionary( segmentMultiForest );
		final Collection< SegmentHypothesisVariable > variablesCollection = segmentVariableDict.values();
		final List< SegmentHypothesisVariable > variables = new ArrayList< SegmentHypothesisVariable >( variablesCollection );

		final List< Function< ?, ? > > functions = new ArrayList< Function< ?, ? > >();
		final BooleanConflictConstraint function = new BooleanConflictConstraint();
		functions.add( function );

		final List< Factor< ?, ?, ? > > factors = new ArrayList< Factor< ?, ?, ? > >();

		Collection< ? extends Collection< Segment >> cliques;
		if ( doCompactConstraints ) {
			cliques = segmentMultiForest.getConflictGraphCliques();
		} else {
			cliques = segmentMultiForest.getConflictGraphEdges();
		}

		for ( final Collection< Segment > clique : cliques ) {
			final BooleanFunctionDomain domain = new BooleanFunctionDomain( clique.size() );
			final BooleanFactor factor = new BooleanFactor( domain, factorId++ );
			factor.setFunction( function );
			int i = 0;
			for ( final Segment segment : clique ) {
				final SegmentHypothesisVariable sv = segmentVariableDict.get( segment );
				factor.setVariable( i, sv );
				i++;
			}
			factors.add( factor );
		}

		return new FactorGraph( variables, factors, functions );
	}
}
