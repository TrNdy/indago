/**
 *
 */
package com.indago.segment.fg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.indago.fg.FactorGraph;
import com.indago.fg.domain.BooleanFunctionDomain;
import com.indago.fg.factor.BooleanFactor;
import com.indago.fg.factor.Factor;
import com.indago.fg.function.BooleanConflictConstraint;
import com.indago.fg.function.BooleanTensorTable;
import com.indago.fg.function.Function;
import com.indago.segment.ConflictGraph;
import com.indago.segment.Segment;
import com.indago.segment.SegmentCosts;

/**
 * @author jug
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class FactorGraphFactory {

	public static < T extends Segment > FactorGraph createFromConflictGraph( final Collection< ? extends T > segments, final ConflictGraph< T > conflicts, final SegmentCosts segmentCosts ) {
		final Collection< ? extends Collection< T > > cliques = conflicts.getConflictGraphCliques();

		int factorId = 0;
		int functionId = 0;

		final ArrayList< Function< ?, ? > > functions = new ArrayList<>( segments.size() + 1 );
		final ArrayList< Factor< ?, ?, ? > > factors = new ArrayList<>( segments.size() + cliques.size() );
		final ArrayList< SegmentHypothesisVariable< T > > variables = new ArrayList<>( segments.size() );

		final HashMap< T, SegmentHypothesisVariable< T > > segmentVariableDict = new HashMap<>( segments.size() );
		for ( final T segment : segments ) {
			final SegmentHypothesisVariable< T > var = new SegmentHypothesisVariable<>( segment );
			segmentVariableDict.put( segment, var );
			variables.add( var );
		}

		final BooleanConflictConstraint conflictConstraint = new BooleanConflictConstraint();
		functions.add( conflictConstraint );

		for ( final Collection< T > clique : cliques ) {
			final BooleanFunctionDomain domain = new BooleanFunctionDomain( clique.size() );
			final BooleanFactor factor = new BooleanFactor( domain, factorId++ );
			factor.setFunction( conflictConstraint );
			int i = 0;
			for ( final T segment : clique ) {
				final SegmentHypothesisVariable< T > sv = segmentVariableDict.get( segment );
				factor.setVariable( i, sv );
				i++;
			}
			factors.add( factor );
		}

		final BooleanFunctionDomain domain = new BooleanFunctionDomain( 1 );
		for ( final T segment : segments ) {
			final double[] entries = new double[] { 0.0, segmentCosts.getCost( segment ) };
			final BooleanTensorTable btt = new BooleanTensorTable( domain, entries, functionId++ );
			final BooleanFactor factor = new BooleanFactor( domain, factorId++ );
			factor.setFunction( btt );
			factor.setVariable( 0, segmentVariableDict.get( segment ) );

			functions.add( btt );
			factors.add( factor );
		}

		return new FactorGraph( variables, factors, functions );
	}
}
