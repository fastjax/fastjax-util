/* Copyright (c) 2008 LibJ
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

package org.libj.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

/**
 * Utility functions for operations pertaining to {@link Number}.
 */
public final class Numbers {
  /** {@link BigInteger} representation of {@link Long#MIN_VALUE}. */
  public static final BigInteger LONG_MIN_VALUE = BigInteger.valueOf(Long.MIN_VALUE);

  /** {@link BigInteger} representation of {@link Long#MAX_VALUE}. */
  public static final BigInteger LONG_MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE);

  /**
   * Utility functions to convert between signed and unsigned numbers.
   */
  public static class Unsigned {
    /** Max value of an unsigned long: {@code 2^64 - 1}. */
    public static final BigInteger UNSIGNED_LONG_MAX_VALUE = new BigInteger("18446744073709551615");

    /**
     * Returns the value of the specified unsigned {@code byte} as a signed
     * {@code short}.
     *
     * @param unsigned The unsigned {@code byte}.
     * @return The value of the specified unsigned {@code byte} as a signed
     *         {@code short}.
     */
    public static short toSigned(final byte unsigned) {
      return (short)(unsigned - Byte.MIN_VALUE);
    }

    /**
     * Returns the value of the specified unsigned {@code short} as a signed
     * {@code int}.
     *
     * @param unsigned The unsigned {@code short}.
     * @return The value of the specified unsigned {@code short} as a signed
     *         {@code int}.
     */
    public static int toSigned(final short unsigned) {
      return unsigned - Short.MIN_VALUE;
    }

    /**
     * Returns the value of the specified unsigned {@code int} as a signed
     * {@code long}.
     *
     * @param unsigned The unsigned {@code int}.
     * @return The value of the specified unsigned {@code int} as a signed
     *         {@code long}.
     */
    public static long toSigned(final int unsigned) {
      return (long)unsigned - Integer.MIN_VALUE;
    }

    /**
     * Returns the value of the specified unsigned {@code long} as a signed
     * {@link BigInteger}.
     *
     * @param unsigned The unsigned {@code long}.
     * @return The value of the specified unsigned {@code long} as a signed
     *         {@link BigInteger}.
     */
    public static BigInteger toSigned(final long unsigned) {
      return BigInteger.valueOf(unsigned).subtract(LONG_MIN_VALUE);
    }

    /**
     * Returns the value of the specified {@code byte} array representing an
     * unsigned value as a signed {@link BigInteger}.
     *
     * @param unsigned The {@code byte} array representing an unsigned value.
     * @return The value of the specified {@code byte} array representing an
     *         unsigned value as a signed {@link BigInteger}.
     */
    public static BigInteger toSigned(final byte[] unsigned) {
      return new BigInteger(1, unsigned);
    }

    // FIXME: jdk9+
//    public static BigInteger toSigned(final byte[] unsigned, final int off, final int len) {
//      return new BigInteger(1, unsigned, off, len);
//    }

    /**
     * Returns the unsigned representation of the signed value. The signed value
     * cannot be less than {@code 0}.
     *
     * @param signed The signed value.
     * @return The unsigned representation of the signed value.
     * @throws IllegalArgumentException If {@code signed < 0}.
     */
    public static byte toUnsigned(final byte signed) {
      if (signed < 0)
        throw new IllegalArgumentException("signed < 0");

      return (byte)(signed + Byte.MIN_VALUE);
    }

    /**
     * Returns the unsigned representation of the signed value. The signed value
     * cannot be less than {@code 0}.
     *
     * @param signed The signed value.
     * @return The unsigned representation of the signed value.
     * @throws IllegalArgumentException If {@code signed < 0}.
     */
    public static short toUnsigned(final short signed) {
      if (signed < 0)
        throw new IllegalArgumentException("signed < 0: " + signed);

      return (short)(signed + Short.MIN_VALUE);
    }

    /**
     * Returns the unsigned representation of the signed value. The signed value
     * cannot be less than {@code 0}.
     *
     * @param signed The signed value.
     * @return The unsigned representation of the signed value.
     * @throws IllegalArgumentException If {@code signed < 0}.
     */
    public static int toUnsigned(final int signed) {
      if (signed < 0)
        throw new IllegalArgumentException("signed < 0: " + signed);

      return signed + Integer.MIN_VALUE;
    }

    /**
     * Returns the unsigned representation of the signed value. The signed value
     * cannot be less than {@code 0}.
     *
     * @param signed The signed value.
     * @return The unsigned representation of the signed value.
     * @throws IllegalArgumentException If {@code signed < 0}.
     */
    public static long toUnsigned(final long signed) {
      if (signed < 0)
        throw new IllegalArgumentException("signed < 0: " + signed);

      return signed + Long.MIN_VALUE;
    }

    /**
     * Returns the unsigned representation of the signed value as a {@code byte}
     * array. The signed value cannot be less than {@code 0}.
     *
     * @param signed The signed value.
     * @return The unsigned representation of the signed value as a {@code byte}
     *         array.
     * @throws IllegalArgumentException If {@code signed < 0}.
     */
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

  private static final Comparator<Number> comparator = (o1, o2) -> {
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

      return o1.doubleValue() < o2.doubleValue() ? -1 : ((o1.doubleValue() == o2.doubleValue()) ? 0 : 1);
    }

    if (o2 instanceof BigInteger)
      return BigDecimal.valueOf(o1.doubleValue()).compareTo(new BigDecimal((BigInteger)o2));

    if (o2 instanceof BigDecimal)
      return BigDecimal.valueOf(o1.doubleValue()).compareTo((BigDecimal)o2);

    return o1.doubleValue() < o2.doubleValue() ? -1 : ((o1.doubleValue() == o2.doubleValue()) ? 0 : 1);
  };

  /**
   * Utility functions for the encoding and decoding of "compound values" in
   * primitive types. A "compound value" in a primitive type is one that
   * contains multiple values of a smaller sized primitive type. For example, a
   * {@code short} is 16 bits in size, allowing it to represent a compound value
   * of 2 {@code byte}s, since a {@code byte} is 8 bits in size.
   */
  public static class Compound {
    // FIXME: Implement all the combinations of encode(...)
    /**
     * Encodes two {@code int}s into a {@code long}.
     *
     * @param a The first {@code int}.
     * @param b The second {@code int}.
     * @return A compounded {@code long} representing two {@code int} values.
     */
    public static long encode(final int a, final int b) {
      return ((long)b << Integer.SIZE) & 0xffffffff00000000l | a & 0xffffffffl;
    }

    /**
     * Encodes four {@code short}s into a {@code long}.
     *
     * @param a The first {@code short}.
     * @param b The second {@code short}.
     * @param c The third {@code short}.
     * @param d The fourth {@code short}.
     * @return A compounded {@code long} representing four {@code short} values.
     */
    public static long encode(final short a, final short b, final short c, final short d) {
      return ((long)d << Short.SIZE * 3) & 0xffff000000000000l | ((long)c << Short.SIZE * 2) & 0xffff00000000l | ((long)b << Short.SIZE) & 0xffff0000l | a & 0xffffl;
    }

    /**
     * Encodes eight {@code byte}s into a {@code long}.
     *
     * @param a The first {@code byte}.
     * @param b The second {@code byte}.
     * @param c The third {@code byte}.
     * @param d The fourth {@code byte}.
     * @param e The fifth {@code byte}.
     * @param f The sixth {@code byte}.
     * @param g The seventh {@code byte}.
     * @param h The eight {@code byte}.
     * @return A compounded {@code long} representing eight {@code byte} values.
     */
    public static long encode(final byte a, final byte b, final byte c, final byte d, final byte e, final byte f, final byte g, final byte h) {
      return ((long)h << Byte.SIZE * 7) & 0xff00000000000000l | ((long)g << Byte.SIZE * 6) & 0xff000000000000l | ((long)f << Byte.SIZE * 5) & 0xff0000000000l | ((long)e << Byte.SIZE * 4) & 0xff00000000l | ((long)d << Byte.SIZE * 3) & 0xff000000l | ((long)c << Byte.SIZE * 2) & 0xff0000l | ((long)b << Byte.SIZE) & 0xff00l | a & 0xffl;
    }

    /**
     * Encodes a {@code float} and an {@code int} into a {@code long}.
     *
     * @param a The first {@code float}.
     * @param b The second {@code int}.
     * @return A compounded {@code long} representing a {@code float} and an {@code int}.
     */
    public static long encode(final float a, final int b) {
      return encode(Float.floatToIntBits(a), b);
    }

    /**
     * Encodes an {@code int} and a {@code float} into a {@code long}.
     *
     * @param a The first {@code int}.
     * @param b The second {@code float}.
     * @return A compounded {@code long} representing an {@code int} and a
     *         {@code float}.
     */
    public static long encode(final int a, final float b) {
      return encode(a, Float.floatToIntBits(b));
    }

    /**
     * Encodes two {@code short}s into an {@code int}.
     *
     * @param a The first {@code short}.
     * @param b The second {@code short}.
     * @return A compounded {@code int} representing two {@code short} values.
     */
    public static int encode(final short a, final short b) {
      return b << Short.SIZE | a & 0xffff;
    }

    /**
     * Encodes four {@code byte}s into an {@code int}.
     *
     * @param a The first {@code byte}.
     * @param b The second {@code byte}.
     * @param c The third {@code byte}.
     * @param d The fourth {@code byte}.
     * @return A compounded {@code int} representing two {@code byte} values.
     */
    public static int encode(final byte a, final byte b, final byte c, final byte d) {
      return (d << Byte.SIZE * 3) & 0xff000000 | (c << Byte.SIZE * 2) & 0xff0000 | (b << Byte.SIZE) & 0xff00 | a & 0xff;
    }

    /**
     * Encodes two {@code byte}s into a {@code short}.
     *
     * @param a The first {@code byte}.
     * @param b The second {@code byte}.
     * @return A compounded {@code short} representing two {@code byte} values.
     */
    public static short encode(final byte a, final byte b) {
      return (short)((b << Byte.SIZE) & 0xff00 | a & 0xff);
    }

    /**
     * Decodes the {@code int} value at the specified position that is
     * represented in the provided compounded {@code long} value.
     *
     * @param val The compounded {@code long} containing {@code int} values.
     * @param pos The position of the value to decode (0, 1).
     * @return The {@code int} value at the specified position that is
     *         represented in the provided compounded {@code long} value.
     */
    public static int decodeInt(final long val, final int pos) {
      return (int)(val >> Integer.SIZE * pos);
    }

    /**
     * Decodes the {@code float} value at the specified position that is
     * represented in the provided compounded {@code long} value.
     *
     * @param val The compounded {@code long} containing a {@code float} value.
     * @param pos The position of the value to decode (0, 1).
     * @return The {@code float} value at the specified position that is
     *         represented in the provided compounded {@code long} value.
     */
    public static float decodeFloat(final long val, final int pos) {
      return Float.intBitsToFloat((int)(val >> Integer.SIZE * pos));
    }

    /**
     * Decodes the {@code short} value at the specified position that is
     * represented in the provided compounded {@code long} value.
     *
     * @param val The compounded {@code long} containing {@code short} values.
     * @param pos The position of the value to decode (0, 1, 2, 3).
     * @return The {@code short} value at the specified position that is
     *         represented in the provided compounded {@code long} value.
     */
    public static short decodeShort(final long val, final int pos) {
      return (short)((val >> Short.SIZE * pos) & 0xffff);
    }

    /**
     * Decodes the {@code byte} value at the specified position that is
     * represented in the provided compounded {@code long} value.
     *
     * @param val The compounded {@code long} containing {@code byte} values.
     * @param pos The position of the value to decode (0, 1, 2, 3, 4, 5, 6, 7).
     * @return The {@code byte} value at the specified position that is
     *         represented in the provided compounded {@code long} value.
     */
    public static byte decodeByte(final long val, final int pos) {
      return (byte)((val >> Byte.SIZE * pos) & 0xff);
    }

    /**
     * Decodes the {@code short} value at the specified position that is
     * represented in the provided compounded {@code int} value.
     *
     * @param val The compounded {@code int} containing {@code short} values.
     * @param pos The position of the value to decode (0, 1).
     * @return The {@code short} value at the specified position that is
     *         represented in the provided compounded {@code int} value.
     */
    public static short decodeShort(final int val, final int pos) {
      return (short)((val >> Short.SIZE * pos) & 0xffff);
    }

    /**
     * Decodes the {@code byte} value at the specified position that is
     * represented in the provided compounded {@code int} value.
     *
     * @param val The compounded {@code int} containing {@code byte} values.
     * @param pos The position of the value to decode (0, 1, 2, 3).
     * @return The {@code byte} value at the specified position that is
     *         represented in the provided compounded {@code int} value.
     */
    public static byte decodeByte(final int val, final int pos) {
      return (byte)((val >> Byte.SIZE * pos) & 0xff);
    }

    /**
     * Decodes the {@code byte} value at the specified position that is
     * represented in the provided compounded {@code short} value.
     *
     * @param val The compounded {@code short} containing {@code byte} values.
     * @param pos The position of the value to decode (0, 1).
     * @return The {@code byte} value at the specified position that is
     *         represented in the provided compounded {@code short} value.
     */
    public static byte decodeByte(final short val, final int pos) {
      return (byte)((val >> Byte.SIZE * pos) & 0xff);
    }
  }

  /**
   * Compares the specified numbers, returning a negative integer, zero, or a
   * positive integer as the first argument is less than, equal to, or greater
   * than the second.
   * <p>
   * Null values are considered as less than non-null values.
   *
   * @param a The first {@link Number} to be compared.
   * @param b The second {@link Number} to be compared.
   * @return A negative integer, zero, or a positive integer as the first
   *         argument is less than, equal to, or greater than the second.
   */
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

  /** {@code double} representation of log(2) */
  public static final double LOG_2 = 0.6931471805599453;

  /** {@code double} representation of log(10) */
  public static final double LOG_10 = 2.302585092994046;

  /**
   * Returns a {@link Number} of type {@code <T>} representing type specified
   * number of generic {@link Number} type.
   *
   * @param <T> The type of {@link Number} to be returned.
   * @param number The number to get the value of.
   * @param as The class representing the type of {@link Number} to be returned.
   * @return A {@link Number} of type {@code <T>} representing type specified
   *         number of generic {@link Number} type.
   * @throws UnsupportedOperationException If the specified {@link Class}
   *           represents a type that is not one of: <br>
   *           <blockquote>{@code byte}, {@link Byte},<br>
   *           {@code short}, {@link Short},<br>
   *           {@code char}, {@link Character},<br>
   *           {@code int}, {@link Integer},<br>
   *           {@code long}, {@link Long},<br>
   *           {@code double}, {@link Double},<br>
   *           {@code float}, {@link Float},<br>
   *           {@code short}, {@link Short},<br>
   *           {@link BigInteger}, {@link BigDecimal}</blockquote>
   */
  @SuppressWarnings("unchecked")
  public static <T extends Number>T valueOf(final Number number, final Class<T> as) {
    if (float.class == as || Float.class == as)
      return (T)Float.valueOf(number.floatValue());

    if (double.class == as || Double.class == as)
      return (T)Double.valueOf(number.doubleValue());

    if (byte.class == as || Byte.class == as)
      return (T)Byte.valueOf(number.byteValue());

    if (short.class == as || Short.class == as)
      return (T)Short.valueOf(number.shortValue());

    if (int.class == as || Integer.class == as)
      return (T)Integer.valueOf(number.intValue());

    if (long.class == as || Long.class == as)
      return (T)Long.valueOf(number.longValue());

    if (number instanceof Float || number instanceof Double || number instanceof Byte || number instanceof Short || number instanceof Integer || number instanceof Long) {
      if (BigInteger.class.isAssignableFrom(as))
        return (T)BigInteger.valueOf(number.longValue());

      if (BigDecimal.class.isAssignableFrom(as))
        return (T)BigDecimal.valueOf(number.doubleValue());

      throw new UnsupportedOperationException(as.getName() + " is not a supported Number type");
    }

    if (number instanceof BigInteger) {
      if (BigInteger.class.isAssignableFrom(as))
        return (T)number;

      if (BigDecimal.class.isAssignableFrom(as))
        return (T)new BigDecimal((BigInteger)number);

      throw new UnsupportedOperationException(as.getName() + " is not a supported Number type");
    }

    if (number instanceof BigDecimal) {
      if (BigDecimal.class.isAssignableFrom(as))
        return (T)number;

      if (BigInteger.class.isAssignableFrom(as))
        return (T)((BigDecimal)number).toBigInteger();

      throw new UnsupportedOperationException(as.getName() + " is not a supported Number type");
    }

    throw new UnsupportedOperationException(as.getName() + " is not a supported Number type");
  }

  /**
   * Returns the value of the specified base raised to the power of the
   * specified exponent. The algorithm in this implementation takes advantage of
   * the whole values of the base and exponent to outperform
   * {@link Math#pow(double,double)}.
   *
   * @param base The base.
   * @param exp The exponent.
   * @return The value of the specified base raised to the power of the
   *         specified exponent.
   * @throws ArithmeticException If the resulting value cannot be represented as
   *           a {@code long} due to overflow.
   */
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
   * Parses the string argument as a boolean. The {@link Boolean} returned is
   * null if the string argument is null, and the value {@code true} if the
   * string argument is equal, ignoring case, to the string {@code "true"}.
   * <ul>
   * <li>{@code Boolean.parseBoolean("True")} returns {@code true}.</li>
   * <li>{@code Boolean.parseBoolean("yes")} returns {@code false}.</li>
   * </ul>
   *
   * @param s The {@link String} containing the boolean representation to be
   *          parsed.
   * @return The {@link Boolean} represented by the string argument.
   * @see Boolean#parseBoolean(String)
   */
  public static Boolean parseBoolean(final String s) {
    return s == null ? null : Boolean.parseBoolean(s);
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Short#parseShort(String)}, but returns {@code null} if the string
   * does not contain a parsable {@code short}.
   *
   * @param s A {@link String} containing the {@link Short} representation to be
   *          parsed.
   * @return The integer value represented by the argument, or {@code null} if
   *         the string does not contain a parsable {@code short}.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final String s) {
    return parseShort(s, 10);
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Short#parseShort(String)}, but returns {@code defaultValue} if the
   * string does not contain a parsable {@code short}.
   *
   * @param s A {@link String} containing the {@code short} representation to be
   *          parsed.
   * @param defaultValue The {@code short} value to be returned if the string
   *          does not contain a parsable {@code short}.
   * @return The {@code short} value represented by the argument, or
   *         {@code defaultValue} if the string does not contain a parsable
   *         {@code short}.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final String s, final short defaultValue) {
    return parseShort(s, 10, defaultValue);
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Short#parseShort(String)}, but returns {@code defaultValue} if the
   * string does not contain a parsable {@code short}.
   *
   * @param cbuf A {@code char} array containing the {@code short} representation
   *          to be parsed.
   * @param defaultValue The {@code short} value to be returned if the string
   *          does not contain a parsable {@code short}.
   * @return The {@code short} value represented by the argument, or
   *         {@code defaultValue} if the string does not contain a parsable
   *         {@code short}.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final char[] cbuf, final short defaultValue) {
    return parseShort(cbuf, 10, defaultValue);
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Short#parseShort(String,int)}, but returns {@code null} if the
   * string does not contain a parsable {@code short}.
   *
   * @param s A {@link String} containing the {@link Short} representation to be
   *          parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code null} if
   *         the string does not contain a parsable {@code short}.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final String s, final int radix) {
    final int i = parseInt(s, radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? null : (short)i;
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Short#parseShort(String,int)}, but returns {@code null} if the
   * string does not contain a parsable {@code short}.
   *
   * @param cbuf A {@code char} array containing the {@link Short}
   *          representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code null} if
   *         the string does not contain a parsable {@code short}.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final char[] cbuf, final int radix) {
    final int i = parseInt(cbuf, radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? null : (short)i;
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Short#parseShort(String,int)}, but returns {@code defaultValue} if
   * the string does not contain a parsable {@code short}.
   *
   * @param s A {@link String} containing the {@code short} representation to be
   *          parsed.
   * @param defaultValue The {@code short} value to be returned if the string
   *          does not contain a parsable {@code short}.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or
   *         {@code defaultValue} if the string does not contain a parsable
   *         {@code short}.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final String s, final int radix, final short defaultValue) {
    final int i = parseInt(s, radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? defaultValue : (short)i;
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Short#parseShort(String,int)}, but returns {@code defaultValue} if
   * the string does not contain a parsable {@code short}.
   *
   * @param cbuf A {@code char} array containing the {@code short} representation
   *          to be parsed.
   * @param defaultValue The {@code short} value to be returned if the string
   *          does not contain a parsable {@code short}.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or
   *         {@code defaultValue} if the string does not contain a parsable
   *         {@code short}.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final char[] cbuf, final int radix, final short defaultValue) {
    final int i = parseInt(cbuf, radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? defaultValue : (short)i;
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Integer#parseInt(String)}, but returns {@code null} if the string
   * does not contain a parsable {@code int}.
   *
   * @param s A {@link String} containing the {@link Integer} representation to
   *          be parsed.
   * @return The {@code int} value represented by the argument, or {@code null}
   *         if the string does not contain a parsable {@code int}.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final String s) {
    return parseInteger(s, 10);
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Integer#parseInt(String)}, but returns {@code defaultValue} if the
   * string does not contain a parsable {@code int}.
   *
   * @param s A {@link String} containing the {@code int} representation to
   *          be parsed.
   * @param defaultValue The {@code int} value to be returned if the string does
   *          not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or
   *         {@code defaultValue} if the string does not contain a parsable
   *         {@code int}.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final String s, final int defaultValue) {
    return parseInt(s, 10, defaultValue);
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Integer#parseInt(String)}, but returns {@code defaultValue} if the
   * string does not contain a parsable {@code int}.
   *
   * @param cbuf A {@code char} array containing the {@code int} representation
   *          to be parsed.
   * @param defaultValue The {@code int} value to be returned if the string does
   *          not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or
   *         {@code defaultValue} if the string does not contain a parsable
   *         {@code int}.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final char[] cbuf, final int defaultValue) {
    return parseInt(cbuf, 10, defaultValue);
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Integer#parseInt(String,int)}, but returns {@code defaultValue} if
   * the string does not contain a parsable {@code int}.
   *
   * @param s A {@link String} containing the {@code int} representation to
   *          be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code int} value to be returned if the string does
   *          not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or
   *         {@code defaultValue} if the string does not contain a parsable
   *         {@code int}.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final String s, final int radix, final int defaultValue) {
    if (s == null || radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      return defaultValue;

    final int len = s.length();
    boolean negative = false;
    int i = 0;
    int limit = -Integer.MAX_VALUE;

    if (len == 0)
      return defaultValue;

    char firstChar = s.charAt(0);
    if (firstChar < '0') { // Possible leading "+" or "-"
      if (firstChar == '-') {
        negative = true;
        limit = Integer.MIN_VALUE;
      }
      else if (firstChar != '+') {
        return defaultValue;
      }

      if (len == 1) { // Cannot have lone "+" or "-"
        return defaultValue;
      }

      ++i;
    }

    final int multmin = limit / radix;
    int result = 0;
    while (i < len) {
      // Accumulating negatively avoids surprises near MAX_VALUE
      final int digit = Character.digit(s.charAt(i++), radix);
      if (digit < 0 || result < multmin)
        return defaultValue;

      result *= radix;
      if (result < limit + digit)
        return defaultValue;

      result -= digit;
    }

    return negative ? result : -result;
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Integer#parseInt(String,int)}, but returns {@code defaultValue} if
   * the string does not contain a parsable {@code int}.
   *
   * @param cbuf A {@code char} array containing the {@code int} representation
   *          to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code int} value to be returned if the string does
   *          not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or
   *         {@code defaultValue} if the string does not contain a parsable
   *         {@code int}.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final char[] cbuf, final int radix, final int defaultValue) {
    if (cbuf == null || radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      return defaultValue;

    final int len = cbuf.length;
    boolean negative = false;
    int i = 0;
    int limit = -Integer.MAX_VALUE;

    if (len == 0)
      return defaultValue;

    char firstChar = cbuf[0];
    if (firstChar < '0') { // Possible leading "+" or "-"
      if (firstChar == '-') {
        negative = true;
        limit = Integer.MIN_VALUE;
      }
      else if (firstChar != '+') {
        return defaultValue;
      }

      if (len == 1) { // Cannot have lone "+" or "-"
        return defaultValue;
      }

      ++i;
    }

    final int multmin = limit / radix;
    int result = 0;
    while (i < len) {
      // Accumulating negatively avoids surprises near MAX_VALUE
      final int digit = Character.digit(cbuf[i++], radix);
      if (digit < 0 || result < multmin)
        return defaultValue;

      result *= radix;
      if (result < limit + digit)
        return defaultValue;

      result -= digit;
    }

    return negative ? result : -result;
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Integer#parseInt(String,int)}, but returns {@code null} if the
   * string does not contain a parsable {@code int}.
   *
   * @param s A {@link String} containing the {@link Integer} representation to
   *          be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code int} value represented by the argument, or {@code null}
   *         if the string does not contain a parsable {@code int}.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final String s, final int radix) {
    if (s == null || radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      return null;

    final int len = s.length();
    boolean negative = false;
    int i = 0;
    int limit = -Integer.MAX_VALUE;

    if (len == 0)
      return null;

    char firstChar = s.charAt(0);
    if (firstChar < '0') { // Possible leading "+" or "-"
      if (firstChar == '-') {
        negative = true;
        limit = Integer.MIN_VALUE;
      }
      else if (firstChar != '+') {
        return null;
      }

      if (len == 1) { // Cannot have lone "+" or "-"
        return null;
      }

      ++i;
    }

    final int multmin = limit / radix;
    int result = 0;
    while (i < len) {
      // Accumulating negatively avoids surprises near MAX_VALUE
      final int digit = Character.digit(s.charAt(i++), radix);
      if (digit < 0 || result < multmin)
        return null;

      result *= radix;
      if (result < limit + digit)
        return null;

      result -= digit;
    }

    return negative ? result : -result;
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Integer#parseInt(String,int)}, but returns {@code null} if the
   * string does not contain a parsable {@code int}.
   *
   * @param cbuf A {@code char} array containing the {@link Integer}
   *          representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code int} value represented by the argument, or {@code null}
   *         if the string does not contain a parsable {@code int}.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final char[] cbuf, final int radix) {
    if (cbuf == null || radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      return null;

    final int len = cbuf.length;
    boolean negative = false;
    int i = 0;
    int limit = -Integer.MAX_VALUE;

    if (len == 0)
      return null;

    char firstChar = cbuf[0];
    if (firstChar < '0') { // Possible leading "+" or "-"
      if (firstChar == '-') {
        negative = true;
        limit = Integer.MIN_VALUE;
      }
      else if (firstChar != '+') {
        return null;
      }

      if (len == 1) { // Cannot have lone "+" or "-"
        return null;
      }

      ++i;
    }

    final int multmin = limit / radix;
    int result = 0;
    while (i < len) {
      // Accumulating negatively avoids surprises near MAX_VALUE
      final int digit = Character.digit(cbuf[i++], radix);
      if (digit < 0 || result < multmin)
        return null;

      result *= radix;
      if (result < limit + digit)
        return null;

      result -= digit;
    }

    return negative ? result : -result;
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Long#parseLong(String)}, but returns {@code null} if the string does
   * not contain a parsable {@code long}.
   *
   * @param s A {@link String} containing the {@link Long} representation to be
   *          parsed.
   * @return The {@code long} value represented by the argument, or {@code null}
   *         if the string does not contain a parsable {@code long}.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final String s) {
    return parseLong(s, 10);
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Long#parseLong(String)}, but returns {@code defaultValue} if the
   * string does not contain a parsable {@code long}.
   *
   * @param s A {@link String} containing the {@code long} representation to be
   *          parsed.
   * @param defaultValue The {@code long} value to be returned if the string
   *          does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or
   *         {@code defaultValue} if the string does not contain a parsable
   *         {@code long}.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final String s, final long defaultValue) {
    return parseLong(s, 10, defaultValue);
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Long#parseLong(String)}, but returns {@code defaultValue} if the
   * string does not contain a parsable {@code long}.
   *
   * @param cbuf A {@code char} array containing the {@code long} representation
   *          to be parsed.
   * @param defaultValue The {@code long} value to be returned if the string
   *          does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or
   *         {@code defaultValue} if the string does not contain a parsable
   *         {@code long}.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final char[] cbuf, final long defaultValue) {
    return parseLong(cbuf, 10, defaultValue);
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Long#parseLong(String,int)}, but returns {@code null} if the string
   * does not contain a parsable {@code long}.
   *
   * @param s A {@link String} containing the {@link Long} representation to be
   *          parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code long} value represented by the argument, or {@code null}
   *         if the string does not contain a parsable {@code long}.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final String s, final int radix) {
    if (s == null || radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      return null;

    final int len = s.length();
    boolean negative = false;
    int i = 0;
    long limit = -Long.MAX_VALUE;

    if (len == 0)
      return null;

    char firstChar = s.charAt(0);
    if (firstChar < '0') { // Possible leading "+" or "-"
      if (firstChar == '-') {
        negative = true;
        limit = Long.MIN_VALUE;
      }
      else if (firstChar != '+') {
        return null;
      }

      if (len == 1) { // Cannot have lone "+" or "-"
        return null;
      }

      ++i;
    }

    final long multmin = limit / radix;
    long result = 0;
    while (i < len) {
      // Accumulating negatively avoids surprises near MAX_VALUE
      final int digit = Character.digit(s.charAt(i++), radix);
      if (digit < 0 || result < multmin)
        return null;

      result *= radix;
      if (result < limit + digit)
        return null;

      result -= digit;
    }

    return negative ? result : -result;
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Long#parseLong(String,int)}, but returns {@code null} if the string
   * does not contain a parsable {@code long}.
   *
   * @param cbuf A {@code char} array containing the {@link Long} representation
   *          to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code long} value represented by the argument, or {@code null}
   *         if the string does not contain a parsable {@code long}.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final char[] cbuf, final int radix) {
    if (cbuf == null || radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      return null;

    final int len = cbuf.length;
    boolean negative = false;
    int i = 0;
    long limit = -Long.MAX_VALUE;

    if (len == 0)
      return null;

    char firstChar = cbuf[0];
    if (firstChar < '0') { // Possible leading "+" or "-"
      if (firstChar == '-') {
        negative = true;
        limit = Long.MIN_VALUE;
      }
      else if (firstChar != '+') {
        return null;
      }

      if (len == 1) { // Cannot have lone "+" or "-"
        return null;
      }

      ++i;
    }

    final long multmin = limit / radix;
    long result = 0;
    while (i < len) {
      // Accumulating negatively avoids surprises near MAX_VALUE
      final int digit = Character.digit(cbuf[i++], radix);
      if (digit < 0 || result < multmin)
        return null;

      result *= radix;
      if (result < limit + digit)
        return null;

      result -= digit;
    }

    return negative ? result : -result;
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Long#parseLong(String,int)}, but returns {@code defaultValue} if the
   * string does not contain a parsable {@code long}.
   *
   * @param s A {@link String} containing the {@code long} representation to be
   *          parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code long} value to be returned if the string
   *          does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or
   *         {@code defaultValue} if the string does not contain a parsable
   *         {@code long}.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final String s, final int radix, final long defaultValue) {
    if (s == null || radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      return defaultValue;

    final int len = s.length();
    boolean negative = false;
    int i = 0;
    long limit = -Long.MAX_VALUE;

    if (len == 0)
      return defaultValue;

    char firstChar = s.charAt(0);
    if (firstChar < '0') { // Possible leading "+" or "-"
      if (firstChar == '-') {
        negative = true;
        limit = Long.MIN_VALUE;
      }
      else if (firstChar != '+') {
        return defaultValue;
      }

      if (len == 1) { // Cannot have lone "+" or "-"
        return defaultValue;
      }

      ++i;
    }

    final long multmin = limit / radix;
    long result = 0;
    while (i < len) {
      // Accumulating negatively avoids surprises near MAX_VALUE
      final int digit = Character.digit(s.charAt(i++), radix);
      if (digit < 0 || result < multmin)
        return defaultValue;

      result *= radix;
      if (result < limit + digit)
        return defaultValue;

      result -= digit;
    }

    return negative ? result : -result;
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Long#parseLong(String,int)}, but returns {@code defaultValue} if the
   * string does not contain a parsable {@code long}.
   *
   * @param cbuf A {@code char} array containing the {@code long} representation
   *          to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code long} value to be returned if the string
   *          does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or
   *         {@code defaultValue} if the string does not contain a parsable
   *         {@code long}.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final char[] cbuf, final int radix, final long defaultValue) {
    if (cbuf == null || radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      return defaultValue;

    final int len = cbuf.length;
    boolean negative = false;
    int i = 0;
    long limit = -Long.MAX_VALUE;

    if (len == 0)
      return defaultValue;

    char firstChar = cbuf[0];
    if (firstChar < '0') { // Possible leading "+" or "-"
      if (firstChar == '-') {
        negative = true;
        limit = Long.MIN_VALUE;
      }
      else if (firstChar != '+') {
        return defaultValue;
      }

      if (len == 1) { // Cannot have lone "+" or "-"
        return defaultValue;
      }

      ++i;
    }

    final long multmin = limit / radix;
    long result = 0;
    while (i < len) {
      // Accumulating negatively avoids surprises near MAX_VALUE
      final int digit = Character.digit(cbuf[i++], radix);
      if (digit < 0 || result < multmin)
        return defaultValue;

      result *= radix;
      if (result < limit + digit)
        return defaultValue;

      result -= digit;
    }

    return negative ? result : -result;
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Float#parseFloat(String)}, but returns {@code null} if the string
   * does not contain a parsable {@code float}.
   *
   * @param s A {@link String} containing the {@link Float} representation to be
   *          parsed.
   * @return The {@code float} value represented by the argument, or
   *         {@code null} if the string does not contain a parsable
   *         {@code float}.
   * @see Float#parseFloat(String)
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
   * {@link Float#parseFloat(String)}, but returns {@code defaultValue} if the
   * string does not contain a parsable {@code float}.
   *
   * @param s A {@link String} containing the {@link Float} representation to be
   *          parsed.
   * @param defaultValue The {@code float} value to be returned if the string
   *          does not contain a parsable {@code float}.
   * @return The {@code float} value represented by the argument, or
   *         {@code defaultValue} if the string does not contain a parsable
   *         {@code float}.
   * @see Float#parseFloat(String)
   */
  public static float parseFloat(final String s, final float defaultValue) {
    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if
    // FIXME: the implementation is copied.
    try {
      return s == null ? defaultValue : Float.parseFloat(s);
    }
    catch (final NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Double#parseDouble(String)}, but returns {@code null} if the string
   * does not contain a parsable {@code double}.
   *
   * @param s A {@link String} containing the {@link Double} representation to
   *          be parsed.
   * @return The {@code double} value represented by the argument, or
   *         {@code null} if the string does not contain a parsable
   *         {@code double}.
   * @see Double#parseDouble(String)
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

  /**
   * Parses the string argument as per the specification of
   * {@link Double#parseDouble(String)}, but returns {@code defaultValue} if the
   * string does not contain a parsable {@code double}.
   *
   * @param s A {@link String} containing the {@link Double} representation to
   *          be parsed.
   * @param defaultValue The {@code double} value to be returned if the string
   *          does not contain a parsable {@code double}.
   * @return The {@code double} value represented by the argument, or
   *         {@code defaultValue} if the string does not contain a parsable
   *         {@code double}.
   * @see Double#parseDouble(String)
   */
  public static double parseDouble(final String s, final double defaultValue) {
    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if
    // FIXME: the implementation is copied.
    try {
      return s == null ? defaultValue : Double.parseDouble(s);
    }
    catch (final NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * Returns an {@code int} array representation of the values in the specified
   * {@link String} array.
   *
   * @param s The {@link String} array.
   * @return An {@code int} array representation of the values in the specified
   *         {@link String} array.
   * @throws NumberFormatException If a string in the specified array does not
   *           contain a parsable integer.
   */
  public static int[] parseInt(final String ... s) {
    final int[] values = new int[s.length];
    for (int i = 0; i < s.length; ++i)
      values[i] = Integer.parseInt(s[i]);

    return values;
  }

  /**
   * Returns a {@code double} array representation of the values in the
   * specified {@link String} array.
   *
   * @param s The {@link String} array.
   * @return An {@code int} array representation of the values in the specified
   *         {@link String} array.
   * @throws NumberFormatException If a string in the specified array does not
   *           contain a parsable double.
   */
  public static double[] parseDouble(final String ... s) {
    final double[] values = new double[s.length];
    for (int i = 0; i < s.length; ++i)
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
      scalar += (double)Integer.parseInt(string.substring(0, slash)) / (double)Integer.parseInt(string.substring(slash + 1));
    else
      scalar += new BigDecimal(string).doubleValue();

    return scalar;
  }

  /**
   * Tests whether the specified string represents a number, or a number with
   * a fraction of two numbers (i.e. {@code 23 3/4}).
   * <p>
   * This method supports exponent form (i.e. {@code 3.2E-5}).
   *
   * @param s The string to test.
   * @return {@code true} if the specified string represents a number, or a
   *         number with a fraction of two numbers.
   */
  public static boolean isNumber(String s) {
    if (s == null || (s = s.trim()).length() == 0)
      return false;

    final String[] parts = s.split(" ");
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
    for (int i = string.length() - 1; i >= 0; --i) {
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
        else if (!expEncountered && c != '+') {
          return false;
        }
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

        ++factor;
      }
    }

    return true;
  }

  /**
   * Assert the specified radix is within legal range.
   *
   * @param radix The radix to assert.
   * @throws NumberFormatException If the specified radix is outside the range
   *           of legal values.
   */
  private static void assertRadix(final int radix) {
    if (radix < Character.MIN_RADIX)
      throw new NumberFormatException("radix " + radix + " less than Character.MIN_RADIX");

    if (radix > Character.MAX_RADIX)
      throw new NumberFormatException("radix " + radix + " greater than Character.MAX_RADIX");
  }

  /**
   * Determines if the specified character is a digit in the provided radix.
   *
   * @param digit The character to test.
   * @param radix The radix to test against.
   * @return {@code true} if the character is a digit in the provided radix;
   *         {@code false} otherwise.
   * @throws NumberFormatException If the specified radix is outside the range
   *           of legal values.
   */
  public static boolean isDigit(final char digit, final int radix) {
    final int val = digit(digit, radix);
    return 0 <= val && val < radix;
  }

  /**
   * Returns the numeric value of the specified character representing a digit.
   * The specified character must be within the following ranges:
   * <ol>
   * <li>{@code '0' <= digit && digit <= '9'}</li>
   * <li>{@code 'a' <= digit && digit <= 'a'}</li>
   * <li>{@code 'A' <= digit && digit <= 'Z'}</li>
   * </ol>
   * <p>
   * If the specified character is outside these ranges, the value
   * {@code -digit} is returned.
   *
   * @param digit The character representing a digit.
   * @param radix The radix to be used to transform the character.
   * @return The numeric value of the specified character representing a digit.
   * @throws NumberFormatException If the specified radix is outside the range
   *           of legal values.
   */
  public static int digit(final char digit, final int radix) {
    assertRadix(radix);
    if ('0' <= digit && digit <= '9')
      return digit - '0';

    if ('a' <= digit && digit <= 'z')
      return digit + 10 - 'a';

    if ('A' <= digit && digit <= 'Z')
      return digit + 10 - 'A';

    return -digit;
  }

  /**
   * Returns {@code true} if the specified {@code float} can be represented as
   * a whole number without loss of precision.
   *
   * @param n The {@code float} to test.
   * @return {@code true} if the specified {@code float} can be represented as
   *         a whole number without loss of precision.
   */
  public static boolean isWhole(final float n) {
    return (int)n == n;
  }

  /**
   * Returns {@code true} if the specified {@code double} can be represented as
   * a whole number without loss of precision.
   *
   * @param n The {@code double} to test.
   * @return {@code true} if the specified {@code double} can be represented as
   *         a whole number without loss of precision.
   */
  public static boolean isWhole(final double n) {
    return (long)n == n;
  }

  /**
   * Returns the multiple of the specified values, throwing an
   * {@link ArithmeticException} if the resulting value cannot be represented as
   * a {@code long} due to overflow.
   *
   * @param a The first value.
   * @param b The second value.
   * @return The multiple of the specified values.
   * @throws ArithmeticException If the resulting value cannot be represented as
   *           a {@code long} due to overflow.
   */
  public static long checkedMultiple(final long a, final long b) {
    final long maximum = Long.signum(a) == Long.signum(b) ? Long.MAX_VALUE : Long.MIN_VALUE;
    if (a != 0 && (b > 0 && b > maximum / a || b < 0 && b < maximum / a))
      throw new ArithmeticException("long overflow");

    return a * b;
  }

  /**
   * Returns a string representation of the specified {@code double} to the
   * provided number of decimal places.
   *
   * @param n The {@code double}.
   * @param decimals The number of decimal places.
   * @return A string representation of the specified {@code double} to the
   *         provided number of decimal places.
   */
  public static String toString(final double n, final int decimals) {
    final double factor = Math.pow(10, decimals);
    return String.valueOf(Math.round(n * factor) / factor);
  }

  /**
   * Strips trailing zeroes after a decimal point in the specified string.
   * <p>
   * This method accepts a string that represents a number. The behavior of this
   * method is undefined for non-number string.
   *
   * @param number The {@link String} representing a number.
   * @return The string with trailing zeroes stripped if the string represents a
   *         decimal number; otherwise the original string is returned.
   */
  public static String stripTrailingZeros(final String number) {
    if (number == null || number.length() == 0)
      return number;

    final int len = number.length();
    int i = len;
    while (i-- > 1)
      if (number.charAt(i) != '0' && number.charAt(i) != '.')
        break;

    ++i;
    return i == len || number.lastIndexOf('.', i + 1) == -1 ? number : number.substring(0, i);
  }

  // FIXME: This is not working! it is returning incorrect results
  public static double log2(BigInteger value) {
    final int blex = value.bitLength() - 1022; // any value in 60..1023 is ok
    if (blex > 0)
      value = value.shiftRight(blex);

    final double result = Math.log(value.doubleValue());
    return blex > 0 ? result + blex * LOG_2 : result;
  }

  /**
   * Determines if the difference of the specified {@link Number} values is less
   * than the provided epsilon.
   *
   * @param a The first {@link Number}.
   * @param b The second {@link Number}.
   * @param epsilon The epsilon.
   * @return {@code true} if the difference of the specified {@link Number}
   *         values is less than the provided epsilon.
   */
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
        return a.equals(BigInteger.valueOf(b.byteValue()));

      if (b instanceof Short)
        return a.equals(BigInteger.valueOf(b.shortValue()));

      if (b instanceof Integer)
        return a.equals(BigInteger.valueOf(b.intValue()));

      if (b instanceof Long)
        return a.equals(BigInteger.valueOf(b.longValue()));

      if (b instanceof BigInteger)
        return a.equals(b);

      if (b instanceof BigDecimal)
        return new BigDecimal((BigInteger)a).equals(b);

      if (b instanceof Float)
        return a.equals(BigDecimal.valueOf(b.floatValue()).toBigInteger());

      if (b instanceof Double)
        return a.equals(BigDecimal.valueOf(b.doubleValue()).toBigInteger());
    }
    else if (a instanceof BigDecimal) {
      if (b instanceof Byte)
        return a.equals(BigDecimal.valueOf(b.byteValue()));

      if (b instanceof Short)
        return a.equals(BigDecimal.valueOf(b.shortValue()));

      if (b instanceof Integer)
        return a.equals(BigDecimal.valueOf(b.intValue()));

      if (b instanceof Long)
        return a.equals(BigDecimal.valueOf(b.longValue()));

      if (b instanceof BigInteger)
        return a.equals(new BigDecimal((BigInteger)b));

      if (b instanceof BigDecimal)
        return a.equals(b);

      if (b instanceof Float)
        return a.equals(BigDecimal.valueOf(b.floatValue()).toBigInteger());

      if (b instanceof Double)
        return a.equals(BigDecimal.valueOf(b.doubleValue()));
    }

    return Math.abs(a.doubleValue() - b.doubleValue()) < epsilon;
  }

  /**
   * Returns the precision necessary to represent the specified number.
   *
   * @param number The number.
   * @return The precision necessary to represent the specified number.
   */
  public static byte precision(final int number) {
    return (byte)(number == 0 ? 1 : Math.log10(number != Integer.MIN_VALUE ? Math.abs(number) : Math.abs((long)number)) + 1);
  }

  /**
   * Returns the precision necessary to represent the specified number.
   *
   * @param number The number.
   * @return The precision necessary to represent the specified number.
   */
  public static byte precision(final long number) {
    return number != Long.MIN_VALUE ? (byte)(Math.log10(Math.abs(number)) + 1) : (byte)precision(BigInteger.valueOf(number));
  }

  /**
   * Returns the precision necessary to represent the specified number.
   *
   * @param number The number.
   * @return The precision necessary to represent the specified number.
   * @throws NullPointerException If {@code number} is null.
   */
  public static int precision(final BigInteger number) {
    return number.abs().toString().length();
  }

  /**
   * Returns the precision necessary to represent the specified number.
   *
   * @param number The number.
   * @return The precision necessary to represent the specified number.
   * @throws NullPointerException If {@code number} is null.
   */
  public static int precision(final BigDecimal number) {
    return number.precision();
  }

  /**
   * Returns the number of trailing zeroes in the specified number.
   *
   * @param number The number.
   * @return The number of trailing zeroes in the specified number.
   */
  public static int trailingZeroes(int number) {
    int zeros = 0;
    while (number % 10 == 0 && number != 0) {
      ++zeros;
      number /= 10;
    }

    return zeros;
  }

  /**
   * Returns the number of trailing zeroes in the specified number.
   *
   * @param number The number.
   * @return The number of trailing zeroes in the specified number.
   */
  public static int trailingZeroes(long number) {
    int zeroes = 0;
    while (number % 10 == 0 && number != 0) {
      ++zeroes;
      number /= 10;
    }

    return zeroes;
  }

  /**
   * Returns the number of decimal places represented in the specified
   * {@link BigInteger}.
   *
   * @param n The {@link BigInteger} from which to determine the number of
   *          decimal places.
   * @return The number of decimal places represented in the specified
   *         {@link BigInteger}.
   * @throws NullPointerException If {@code n} is null.
   */
  public static int numberOfDecimalPlaces(final BigDecimal n) {
    return Math.max(0, n.stripTrailingZeros().scale());
  }

  /**
   * Returns the {@link BigDecimal} representation of the specified
   * {@link Number}.
   *
   * @param n The {@link Number} to convert to a {@link BigDecimal}.
   * @return The {@link BigDecimal} representation of the specified
   *         {@link Number}.
   * @throws NullPointerException If {@code n} is null.
   */
  public static BigDecimal toBigDecimal(final Number n) {
    if (n instanceof BigDecimal)
      return (BigDecimal)n;

    if (n instanceof BigInteger)
      return new BigDecimal((BigInteger)n);

    if (n instanceof Byte)
      return BigDecimal.valueOf(n.byteValue());

    if (n instanceof Short)
      return BigDecimal.valueOf(n.shortValue());

    if (n instanceof Integer)
      return BigDecimal.valueOf(n.intValue());

    if (n instanceof Long)
      return BigDecimal.valueOf(n.longValue());

    if (n instanceof Float)
      return BigDecimal.valueOf(n.floatValue()).stripTrailingZeros();

    return BigDecimal.valueOf(n.doubleValue()).stripTrailingZeros();
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If {@code numbers.length == 0}.
   */
  public static BigDecimal average(final BigDecimal ... numbers) {
    BigDecimal sum = numbers[0];
    for (int i = 1; i < numbers.length; ++i)
      sum = sum.add(numbers[i]);

    return sum.divide(BigDecimal.valueOf(numbers.length));
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If {@code numbers.length == 0}.
   */
  public static BigDecimal average(final BigInteger ... numbers) {
    BigDecimal sum = new BigDecimal(numbers[0]);
    for (int i = 1; i < numbers.length; ++i)
      sum = sum.add(new BigDecimal(numbers[i]));

    return sum.divide(BigDecimal.valueOf(numbers.length));
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If {@code numbers.length == 0}.
   */
  public static double average(final byte ... numbers) {
    long sum = numbers[0];
    for (int i = 1; i < numbers.length; ++i)
      sum += numbers[i];

    return sum / numbers.length;
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If {@code numbers.length == 0}.
   */
  public static double average(final short ... numbers) {
    long sum = numbers[0];
    for (int i = 1; i < numbers.length; ++i)
      sum += numbers[i];

    return sum / numbers.length;
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If {@code numbers.length == 0}.
   */
  public static double average(final int ... numbers) {
    long sum = numbers[0];
    for (int i = 1; i < numbers.length; ++i)
      sum += numbers[i];

    return sum / numbers.length;
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If {@code numbers.length == 0}.
   */
  public static double average(final long ... numbers) {
    long sum = numbers[0];
    for (int i = 1; i < numbers.length; ++i)
      sum += numbers[i];

    return sum / numbers.length;
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If {@code numbers.length == 0}.
   */
  public static double average(final float ... numbers) {
    double sum = numbers[0];
    for (int i = 1; i < numbers.length; ++i)
      sum += numbers[i];

    return sum / numbers.length;
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If {@code numbers.length == 0}.
   */
  public static double average(final double ... numbers) {
    double sum = numbers[0];
    for (int i = 1; i < numbers.length; ++i)
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