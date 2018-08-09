package com.indago.io;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.list.ListImg;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DataMoverTest {

	@Test
	public void testCopy() {
		Img<IntType> in = ArrayImgs.ints(new int[]{42}, 1);
		Img<IntType> out = ArrayImgs.ints(1);
		DataMover.copy(in, (RandomAccessibleInterval<IntType>) out);
		assertEquals(42, out.firstElement().get());
	}

	@Test
	public void testCopyWithConverter() {
		Img<IntType> in = ArrayImgs.ints(new int[]{42}, 1);
		Img<FloatType> out = ArrayImgs.floats(1);
		DataMover.copy(in, (IterableInterval<FloatType>) out, (i,o) -> o.set(i.get()));
		assertEquals(42, out.firstElement().get(), 0.0f);
	}

	@Test
	public void testAdd() {
		Img<IntType> in = ArrayImgs.ints(new int[]{42}, 1);
		Img<IntType> out = ArrayImgs.ints(new int[]{4}, 1);
		DataMover.add(in, (RandomAccessibleInterval<IntType>) out);
		assertEquals(46, out.firstElement().get());
	}

	@Test
	public void testConvertAndCopyIntTypeToIntType() {
		testConvertAndCopy(new IntType(42), new IntType(42));
	}

	@Test
	public void testConvertAndCopyFloatTypeToDoubleType() {
		testConvertAndCopy(new FloatType(42), new DoubleType(42));
	}

	@Test
	public void testConvertAndCopyIntegerTypeToIntType() {
		testConvertAndCopy(new ByteType((byte) 42), new IntType(42));
	}

	@Test
	public void testConvertAndCopyFloatTypeToIntType() {
		testConvertAndCopy(new FloatType(42), new IntType(42));
	}

	@Test
	public void testConvertAndCopyRealTypeToFloatType() {
		testConvertAndCopy(new IntType(42), new FloatType(42));
	}

	@Test
	public void testConvertAndCopyRealTypeToDoubleType() {
		testConvertAndCopy(new IntType(42), new DoubleType(42));
	}

	@Test
	public void testConvertAndCopyFloatTypeToARGBType() {
		testConvertAndCopy(new FloatType(0.5f), new ARGBType(ARGBType.rgba(128, 128, 128, 255)));
	}

	@Test
	public void testConvertAndCopyUnsignedShortTypeToFloatType() {
		testConvertAndCopy(new UnsignedShortType(42), new FloatType(42));
	}

	@Test
	public void testConvertAndCopyUnsignedShortTypeToIntType() {
		testConvertAndCopy(new UnsignedShortType(42), new IntType(42));
	}

	@Test
	public void testConvertAndCopyUnsignedShortTypeToDoubleType() {
		testConvertAndCopy(new UnsignedShortType(42), new DoubleType(42));
	}

	private <S extends RealType<S>, T extends NativeType<T>> void testConvertAndCopy(S input, T expected) {
		Img<S> in = new ListImg<>(Collections.singleton(input), 1);
		Img<T> out = new ArrayImgFactory<>(expected.createVariable()).create(1);
		try {
			DataMover.convertAndCopy(in, out);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		assertTrue(expected.valueEquals(out.randomAccess().get()));
	}
}
