package com.indago.data.segmentation.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.imagej.ops.Op;
import net.imagej.ops.OpEnvironment;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imglib2.type.Type;

public class FeatureSet< I, O extends Type< O > >
{
	private final List< Class< ? extends Op > > features;

	private final Map< Class< ? extends Op >, UnaryComputerOp< I, O > > computers;

	private final Map< String, O > namedOutputs;

	private final List< O > outputs;

	public FeatureSet(
			final OpEnvironment ops,
			final O outputTypeInstance,
			final Class< I > inType,
			final Class< ? extends Op >... features )
	{
		this.features = new ArrayList<>( Arrays.asList( features ) );

		computers = new HashMap<>();
		outputs = new ArrayList<>();
		namedOutputs = new HashMap<>();

		@SuppressWarnings( "unchecked" )
		final Class< O > outType = ( Class< O > ) outputTypeInstance.getClass();
		for ( final Class< ? extends Op > feature : features )
		{
			final UnaryComputerOp< I, O > computer = Computers.unary( ops, feature, outType, inType );
			final O output = outputTypeInstance.createVariable();
			outputs.add( output );
			namedOutputs.put( feature.getName(), output );
			computer.setOutput( output );
			computers.put( feature, computer );
		}
	}

	public void compute( final I input )
	{
		for ( final UnaryComputerOp< I, O > computer : computers.values() )
			computer.compute1( input, computer.out() );
	}

	public List< O > getOutputs()
	{
		return outputs;
	}

	public Map< String, O > getNamedOutputs()
	{
		return namedOutputs;
	}

	public O getOutput( final Class< ? extends Op > feature )
	{
		return computers.get( feature ).out();
	}

	public List< Class< ? extends Op > > getFeatureOps()
	{
		return features;
	}
}
