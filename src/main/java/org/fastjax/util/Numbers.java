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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

public final class Numbers {
  public static final BigInteger LONG_MIN_VALUE = BigInteger.valueOf(Long.MIN_VALUE);
  public static final BigInteger LONG_MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE);

  public static class Unsigned {
    public static final BigInteger UNSIGNED_LONG_MAX_VALUE = new BigInteger("18446744073709551615");

    public static short toSigned(final byte unsigned) {
      return (short)(unsigned - Byte.MIN_VALUE);
    }

    public static int toSigned(final short unsigned) {
      return unsigned - Short.MIN_VALUE;
    }

    public static long toSigned(final int unsigned) {
      return (long)unsigned - Integer.MIN_VALUE;
    }

    public static BigInteger toSigned(final long unsigned) {
      return BigInteger.valueOf(unsigned).subtract(LONG_MIN_VALUE);
    }

    public static BigInteger toSigned(final byte[] unsigned) {
      return new BigInteger(1, unsigned);
    }

    public static BigInteger toSigned(final byte[] unsigned, final int off, final int len) {
      return new BigInteger(1, unsigned, off, len);
    }

    public static byte toUnsigned(final byte signed) {
      if (signed < 0)
        throw new IllegalArgumentException("signed < 0");

      return (byte)(signed + Byte.MIN_VALUE);
    }

    public static short toUnsigned(final short signed) {
      if (signed < 0)
        throw new IllegalArgumentException("signed < 0: " + signed);

      return (short)(signed + Short.MIN_VALUE);
    }

    public static int toUnsigned(final int signed) {
      if (signed < 0)
        throw new IllegalArgumentException("signed < 0: " + signed);

      return signed + Integer.MIN_VALUE;
    }

    public static long toUnsigned(final long signed) {
      if (signed < 0)
        throw new IllegalArgumentException("signed < 0: " + signed);

      return signed + Long.MIN_VALUE;
    }

    public static byte[] toUnsigned(final BigInteger signed) {
      if (signed.signum() == -1)
        throw new IllegalArgumentException("signed < 0: " + signed);

      final byte[] bytes = signed.toByteArray();
      if (bytes[0] != 0)
        return bytes;

      final byte[] trimmed = new byte[bytes.length - 1];
      System.arraycopy(bytes, 1, trimmed, 0, trimmed.length);
      return trimmed;
    }

    private Unsigned() {
    }
  }

  private static final Comparator<Number> comparator = new Comparator<>() {
    @Override
    public int compare(final Number o1, final Number o2) {
      if (o1 == null)
        return o2 == null ? 0 : 1;

      if (o2 == null)
        return -1;

      if (o1 instanceof BigDecimal) {
        if (o2 instanceof BigDecimal)
          return ((BigDecimal)o1).compareTo((BigDecimal)o2);

        if (o2 instanceof BigInteger)
          return ((BigDecimal)o1).compareTo(new BigDecimal((BigInteger)o2));

        if (o2 instanceof Byte || o2 instanceof Short || o2 instanceof Integer || o2 instanceof Long)
          return ((BigDecimal)o1).compareTo(BigDecimal.valueOf(o2.longValue()));

        return ((BigDecimal)o1).compareTo(BigDecimal.valueOf(o2.doubleValue()));
      }

      if (o1 instanceof BigInteger) {
        if (o2 instanceof BigInteger)
          return ((BigInteger)o1).compareTo((BigInteger)o2);

        if (o2 instanceof BigDecimal)
          return new BigDecimal((BigInteger)o1).compareTo((BigDecimal)o2);

        if (o2 instanceof Byte || o2 instanceof Short || o2 instanceof Integer || o2 instanceof Long)
          return ((BigInteger)o1).compareTo(BigInteger.valueOf(o2.longValue()));

        return new BigDecimal((BigInteger)o1).compareTo(BigDecimal.valueOf(o2.doubleValue()));
      }

      if (o1 instanceof Byte || o1 instanceof Short || o1 instanceof Integer || o1 instanceof Long) {
        if (o2 instanceof BigInteger)
          return BigInteger.valueOf(o1.longValue()).compareTo((BigInteger)o2);

        if (o2 instanceof BigDecimal)
          return BigDecimal.valueOf(o1.doubleValue()).compareTo((BigDecimal)o2);

        return (o1.doubleValue() < o2.doubleValue()) ? -1 : ((o1.doubleValue() == o2.doubleValue()) ? 0 : 1);
      }

      if (o2 instanceof BigInteger)
        return BigDecimal.valueOf(o1.doubleValue()).compareTo(new BigDecimal((BigInteger)o2));

      if (o2 instanceof BigDecimal)
        return BigDecimal.valueOf(o1.doubleValue()).compareTo((BigDecimal)o2);

      return (o1.doubleValue() < o2.doubleValue()) ? -1 : ((o1.doubleValue() == o2.doubleValue()) ? 0 : 1);
    }
  };

  public static class Compound {
    public static long encode(final int a, final int b) {
      return ((long)b << Integer.SIZE) & 0xffffffff00000000l | a & 0xffffffffl;
    }

    public static long encode(final short a, final short b, final short c, final short d) {
      return ((long)d << Short.SIZE * 3) & 0xffff000000000000l | ((long)c << Short.SIZE * 2) & 0xffff00000000l | ((long)b << Short.SIZE) & 0xffff0000l | a & 0xffffl;
    }

    public static long encode(final byte a, final byte b, final byte c, final byte d, final byte e, final byte f, final byte g, final byte h) {
      return ((long)h << Byte.SIZE * 7) & 0xff00000000000000l | ((long)g << Byte.SIZE * 6) & 0xff000000000000l | ((long)f << Byte.SIZE * 5) & 0xff0000000000l | ((long)e << Byte.SIZE * 4) & 0xff00000000l | ((long)d << Byte.SIZE * 3) & 0xff000000l | ((long)c << Byte.SIZE * 2) & 0xff0000l | ((long)b << Byte.SIZE) & 0xff00l | a & 0xffl;
    }

    public static int encode(final short a, final short b) {
      return b << Short.SIZE | a & 0xffff;
    }

    public static int encode(final byte a, final byte b, final byte c, final byte d) {
      return (d << Byte.SIZE * 3) & 0xff000000 | (c << Byte.SIZE * 2) & 0xff0000 | (b << Byte.SIZE) & 0xff00 | a & 0xff;
    }

    public static short encode(final byte a, final byte b) {
      return (short)((b << Byte.SIZE) & 0xff00 | a & 0xff);
    }

    public static int dencodeInt(final long val, final int pos) {
      return (int)((val >> Integer.SIZE * pos) & 0xffffffff);
    }

    public static short dencodeShort(final long val, final int pos) {
      return (short)((val >> Short.SIZE * pos) & 0xffff);
    }

    public static byte dencodeByte(final long val, final int pos) {
      return (byte)((val >> Byte.SIZE * pos) & 0xff);
    }

    public static short dencodeShort(final int val, final int pos) {
      return (short)((val >> Short.SIZE * pos) & 0xffff);
    }

    public static byte dencodeByte(final int val, final int pos) {
      return (byte)((val >> Byte.SIZE * pos) & 0xff);
    }

    public static byte dencodeByte(final short val, final int pos) {
      return (byte)((val >> Byte.SIZE * pos) & 0xff);
    }
  }

  public static int compare(final Number a, final Number b) {
    return comparator.compare(a, b);
  }

  private static final int[] highestBitSet = {
    0, 1, 2, 2, 3, 3, 3, 3,
    4, 4, 4, 4, 4, 4, 4, 4,
    5, 5, 5, 5, 5, 5, 5, 5,
    5, 5, 5, 5, 5, 5, 5, 5,
    6, 6, 6, 6, 6, 6, 6, 6,
    6, 6, 6, 6, 6, 6, 6, 6,
    6, 6, 6, 6, 6, 6, 6, 6,
    6, 6, 6, 6, 6, 6, 6, 255, // anything past 63 is a guaranteed overflow with base > 1
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
  };

  public static final double LOG_2 = 0.6931471805599453;
  public static final double LOG_10 = 2.302585092994046;

  @SuppressWarnings("unchecked")
  public static <T extends Number>T valueOf(final Class<T> clazz, final Number number) {
    if (float.class == clazz || Float.class == clazz)
      return (T)Float.valueOf(number.floatValue());

    if (double.class == clazz || Double.class == clazz)
      return (T)Double.valueOf(number.doubleValue());

    if (byte.class == clazz || Byte.class == clazz)
      return (T)Byte.valueOf(number.byteValue());

    if (short.class == clazz || Short.class == clazz)
      return (T)Short.valueOf(number.shortValue());

    if (int.class == clazz || Integer.class == clazz)
      return (T)Integer.valueOf(number.intValue());

    if (long.class == clazz || Long.class == clazz)
      return (T)Long.valueOf(number.longValue());

    if (number instanceof Float || number instanceof Double || number instanceof Byte || number instanceof Short || number instanceof Integer || number instanceof Long) {
      if (BigInteger.class.isAssignableFrom(clazz))
        return (T)BigInteger.valueOf(number.longValue());

      if (BigDecimal.class.isAssignableFrom(clazz))
        return (T)BigDecimal.valueOf(number.doubleValue());

      throw new UnsupportedOperationException(clazz.getName() + " is not a supported Number type");
    }

    if (number instanceof BigInteger) {
      if (BigInteger.class.isAssignableFrom(clazz))
        return (T)number;

      if (BigDecimal.class.isAssignableFrom(clazz))
        return (T)new BigDecimal((BigInteger)number);

      throw new UnsupportedOperationException(clazz.getName() + " is not a supported Number type");
    }

    if (number instanceof BigDecimal) {
      if (BigDecimal.class.isAssignableFrom(clazz))
        return (T)number;

      if (BigInteger.class.isAssignableFrom(clazz))
        return (T)((BigDecimal)number).toBigInteger();

      throw new UnsupportedOperationException(clazz.getName() + " is not a supported Number type");
    }

    throw new UnsupportedOperationException(clazz.getName() + " is not a supported Number type");
  }

  public static long pow(long base, int exp) {
    long result = 1;

    switch (highestBitSet[exp]) {
      case 255: // we use 255 as an overflow marker and return 0 on overflow/underflow
        return base == 1 ? 1 : base == -1 ? 1 - 2 * (exp & 1) : 0;

      case 6:
        if ((exp & 1) != 0)
          result = checkedMultiple(result, base);

        exp >>= 1;
        base *= base;
      case 5:
        if ((exp & 1) != 0)
          result *= base;

        exp >>= 1;
        base *= base;
      case 4:
        if ((exp & 1) != 0)
          result *= base;

        exp >>= 1;
        base *= base;
      case 3:
        if ((exp & 1) != 0)
          result *= base;

        exp >>= 1;
        base *= base;
      case 2:
        if ((exp & 1) != 0)
          result *= base;

        exp >>= 1;
        base *= base;
      case 1:
        if ((exp & 1) != 0)
          result *= base;

      default:
        return result;
    }
  }

  /**
   * Parses the string argument as a boolean. The {@code Boolean}
   * returned is {@code null} if the string argument is {@code null}, and
   * the value {@code true} if the string argument is equal, ignoring case,
   * to the string {@code "true"}. <p>
   * Example: {@code Boolean.parseBoolean("True")} returns {@code true}.<br>
   * Example: {@code Boolean.parseBoolean("yes")} returns {@code false}.
   *
   * @param      s   the {@code String} containing the boolean
   *                 representation to be parsed
   * @return     the {@code Boolean} represented by the string argument
   * @see        java.lang.Boolean#parseBoolean(String)
   */
  public static Boolean parseBoolean(final String s) {
    return s == null ? null : Boolean.parseBoolean(s);
  }

  /**
   * Parses the string argument as per the specification of
   * {@code Short#parseShort(String)}, but returns {@code null} if the
   * string does not contain a parsable {@code short}.
   *
   * @param s    a {@code String} containing the {@code Short}
   *             representation to be parsed
   * @return     the integer value represented by the argument, or
   *             {@code null} if the string does not contain a parsable
   *             {@code short}.
   * @see        java.lang.Short#parseShort(String)
   */
  public static Short parseShort(final String s) {
    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if
    // FIXME: the implementation is copied.
    try {
      return s == null ? null : Short.parseShort(s);
    }
    catch (final NumberFormatException e) {
      return null;
    }
  }

  /**
   * Parses the string argument as per the specification of
   * {@code Integer#parseInt(String)}, but returns {@code null} if the
   * string does not contain a parsable {@code int}.
   *
   * @param s    a {@code String} containing the {@code Integer}
   *             representation to be parsed
   * @return     the {@code int} value represented by the argument, or
   *             {@code null} if the string does not contain a parsable
   *             {@code int}.
   * @see        java.lang.Integer#parseInt(String)
   */
  public static Integer parseInteger(final String s) {
    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if
    // FIXME: the implementation is copied.
    try {
      return s == null ? null : Integer.parseInt(s);
    }
    catch (final NumberFormatException e) {
      return null;
    }
  }

  /**
   * Parses the string argument as per the specification of
   * {@code Long#parseLong(String)}, but returns {@code null} if the
   * string does not contain a parsable {@code long}.
   *
   * @param s    a {@code String} containing the {@code Long}
   *             representation to be parsed.
   * @return     the {@code long} value represented by the argument, or
   *             {@code null} if the string does not contain a parsable
   *             {@code long}.
   * @see        java.lang.Long#parseLong(String)
   */
  public static Long parseLong(final String s) {
    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if
    // FIXME: the implementation is copied.
    try {
      return s == null ? null : Long.parseLong(s);
    }
    catch (final NumberFormatException e) {
      return null;
    }
  }

  /**
   * Parses the string argument as per the specification of
   * {@code Float#parseFloat(String)}, but returns {@code null} if the
   * string does not contain a parsable {@code float}.
   *
   * @param s    a {@code String} containing the {@code Float}
   *             representation to be parsed.
   * @return     the {@code float} value represented by the argument, or
   *             {@code null} if the string does not contain a parsable
   *             {@code float}.
   * @see        java.lang.Float#parseFloat(String)
   */
  public static Float parseFloat(final String s) {
    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if
    // FIXME: the implementation is copied.
    try {
      return s == null ? null : Float.parseFloat(s);
    }
    catch (final NumberFormatException e) {
      return null;
    }
  }

  /**
   * Parses the string argument as per the specification of
   * {@code Double#parseDouble(String)}, but returns {@code null} if the
   * string does not contain a parsable {@code double}.
   *
   * @param s    a {@code String} containing the {@code Double}
   *             representation to be parsed.
   * @return     the {@code double} value represented by the argument, or
   *             {@code null} if the string does not contain a parsable
   *             {@code double}.
   * @see        java.lang.Long#parseLong(String)
   */
  public static Double parseDouble(final String s) {
    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if
    // FIXME: the implementation is copied.
    try {
      return s == null ? null : Double.parseDouble(s);
    }
    catch (final NumberFormatException e) {
      return null;
    }
  }

  public static int[] parseInt(final String ... s) {
    final int[] values = new int[s.length];
    for (int i = 0; i < s.length; i++)
      values[i] = Integer.parseInt(s[i]);

    return values;
  }

  public static double[] parseDouble(final String ... s) {
    final double[] values = new double[s.length];
    for (int i = 0; i < s.length; i++)
      values[i] = Double.parseDouble(s[i]);

    return values;
  }

  public static double parseNumber(String string) {
    if (string == null || (string = string.trim()).length() == 0 || !isNumber(string))
      return Double.NaN;

    double scalar = 0;
    final String[] parts = string.split(" ");
    if (parts.length == 2) {
      scalar += new BigDecimal(parts[0]).doubleValue();
      string = parts[1];
    }

    final int slash = string.indexOf('/');
    if (slash == 1)
      scalar += (double)Integer.parseInt(string.substring(0, slash)) / (double)Integer.parseInt(string.substring(slash + 1, string.length()));
    else
      scalar += new BigDecimal(string).doubleValue();

    return scalar;
  }

  public static boolean isNumber(String string) {
    if (string == null || (string = string.trim()).length() == 0)
      return false;

    final String[] parts = string.split(" ");
    if (parts.length > 2)
      return false;

    if (parts.length == 2) {
      final int slash = parts[1].indexOf('/');
      if (slash == -1)
        return false;

      return isNumber(parts[0], false) && isNumber(parts[1], true);
    }

    return isNumber(parts[0], true);
  }

  private static boolean isNumber(String string, final boolean fraction) {
    if (string == null || (string = string.trim()).length() == 0)
      return false;

    boolean dotEncountered = false;
    boolean expEncountered = false;
    boolean minusEncountered = false;
    boolean slashEncountered = false;
    int factor = 0;
    for (int i = string.length() - 1; i >= 0; i--) {
      char c = string.charAt(i);
      if (c < '0') {
        if (c == '/') {
          if (!fraction || dotEncountered || expEncountered || minusEncountered || slashEncountered)
            return false;

          slashEncountered = true;
        }
        else if (c == '.') {
          if (dotEncountered || slashEncountered)
            return false;

          dotEncountered = true;
        }
        else if (c == '-') {
          if (minusEncountered)
            return false;

          minusEncountered = true;
        }
        else
          return false;
      }
      else if ('9' < c) {
        if (c != 'E')
          return false;

        if (factor == 0 || expEncountered)
          return false;

        expEncountered = true;
        factor = 0;
        minusEncountered = false;
      }
      else {
        if (minusEncountered)
          return false;

        factor++;
      }
    }

    return true;
  }

  public static boolean isInteger(final float number) {
    return (int)number == number;
  }

  public static boolean isInteger(final double number) {
    return (int)number == number;
  }

  public static long checkedMultiple(final long a, final long b) {
    final long maximum = Long.signum(a) == Long.signum(b) ? Long.MAX_VALUE : Long.MIN_VALUE;
    if (a != 0 && (b > 0 && b > maximum / a || b < 0 && b < maximum / a))
      throw new ArithmeticException("long overflow");

    return a * b;
  }

  public static int rotateBits(final int value, final int sizeof, final int distance) {
    return (int)((distance == 0 ? value : (distance < 0 ? value << -distance | value >> (sizeof + distance) : value >> distance | value << (sizeof - distance))) & (pow(2, sizeof) - 1));
  }

  public static String toString(final double value, final int decimals) {
    final double factor = Math.pow(10, decimals);
    return String.valueOf(Math.round(value * factor) / factor);
  }

  public static String roundInsignificant(final String value) {
    if (value == null)
      return null;

    if (value.length() == 0)
      return value;

    int i = value.length();
    while (i-- > 0)
      if (value.charAt(i) != '0' && value.charAt(i) != '.')
        break;

    return value.substring(0, i + 1);
  }

  // FIXME: This is not working! it is returning incorrect results
  public static double log2(BigInteger value) {
    final int blex = value.bitLength() - 1022; // any value in 60..1023 is ok
    if (blex > 0)
      value = value.shiftRight(blex);

    final double result = Math.log(value.doubleValue());
    return blex > 0 ? result + blex * LOG_2 : result;
  }

  public static boolean equivalent(final Number a, final Number b, final double epsilon) {
    if (a == null)
      return b == null;

    if (b == null)
      return false;

    if (a instanceof Byte) {
      if (b instanceof Byte)
        return a.byteValue() == b.byteValue();

      if (b instanceof Short)
        return a.byteValue() == b.shortValue();

      if (b instanceof Integer)
        return a.byteValue() == b.intValue();

      if (b instanceof Long)
        return a.byteValue() == b.longValue();

      if (b instanceof BigInteger)
        return BigInteger.valueOf(a.byteValue()).equals(b);

      if (b instanceof BigDecimal)
        return BigDecimal.valueOf(a.byteValue()).equals(b);

      if (b instanceof Float)
        return Math.abs(a.byteValue() - b.floatValue()) < epsilon;

      return Math.abs(a.byteValue() - b.doubleValue()) < epsilon;
    }

    if (a instanceof Short) {
      if (b instanceof Byte)
        return a.shortValue() == b.byteValue();

      if (b instanceof Short)
        return a.shortValue() == b.shortValue();

      if (b instanceof Integer)
        return a.shortValue() == b.intValue();

      if (b instanceof Long)
        return a.shortValue() == b.longValue();

      if (b instanceof BigInteger)
        return BigInteger.valueOf(a.shortValue()).equals(b);

      if (b instanceof BigDecimal)
        return BigDecimal.valueOf(a.shortValue()).equals(b);

      if (b instanceof Float)
        return Math.abs(a.shortValue() - b.floatValue()) < epsilon;

      return Math.abs(a.shortValue() - b.doubleValue()) < epsilon;
    }

    if (a instanceof Integer) {
      if (b instanceof Byte)
        return a.intValue() == b.byteValue();

      if (b instanceof Short)
        return a.intValue() == b.shortValue();

      if (b instanceof Integer)
        return a.intValue() == b.intValue();

      if (b instanceof Long)
        return a.intValue() == b.longValue();

      if (b instanceof BigInteger)
        return BigInteger.valueOf(a.intValue()).equals(b);

      if (b instanceof BigDecimal)
        return BigDecimal.valueOf(a.intValue()).equals(b);

      if (b instanceof Float)
        return Math.abs(a.intValue() - b.floatValue()) < epsilon;

      return Math.abs(a.intValue() - b.doubleValue()) < epsilon;
    }

    if (a instanceof Long) {
      if (b instanceof Byte)
        return a.longValue() == b.byteValue();

      if (b instanceof Short)
        return a.longValue() == b.shortValue();

      if (b instanceof Integer)
        return a.longValue() == b.intValue();

      if (b instanceof Long)
        return a.longValue() == b.longValue();

      if (b instanceof BigInteger)
        return BigInteger.valueOf(a.longValue()).equals(b);

      if (b instanceof BigDecimal)
        return BigDecimal.valueOf(a.longValue()).equals(b);

      if (b instanceof Float)
        return Math.abs(a.longValue() - b.floatValue()) < epsilon;

      return Math.abs(a.longValue() - b.doubleValue()) < epsilon;
    }

    if (a instanceof BigInteger) {
      if (b instanceof Byte)
        return ((BigInteger)a).equals(BigInteger.valueOf(b.byteValue()));

      if (b instanceof Short)
        return ((BigInteger)a).equals(BigInteger.valueOf(b.shortValue()));

      if (b instanceof Integer)
        return ((BigInteger)a).equals(BigInteger.valueOf(b.intValue()));

      if (b instanceof Long)
        return ((BigInteger)a).equals(BigInteger.valueOf(b.longValue()));

      if (b instanceof BigInteger)
        return ((BigInteger)a).equals(b);

      if (b instanceof BigDecimal)
        return new BigDecimal((BigInteger)a).equals(b);

      if (b instanceof Float)
        return ((BigInteger)a).equals(BigDecimal.valueOf(b.floatValue()).toBigInteger());

      if (b instanceof Double)
        return ((BigInteger)a).equals(BigDecimal.valueOf(b.doubleValue()).toBigInteger());
    }
    else if (a instanceof BigDecimal) {
      if (b instanceof Byte)
        return ((BigDecimal)a).equals(BigDecimal.valueOf(b.byteValue()));

      if (b instanceof Short)
        return ((BigDecimal)a).equals(BigDecimal.valueOf(b.shortValue()));

      if (b instanceof Integer)
        return ((BigDecimal)a).equals(BigDecimal.valueOf(b.intValue()));

      if (b instanceof Long)
        return ((BigDecimal)a).equals(BigDecimal.valueOf(b.longValue()));

      if (b instanceof BigInteger)
        return ((BigDecimal)a).equals(new BigDecimal((BigInteger)b));

      if (b instanceof BigDecimal)
        return ((BigDecimal)a).equals(b);

      if (b instanceof Float)
        return ((BigInteger)a).equals(BigDecimal.valueOf(b.floatValue()).toBigInteger());

      if (b instanceof Double)
        return ((BigDecimal)a).equals(BigDecimal.valueOf(b.doubleValue()));
    }

    return Math.abs(a.doubleValue() - b.doubleValue()) < epsilon;
  }

  @SuppressWarnings("unchecked")
  public static <T extends Number>T cast(final Number number, final Class<T> type) {
    if (type == Byte.class)
      return (T)Byte.valueOf(number.byteValue());

    if (type == Short.class)
      return (T)Short.valueOf(number.shortValue());

    if (type == Integer.class)
      return (T)Integer.valueOf(number.intValue());

    if (type == Float.class)
      return (T)Float.valueOf(number.floatValue());

    if (type == Double.class)
      return (T)Double.valueOf(number.doubleValue());

    if (type == Long.class)
      return (T)Long.valueOf(number.longValue());

    throw new UnsupportedOperationException("Unsupported Numebr type: " + type.getName());
  }

  public static byte precision(final int number) {
    return (byte)(number == 0 ? 1 : Math.log10(number != Integer.MIN_VALUE ? Math.abs(number) : Math.abs((long)number)) + 1);
  }

  public static byte precision(final long number) {
    return number != Long.MIN_VALUE ? (byte)(Math.log10(Math.abs(number)) + 1) : (byte)precision(BigInteger.valueOf(number));
  }

  public static int precision(final BigInteger number) {
    return number.abs().toString().length();
  }

  public static int precision(final BigDecimal number) {
    return number.precision();
  }

  public static int trailingZeroes(int number) {
    int zeros = 0;
    while (number % 10 == 0 && number != 0) {
      zeros++;
      number /= 10;
    }

    return zeros;
  }

  public static int trailingZeroes(long number) {
    int zeroes = 0;
    while (number % 10 == 0 && number != 0) {
      zeroes++;
      number /= 10;
    }

    return zeroes;
  }

  public static int numberOfDecimalPlaces(final BigDecimal bigDecimal) {
    return Math.max(0, bigDecimal.stripTrailingZeros().scale());
  }

  public static BigDecimal toBigDecimal(final Number number) {
    if (number instanceof BigDecimal)
      return (BigDecimal)number;

    if (number instanceof BigInteger)
      return new BigDecimal((BigInteger)number);

    if (number instanceof Byte)
      return BigDecimal.valueOf(number.byteValue());

    if (number instanceof Short)
      return BigDecimal.valueOf(number.shortValue());

    if (number instanceof Integer)
      return BigDecimal.valueOf(number.intValue());

    if (number instanceof Long)
      return BigDecimal.valueOf(number.longValue());

    if (number instanceof Float)
      return BigDecimal.valueOf(number.floatValue()).stripTrailingZeros();

    return BigDecimal.valueOf(number.doubleValue()).stripTrailingZeros();
  }

  public static BigDecimal average(final BigDecimal ... numbers) {
    BigDecimal sum = numbers[0];
    for (int i = 1; i < numbers.length; i++)
      sum = sum.add(numbers[i]);

    return sum.divide(BigDecimal.valueOf(numbers.length));
  }

  public static BigDecimal average(final BigInteger ... numbers) {
    BigDecimal sum = new BigDecimal(numbers[0]);
    for (int i = 1; i < numbers.length; i++)
      sum = sum.add(new BigDecimal(numbers[i]));

    return sum.divide(BigDecimal.valueOf(numbers.length));
  }

  public static double average(final byte ... numbers) {
    long sum = numbers[0];
    for (int i = 1; i < numbers.length; i++)
      sum += numbers[i];

    return sum / numbers.length;
  }

  public static double average(final short ... numbers) {
    long sum = numbers[0];
    for (int i = 1; i < numbers.length; i++)
      sum += numbers[i];

    return sum / numbers.length;
  }

  public static double average(final int ... numbers) {
    long sum = numbers[0];
    for (int i = 1; i < numbers.length; i++)
      sum += numbers[i];

    return sum / numbers.length;
  }

  public static double average(final long ... numbers) {
    long sum = numbers[0];
    for (int i = 1; i < numbers.length; i++)
      sum += numbers[i];

    return sum / numbers.length;
  }

  public static double average(final float ... numbers) {
    double sum = numbers[0];
    for (int i = 1; i < numbers.length; i++)
      sum += numbers[i];

    return sum / numbers.length;
  }

  public static double average(final double ... numbers) {
    double sum = numbers[0];
    for (int i = 1; i < numbers.length; i++)
      sum += numbers[i];

    return sum / numbers.length;
  }

  // FIXME: Write a test for this
  public static Number compress(final Number number) {
    if (number == null || number instanceof Float || number instanceof Double || number instanceof BigDecimal || number instanceof Byte)
      return number;

    final double value = number.doubleValue();
    if (Byte.MIN_VALUE <= value && value <= Byte.MAX_VALUE)
      return number.byteValue();

    if (Short.MIN_VALUE <= value && value <= Short.MAX_VALUE)
      return number.shortValue();

    if (Integer.MIN_VALUE <= value && value <= Integer.MAX_VALUE)
      return number.intValue();

    if (Long.MIN_VALUE <= value && value <= Long.MAX_VALUE)
      return number.longValue();

    return number;
  }

  private Numbers() {
  }
}