/* Copyright (c) 2008 FastJAX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.fastjax.util;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumbersTest {
  private static final Logger logger = LoggerFactory.getLogger(NumbersTest.class);

  public static class CompoundTest {
    private static final Random random = new Random();

    private static byte[] randomBytes(final int length) {
      final byte[] bytes = new byte[length];
      for (int i = 0; i < bytes.length; ++i)
        bytes[i] = (byte)random.nextInt();

      return bytes;
    }

    private static short[] randomShorts(final int length) {
      final short[] shorts = new short[length];
      for (int i = 0; i < shorts.length; ++i)
        shorts[i] = (short)random.nextInt();

      return shorts;
    }

    private static int[] randomInts(final int length) {
      final int[] ints = new int[length];
      for (int i = 0; i < ints.length; ++i)
        ints[i] = random.nextInt();

      return ints;
    }

    @Test
    public void testLongOfInts() {
      for (int i = 0; i < 10000; ++i) {
        final int[] expected = randomInts(2);
        final long encoded = Numbers.Compound.encode(expected[0], expected[1]);
        for (int j = 0; j < expected.length; ++j)
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Compound.dencodeInt(encoded, j));
      }
    }

    @Test
    public void testLongOfShorts() {
      for (int i = 0; i < 10000; ++i) {
        final short[] expected = randomShorts(4);
        final long encoded = Numbers.Compound.encode(expected[0], expected[1], expected[2], expected[3]);
        for (int j = 0; j < expected.length; ++j)
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Compound.dencodeShort(encoded, j));
      }
    }

    @Test
    public void testLongOfBytes() {
      for (int i = 0; i < 10000; ++i) {
        final byte[] expected = randomBytes(8);
        final long encoded = Numbers.Compound.encode(expected[0], expected[1], expected[2], expected[3], expected[4], expected[5], expected[6], expected[7]);
        for (int j = 0; j < expected.length; ++j)
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Compound.dencodeByte(encoded, j));
      }
    }

    @Test
    public void testIntOfShorts() {
      for (int i = 0; i < 10000; ++i) {
        final short[] expected = randomShorts(2);
        final int encoded = Numbers.Compound.encode(expected[0], expected[1]);
        for (int j = 0; j < expected.length; ++j)
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Compound.dencodeShort(encoded, j));
      }
    }

    @Test
    public void testIntOfBytes() {
      for (int i = 0; i < 10000; ++i) {
        final byte[] expected = randomBytes(4);
        final int encoded = Numbers.Compound.encode(expected[0], expected[1], expected[2], expected[3]);
        for (int j = 0; j < expected.length; ++j)
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Compound.dencodeByte(encoded, j));
      }
    }

    @Test
    public void testShortOfBytes() {
      for (int i = 0; i < 10000; ++i) {
        final byte[] expected = randomBytes(2);
        final short encoded = Numbers.Compound.encode(expected[0], expected[1]);
        for (int j = 0; j < expected.length; ++j)
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Compound.dencodeByte(encoded, j));
      }
    }
  }

  private static double testLogBigInteger(final int[] factors, final int[] exponents) {
    double l1 = 0;
    BigInteger bi = BigInteger.ONE;
    for (int i = 0; i < factors.length; ++i) {
      int exponent = exponents[i];
      int factor = factors[i];
      if (factor <= 1)
        continue;

      for (int n = 0; n < exponent; ++n)
        bi = bi.multiply(BigInteger.valueOf(factor));

      l1 += Math.log(factor) * exponent;
    }

    final double l2 = Numbers.log2(bi);
    final double err = Math.abs((l2 - l1) / l1);
    final int decdigits = (int) (l1 / Math.log(10) + 0.5);
    logger.info("e={} digitss={}", err, decdigits);
    return err;
  }

  @Test
  public void testManyTries() {
    final int tries = 100;

    final int[] f = {1, 1, 1, 1, 1};
    final int[] e = {1, 1, 1, 1, 1};
    final Random r = new Random();
    double maxerr = 0;
    for (int n = 0; n < tries; ++n) {
      for (int i = 0; i < f.length; ++i) {
        f[i] = r.nextInt(100000) + 2;
        e[i] = r.nextInt(1000) + 1;
      }

      final double err = testLogBigInteger(f, e);
      if (err > maxerr)
        maxerr = err;
    }

    logger.info("Max err: {}", maxerr);
  }

  @Test
  public void testParseNumber() {
    assertEquals(2.5, Numbers.parseNumber("2 1/2"), 0);
    assertEquals(2.75, Numbers.parseNumber("2 3/4"), 0);

    assertEquals(0, Numbers.parseNumber("0"), 0);
    assertEquals(299792458, Numbers.parseNumber(" 299792458"), 0);
    assertEquals(-299792458, Numbers.parseNumber("-299792458 "), 0);
    assertEquals(3.14159265, Numbers.parseNumber(" 3.14159265"), 0);
    assertEquals(-3.14159265, Numbers.parseNumber("-3.14159265"), 0);
    assertEquals(6.022E23, Numbers.parseNumber("6.022E23 "), 0);
    assertEquals(-6.022E23, Numbers.parseNumber(" -6.022E23"), 0);
    assertEquals(6.626068E-34, Numbers.parseNumber(" 6.626068E-34"), 0);
    assertEquals(-6.626068E-34, Numbers.parseNumber("-6.626068E-34 "), 0);

    assertEquals(Double.NaN, Numbers.parseNumber(null), 0);
    assertEquals(Double.NaN, Numbers.parseNumber(""), 0);
    assertEquals(Double.NaN, Numbers.parseNumber(" "), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("not number"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("3.14.15"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("29-97924"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("-29-97924"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("2 997924"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("-29-979.24"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("-2.9-979.24"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("6.022 E23 "), 0);
    assertEquals(Double.NaN, Numbers.parseNumber(" -6.022E 23"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("-6.626068E--34 "), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("-6.626068E-3-4 "), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("-6.626068E-3.4"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("-6.626E068E-34"), 0);
  }

  @Test
  public void testIsNumber() {
    assertTrue(Numbers.isNumber("0"));
    assertTrue(Numbers.isNumber(" 299792458"));
    assertTrue(Numbers.isNumber("-299792458 "));
    assertTrue(Numbers.isNumber(" 3.14159265"));
    assertTrue(Numbers.isNumber("-3.14159265"));
    assertTrue(Numbers.isNumber("6.022E23 "));
    assertTrue(Numbers.isNumber(" -6.022E23"));
    assertTrue(Numbers.isNumber(" 6.626068E-34"));
    assertTrue(Numbers.isNumber("-6.626068E-34 "));
    assertTrue(Numbers.isNumber("-6.626068E-34 24/49"));
    assertTrue(Numbers.isNumber("3/5"));

    assertFalse(Numbers.isNumber(null));
    assertFalse(Numbers.isNumber(""));
    assertFalse(Numbers.isNumber(" "));
    assertFalse(Numbers.isNumber("not number"));
    assertFalse(Numbers.isNumber("3.14.15"));
    assertFalse(Numbers.isNumber("29-97924"));
    assertFalse(Numbers.isNumber("-29-97924"));
    assertFalse(Numbers.isNumber("2 997924"));
    assertFalse(Numbers.isNumber("-29-979.24"));
    assertFalse(Numbers.isNumber("-2.9-979.24"));
    assertFalse(Numbers.isNumber("6.022 E23 "));
    assertFalse(Numbers.isNumber(" -6.022E 23"));
    assertFalse(Numbers.isNumber("-6.626068E--34 "));
    assertFalse(Numbers.isNumber("-6.626068E-3-4 "));
    assertFalse(Numbers.isNumber("-6.626068E-3.4"));
    assertFalse(Numbers.isNumber("-6.626E068E-34"));
  }

  @Test
  public void testToString() {
    assertEquals("0.00833333333333", Numbers.toString(0.008333333333330742, 14));
    assertEquals("0.00833333333334", Numbers.toString(0.008333333333339323, 14));
    assertEquals("0.008333333333", Numbers.toString(0.008333333333000000, 14));
  }

  @Test
  public void testUnsignedByte() {
    final byte value = Byte.MAX_VALUE;
    assertEquals(value, Numbers.Unsigned.toSigned(Numbers.Unsigned.toUnsigned(value)));
  }

  @Test
  public void testUnsignedShort() {
    final short value = Short.MAX_VALUE;
    assertEquals(value, Numbers.Unsigned.toSigned(Numbers.Unsigned.toUnsigned(value)));
  }

  @Test
  public void testUnsignedInt() {
    final int value = Integer.MAX_VALUE;
    assertEquals(value, Numbers.Unsigned.toSigned(Numbers.Unsigned.toUnsigned(value)));
  }

  @Test
  public void testUnsignedLong() {
    final long value = Long.MAX_VALUE;
    assertEquals(BigInteger.valueOf(value), Numbers.Unsigned.toSigned(Numbers.Unsigned.toUnsigned(value)));
  }

  @Test
  public void testUnsignedBigInteger() {
    final BigInteger value = new BigInteger("18446744073709551615");
    assertEquals(value, Numbers.Unsigned.toSigned(Numbers.Unsigned.toUnsigned(value)));
  }

  private static final Class<?>[] numberTypes = new Class<?>[] {Byte.class, Short.class, Integer.class, Float.class, Double.class, Long.class};

  @Test
  @SuppressWarnings("unchecked")
  public void testValueOf() {
    for (int i = 0; i < numberTypes.length; ++i) {
      for (int j = 0; j < numberTypes.length; ++j) {
        final Class<? extends Number> from = (Class<? extends Number>)numberTypes[i];
        final Class<? extends Number> to = (Class<? extends Number>)numberTypes[j];
        final Number value = Numbers.valueOf(111, from);
        assertEquals(value, Numbers.valueOf(Numbers.valueOf(value, to), from));
      }
    }
  }

  @Test
  public void testPrecision() {
    assertEquals(3, Numbers.precision(349));
    assertEquals(1, Numbers.precision(3));
    assertEquals(5, Numbers.precision(34329));
    assertEquals(10, Numbers.precision(Integer.MIN_VALUE));
    assertEquals(1, Numbers.precision(-1));
    assertEquals(5, Numbers.precision(-13423));
    assertEquals(12, Numbers.precision(349349349349l));
    assertEquals(19, Numbers.precision(BigInteger.valueOf(4389429384493848239l)));
    assertEquals(19, Numbers.precision(BigInteger.valueOf(-4389429384493848239l)));
    assertEquals(19, Numbers.precision(Long.MIN_VALUE));
    assertEquals(19, Numbers.precision(new BigDecimal("-4389429384.493848239")));
  }

  @Test
  public void testTrailingZeroes() {
    assertEquals(3, Numbers.trailingZeroes(349000));
    assertEquals(1, Numbers.trailingZeroes(3490l));
    assertEquals(1, Numbers.trailingZeroes(10));
    assertEquals(0, Numbers.trailingZeroes(1));
    assertEquals(1, Numbers.trailingZeroes(-10));
    assertEquals(4, Numbers.trailingZeroes(-14320000l));
  }

  @Test
  public void testToBigDecimal() {
    assertEquals(BigDecimal.ONE, Numbers.toBigDecimal((short)1));
    assertEquals(BigDecimal.ONE, Numbers.toBigDecimal(1));
    assertEquals(BigDecimal.ONE, Numbers.toBigDecimal(1l));
    assertEquals(BigDecimal.ONE, Numbers.toBigDecimal(1f));
    assertEquals(BigDecimal.ONE, Numbers.toBigDecimal(1d));
    assertEquals(BigDecimal.ONE, Numbers.toBigDecimal(BigInteger.ONE));
  }

  @Test
  public void testStripTrailingZeros() {
    assertNull(Numbers.stripTrailingZeros(null));
    assertEquals("5.4", Numbers.stripTrailingZeros("5.4000"));
    assertEquals("500", Numbers.stripTrailingZeros("500"));
    assertEquals("0500", Numbers.stripTrailingZeros("0500"));
    assertEquals("0.1", Numbers.stripTrailingZeros("0.100"));
    assertEquals("0", Numbers.stripTrailingZeros("0.000"));
    assertEquals("1", Numbers.stripTrailingZeros("1.000"));
    assertEquals("xxx", Numbers.stripTrailingZeros("xxx"));
    assertEquals("xxx00", Numbers.stripTrailingZeros("xxx00"));
  }

  @Test
  public void testAverage() {
    assertEquals(BigDecimal.valueOf(3), Numbers.average(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO));
    assertEquals(BigDecimal.valueOf(3), Numbers.average(BigInteger.ONE, BigInteger.ONE, BigInteger.TEN, BigInteger.ZERO));
    assertEquals(3d, Numbers.average((byte)1, (byte)1, (byte)10, (byte)0), 0.000000001);
    assertEquals(3d, Numbers.average((short)1, (short)1, (short)10, (short)0), 0.000000001);
    assertEquals(3d, Numbers.average(1, 1, 10, 0), 0.000000001);
    assertEquals(3d, Numbers.average(1l, 1l, 10l, 0l), 0.000000001);
    assertEquals(3d, Numbers.average((byte)1, 1l, 10, 0d), 0.000000001);
  }
}