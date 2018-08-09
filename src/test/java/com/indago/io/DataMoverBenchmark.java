package com.indago.io;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@State(Scope.Benchmark)
public class DataMoverBenchmark {

	private final long[] dimensions = {1000, 1000};

	Img<FloatType> floats = ArrayImgs.floats(dimensions);
	Img<FloatType> floats2 = ArrayImgs.floats(dimensions);
	Img<DoubleType > doubles = ArrayImgs.doubles(dimensions);
	Img<DoubleType > doubles2 = ArrayImgs.doubles(dimensions);
	Img<UnsignedShortType > shorts = ArrayImgs.unsignedShorts(dimensions);
	Img<UnsignedShortType > shorts2 = ArrayImgs.unsignedShorts(dimensions);
	Img<IntType > ints = ArrayImgs.ints(dimensions);

	@Benchmark
	public void benchmarkConvertAndCopy() throws Exception {
		DataMover.convertAndCopy(floats, doubles);
		DataMover.convertAndCopy(shorts, ints);
		DataMover.convertAndCopy(shorts, floats);
	}

	@Benchmark
	public void benchmarkCopy() {
		DataMover.copy(floats, floats2);
		DataMover.copy(doubles, doubles2);
		DataMover.copy(shorts, shorts2);
	}
	public static void main( final String... args ) throws RunnerException
	{
		final Options opt = new OptionsBuilder()
				.include( DataMover.class.getSimpleName() )
				.forks( 0 )
				.warmupIterations( 20 )
				.measurementIterations( 20 )
				.warmupTime( TimeValue.milliseconds( 100 ) )
				.measurementTime( TimeValue.milliseconds( 100 ) )
				.build();
		new Runner( opt ).run();
	}
}
