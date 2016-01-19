/**
 *
 */
package com.indago.fg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.indago.models.SegmentationModel;
import com.indago.models.TrackingModel;
import com.indago.models.segments.ConflictSet;
import com.indago.models.segments.SegmentVar;

import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

/**
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class FactorGraphFactory {

	public static Pair< UnaryCostConstraintGraph, AssignmentMapper< Variable, SegmentVar > >
			createFactorGraph( final SegmentationModel segmentationModel ) {

		final Map< SegmentVar, Variable > varmap = new HashMap<>();

		final Collection< Variable > variables = varmap.values();
		final ArrayList< Factor > unaries = new ArrayList<>();
		final ArrayList< Factor > constraints = new ArrayList<>();

		for ( final SegmentVar segvar : segmentationModel.getSegments() ) {
			final Variable var = Variables.binary();
			varmap.put( segvar, var );
			unaries.add( Factors.unary( var, 0.0, segvar.getCost() ) );
		}

		for ( final ConflictSet conflictSet : segmentationModel.getConflictSets() ) {
			final ArrayList< Variable > vars = new ArrayList<>();
			for ( final SegmentVar segvar : conflictSet )
				vars.add( varmap.get( segvar ) );
			constraints.add( Factors.atMostOneConstraint( vars ) );
		}

		final UnaryCostConstraintGraph fg = new UnaryCostConstraintGraph( variables, unaries, constraints );
		final AssignmentMapper< Variable, SegmentVar > mapper = new AssignmentMapper< Variable, SegmentVar >() {
			@Override
			public Assignment< SegmentVar > map( final Assignment< Variable > assignment ) {
				return new Assignment< SegmentVar >() {
					@Override
					public boolean isAssigned( final SegmentVar variable ) {
						return assignment.isAssigned( varmap.get( variable ) );
					}

					@Override
					public int getAssignment( final SegmentVar variable ) {
						return assignment.getAssignment( varmap.get( variable ) );
					}
				};
			}
		};

		return new ValuePair<>( fg, mapper );
	}

	public static Pair< UnaryCostConstraintGraph, AssignmentMapper< Variable, SegmentVar > > createFactorGraph(
			final TrackingModel trackingModel ) {

		final Map< SegmentVar, Variable > varmap = new HashMap< >();

		final Collection< Variable > variables = varmap.values();
		final ArrayList< Factor > unaries = new ArrayList< >();
		final ArrayList< Factor > constraints = new ArrayList< >();

		for ( final SegmentationModel segmentationModel : trackingModel.getTimepoints() ) {
			final Pair< UnaryCostConstraintGraph, AssignmentMapper< Variable, SegmentVar > > frameResult =
					FactorGraphFactory.createFactorGraph( segmentationModel );
			final UnaryCostConstraintGraph frameFG = frameResult.getA();
			final AssignmentMapper< Variable, SegmentVar > frameAM = frameResult.getB();
		}

		final UnaryCostConstraintGraph fg = new UnaryCostConstraintGraph( variables, unaries, constraints );
		final AssignmentMapper< Variable, SegmentVar > mapper = new AssignmentMapper< Variable, SegmentVar >() {
			@Override
			public Assignment< SegmentVar > map( final Assignment< Variable > assignment ) {
				return new Assignment< SegmentVar >() {

					@Override
					public boolean isAssigned( final SegmentVar variable ) {
						return assignment.isAssigned( varmap.get( variable ) );
					}

					@Override
					public int getAssignment( final SegmentVar variable ) {
						return assignment.getAssignment( varmap.get( variable ) );
					}
				};
			}
		};

		return new ValuePair< >( fg, mapper );
	}
}
