/* Copyright (c) 2008 lib4j
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

package org.lib4j.util;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.lib4j.util.Numbers;
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
    for (int i = 0; i < factors.length; i++) {
      int exponent = exponents[i];
      int factor = factors[i];
      if (factor <= 1)
        continue;

      for (int n = 0; n < exponent; n++)
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
    for (int n = 0; n < tries; n++) {
      for (int i = 0; i < f.length; i++) {
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
    Assert.assertEquals(2.5, Numbers.parseNumber("2 1/2"), 0);
    Assert.assertEquals(2.75, Numbers.parseNumber("2 3/4"), 0);

    Assert.assertEquals(0, Numbers.parseNumber("0"), 0);
    Assert.assertEquals(299792458, Numbers.parseNumber(" 299792458"), 0);
    Assert.assertEquals(-299792458, Numbers.parseNumber("-299792458 "), 0);
    Assert.assertEquals(3.14159265, Numbers.parseNumber(" 3.14159265"), 0);
    Assert.assertEquals(-3.14159265, Numbers.parseNumber("-3.14159265"), 0);
    Assert.assertEquals(6.022E23, Numbers.parseNumber("6.022E23 "), 0);
    Assert.assertEquals(-6.022E23, Numbers.parseNumber(" -6.022E23"), 0);
    Assert.assertEquals(6.626068E-34, Numbers.parseNumber(" 6.626068E-34"), 0);
    Assert.assertEquals(-6.626068E-34, Numbers.parseNumber("-6.626068E-34 "), 0);

    Assert.assertEquals(Double.NaN, Numbers.parseNumber(null), 0);
    Assert.assertEquals(Double.NaN, Numbers.parseNumber(""), 0);
    Assert.assertEquals(Double.NaN, Numbers.parseNumber(" "), 0);
    Assert.assertEquals(Double.NaN, Numbers.parseNumber("not number"), 0);
    Assert.assertEquals(Double.NaN, Numbers.parseNumber("3.14.15"), 0);
    Assert.assertEquals(Double.NaN, Numbers.parseNumber("29-97924"), 0);
    Assert.assertEquals(Double.NaN, Numbers.parseNumber("-29-97924"), 0);
    Assert.assertEquals(Double.NaN, Numbers.parseNumber("2 997924"), 0);
    Assert.assertEquals(Double.NaN, Numbers.parseNumber("-29-979.24"), 0);
    Assert.assertEquals(Double.NaN, Numbers.parseNumber("-2.9-979.24"), 0);
    Assert.assertEquals(Double.NaN, Numbers.parseNumber("6.022 E23 "), 0);
    Assert.assertEquals(Double.NaN, Numbers.parseNumber(" -6.022E 23"), 0);
    Assert.assertEquals(Double.NaN, Numbers.parseNumber("-6.626068E--34 "), 0);
    Assert.assertEquals(Double.NaN, Numbers.parseNumber("-6.626068E-3-4 "), 0);
    Assert.assertEquals(Double.NaN, Numbers.parseNumber("-6.626068E-3.4"), 0);
    Assert.assertEquals(Double.NaN, Numbers.parseNumber("-6.626E068E-34"), 0);
  }

  @Test
  public void testIsNumber() {
    Assert.assertTrue(Numbers.isNumber("0"));
    Assert.assertTrue(Numbers.isNumber(" 299792458"));
    Assert.assertTrue(Numbers.isNumber("-299792458 "));
    Assert.assertTrue(Numbers.isNumber(" 3.14159265"));
    Assert.assertTrue(Numbers.isNumber("-3.14159265"));
    Assert.assertTrue(Numbers.isNumber("6.022E23 "));
    Assert.assertTrue(Numbers.isNumber(" -6.022E23"));
    Assert.assertTrue(Numbers.isNumber(" 6.626068E-34"));
    Assert.assertTrue(Numbers.isNumber("-6.626068E-34 "));
    Assert.assertTrue(Numbers.isNumber("-6.626068E-34 24/49"));
    Assert.assertTrue(Numbers.isNumber("3/5"));

    Assert.assertFalse(Numbers.isNumber(null));
    Assert.assertFalse(Numbers.isNumber(""));
    Assert.assertFalse(Numbers.isNumber(" "));
    Assert.assertFalse(Numbers.isNumber("not number"));
    Assert.assertFalse(Numbers.isNumber("3.14.15"));
    Assert.assertFalse(Numbers.isNumber("29-97924"));
    Assert.assertFalse(Numbers.isNumber("-29-97924"));
    Assert.assertFalse(Numbers.isNumber("2 997924"));
    Assert.assertFalse(Numbers.isNumber("-29-979.24"));
    Assert.assertFalse(Numbers.isNumber("-2.9-979.24"));
    Assert.assertFalse(Numbers.isNumber("6.022 E23 "));
    Assert.assertFalse(Numbers.isNumber(" -6.022E 23"));
    Assert.assertFalse(Numbers.isNumber("-6.626068E--34 "));
    Assert.assertFalse(Numbers.isNumber("-6.626068E-3-4 "));
    Assert.assertFalse(Numbers.isNumber("-6.626068E-3.4"));
    Assert.assertFalse(Numbers.isNumber("-6.626E068E-34"));
  }

  @Test
  public void testToString() {
    Assert.assertEquals("0.00833333333333", Numbers.toString(0.008333333333330742, 14));
    Assert.assertEquals("0.00833333333334", Numbers.toString(0.008333333333339323, 14));
    Assert.assertEquals("0.008333333333", Numbers.toString(0.008333333333000000, 14));
  }

  @Test
  public void testUnsignedByte() {
    final byte value = Byte.MAX_VALUE;
    Assert.assertEquals(value, Numbers.Unsigned.toSigned(Numbers.Unsigned.toUnsigned(value)));
  }

  @Test
  public void testUnsignedShort() {
    final short value = Short.MAX_VALUE;
    Assert.assertEquals(value, Numbers.Unsigned.toSigned(Numbers.Unsigned.toUnsigned(value)));
  }

  @Test
  public void testUnsignedInt() {
    final int value = Integer.MAX_VALUE;
    Assert.assertEquals(value, Numbers.Unsigned.toSigned(Numbers.Unsigned.toUnsigned(value)));
  }

  @Test
  public void testUnsignedLong() {
    final long value = Long.MAX_VALUE;
    Assert.assertEquals(BigInteger.valueOf(value), Numbers.Unsigned.toSigned(Numbers.Unsigned.toUnsigned(value)));
  }

  @Test
  public void testUnsignedBigInteger() {
    final BigInteger value = new BigInteger("18446744073709551615");
    Assert.assertEquals(value, Numbers.Unsigned.toSigned(Numbers.Unsigned.toUnsigned(value)));
  }

  private static final Class<?>[] numberTypes = new Class<?>[] {Byte.class, Short.class, Integer.class, Float.class, Double.class, Long.class};

  @Test
  @SuppressWarnings("unchecked")
  public void testCast() {
    for (int i = 0; i < numberTypes.length; i++) {
      for (int j = 0; j < numberTypes.length; j++) {
        final Class<? extends Number> from = (Class<? extends Number>)numberTypes[i];
        final Class<? extends Number> to = (Class<? extends Number>)numberTypes[j];
        final Number value = Numbers.cast(111, from);
        Assert.assertEquals(value, Numbers.cast(Numbers.cast(value, to), from));
      }
    }
  }

  @Test
  public void testPrecision() {
    Assert.assertEquals(3, Numbers.precision(349));
    Assert.assertEquals(1, Numbers.precision(3));
    Assert.assertEquals(5, Numbers.precision(34329));
    Assert.assertEquals(10, Numbers.precision(Integer.MIN_VALUE));
    Assert.assertEquals(1, Numbers.precision(-1));
    Assert.assertEquals(5, Numbers.precision(-13423));
    Assert.assertEquals(12, Numbers.precision(349349349349l));
    Assert.assertEquals(19, Numbers.precision(BigInteger.valueOf(4389429384493848239l)));
    Assert.assertEquals(19, Numbers.precision(BigInteger.valueOf(-4389429384493848239l)));
    Assert.assertEquals(19, Numbers.precision(Long.MIN_VALUE));
    Assert.assertEquals(19, Numbers.precision(new BigDecimal("-4389429384.493848239")));
  }

  @Test
  public void testTrailingZeroes() {
    Assert.assertEquals(3, Numbers.trailingZeroes(349000));
    Assert.assertEquals(1, Numbers.trailingZeroes(3490l));
    Assert.assertEquals(1, Numbers.trailingZeroes(10));
    Assert.assertEquals(0, Numbers.trailingZeroes(1));
    Assert.assertEquals(1, Numbers.trailingZeroes(-10));
    Assert.assertEquals(4, Numbers.trailingZeroes(-14320000l));
  }

  @Test
  public void testToBigDecimal() {
    Assert.assertEquals(BigDecimal.ONE, Numbers.toBigDecimal((short)1));
    Assert.assertEquals(BigDecimal.ONE, Numbers.toBigDecimal(1));
    Assert.assertEquals(BigDecimal.ONE, Numbers.toBigDecimal(1l));
    Assert.assertEquals(BigDecimal.ONE, Numbers.toBigDecimal(1f));
    Assert.assertEquals(BigDecimal.ONE, Numbers.toBigDecimal(1d));
    Assert.assertEquals(BigDecimal.ONE, Numbers.toBigDecimal(BigInteger.ONE));
  }

  @Test
  public void testAverage() {
    Assert.assertEquals(BigDecimal.valueOf(3), Numbers.average(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO));
    Assert.assertEquals(BigDecimal.valueOf(3), Numbers.average(BigInteger.ONE, BigInteger.ONE, BigInteger.TEN, BigInteger.ZERO));
    Assert.assertEquals(3d, Numbers.average((byte)1, (byte)1, (byte)10, (byte)0), 0.000000001);
    Assert.assertEquals(3d, Numbers.average((short)1, (short)1, (short)10, (short)0), 0.000000001);
    Assert.assertEquals(3d, Numbers.average(1, 1, 10, 0), 0.000000001);
    Assert.assertEquals(3d, Numbers.average(1l, 1l, 10l, 0l), 0.000000001);
    Assert.assertEquals(3d, Numbers.average((byte)1, 1l, 10, 0d), 0.000000001);
  }
}