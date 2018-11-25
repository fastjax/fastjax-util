/* Copyright (c) 2006 FastJAX
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

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public final class Strings {
  private static final char[] alphaNumeric = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
  private static final SecureRandom secureRandom = new SecureRandom();

  private static String getRandom(final SecureRandom secureRandom, final int length, final int start, final int len) {
    if (length == 0)
      return "";

    if (length < 0)
      throw new IllegalArgumentException("Length must be non-negative: " + length);

    final char[] array = new char[length];
    for (int i = 0; i < length; ++i)
      array[i] = alphaNumeric[start + secureRandom.nextInt(len)];

    return new String(array);
  }

  /**
   * Returns a randomly constructed alphanumeric string of the specified length.
   *
   * @param secureRandom The {@link SecureRandom} instance for generation of
   *          random values.
   * @param len The length of the string to construct.
   * @return A randomly constructed alphanumeric string of the specified length.
   */
  public static String getRandomAlphaNumeric(final SecureRandom secureRandom, final int len) {
    return getRandom(secureRandom, len, 0, alphaNumeric.length);
  }

  /**
   * Returns a randomly constructed alphanumeric string of the specified length.
   * <p>
   * This method uses a static {@link SecureRandom} instance for generation of
   * random values.
   *
   * @param len The length of the string to construct.
   * @return A randomly constructed alphanumeric string of the specified length.
   */
  public static String getRandomAlphaNumeric(final int len) {
    return getRandom(secureRandom, len, 0, alphaNumeric.length);
  }

  /**
   * Returns a randomly constructed alpha string of the specified length.
   *
   * @param secureRandom The {@link SecureRandom} instance for generation of
   *          random values.
   * @param len The length of the string to construct.
   * @return A randomly constructed alpha string of the specified length.
   */
  public static String getRandomAlpha(final SecureRandom secureRandom, final int len) {
    return getRandom(secureRandom, len, 0, alphaNumeric.length - 10);
  }

  /**
   * Returns a randomly constructed alpha string of the specified length.
   * <p>
   * This method uses a static {@link SecureRandom} instance for generation of
   * random values.
   *
   * @param len The length of the string to construct.
   * @return A randomly constructed alpha string of the specified length.
   */
  public static String getRandomAlpha(final int len) {
    return getRandom(secureRandom, len, 0, alphaNumeric.length - 10);
  }

  /**
   * Returns a randomly constructed numeric string of the specified length.
   *
   * @param secureRandom The {@link SecureRandom} instance for generation of
   *          random values.
   * @param len The length of the string to construct.
   * @return A randomly constructed numeric string of the specified length.
   */
  public static String getRandomNumeric(final SecureRandom secureRandom, final int len) {
    return getRandom(secureRandom, len, alphaNumeric.length - 10, 10);
  }

  /**
   * Returns a randomly constructed numeric string of the specified length.
   * <p>
   * This method uses a static {@link SecureRandom} instance for generation of
   * random values.
   *
   * @param len The length of the string to construct.
   * @return A randomly constructed numeric string of the specified length.
   */
  public static String getRandomNumeric(final int len) {
    return getRandom(secureRandom, len, alphaNumeric.length - 10, 10);
  }

  private static boolean interpolateShallow(final StringBuilder text, final Map<String,String> properties, final String open, final String close) {
    boolean changed = false;
    for (int start = text.length() - close.length() - 1; (start = text.lastIndexOf(open, start - 1)) > -1;) {
      final int end = text.indexOf(close, start + open.length());
      if (end < start)
        continue;

      final String key = text.substring(start + open.length(), end);
      final String value = properties.get(key);
      if (value != null) {
        text.replace(start, end + close.length(), value);
        changed = true;
      }
    }

    return changed;
  }

  private static String interpolateDeep(final StringBuilder text, final Map<String,String> properties, final String prefix, final String suffix) {
    final int max = properties.size() * properties.size();
    for (int i = 0; interpolateShallow(text, properties, prefix, suffix); ++i)
      if (i == max)
        throw new IllegalArgumentException("Loop detected");

    return text.toString();
  }

  /**
   * Interpolates all the <i>value</i> strings in the specified {@code Map} by
   * matching {@code prefix + value + suffix}, where <i>value</i> is a
   * <i>key</i> in the {@code Map}, and replacing it with the value from the
   * {@code Map}.
   * <p>
   * This performance of this algorithm is {@code O(n^2)} by nature. If the
   * specified {@code Map} has {@code key=value} entries that result in a loop,
   * this method will throw a {@code IllegalArgumentException}.
   * <p>
   * <blockquote>
   * <i><b>Example:</b></i>
   * <p>
   * <table>
   * <caption>Input, with prefix=<code>"${"</code>, and suffix=<code>"}"</code></caption>
   * <tr><td><b>Key</b></td><td><b>Value</b></td></tr>
   * <tr><td>title</td><td>The ${subject} jumps over the ${object}</td></tr>
   * <tr><td>subject</td><td>${adj1} fox</td></tr>
   * <tr><td>object</td><td>${adj2} dog</td></tr>
   * <tr><td>adj1</td><td>quick brown</td></tr>
   * <tr><td>adj2</td><td>lazy</td></tr>
   * </table>
   * <p>
   * <table>
   * <caption>Output</code></caption>
   * <tr><td><b>Key</b></td><td><b>Value</b></td></tr>
   * <tr><td>title</td><td>The quick brown fox jumps over the lazy dog</td></tr>
   * <tr><td>subject</td><td>quick brown fox</td></tr>
   * <tr><td>object</td><td>lazy dog</td></tr>
   * <tr><td>adj1</td><td>quick brown</td></tr>
   * <tr><td>adj2</td><td>lazy</td></tr>
   * </table>
   * </blockquote>
   *
   * @param properties The map to interpolate.
   * @param prefix String prefixing the key name.
   * @param suffix String suffixing the key name.
   * @return The specified map, with its values interpolated.
   * @see #interpolate(String,Map,String,String)
   * @throws IllegalArgumentException If the specified {@code properties} has
   *           {@code key=value} entries that result in a loop.
   * @throws NullPointerException If {@code properties}, {@code prefix}, or
   *           {@code suffix} are null.
   */
  public static Map<String,String> interpolate(final Map<String,String> properties, final String prefix, final String suffix) {
    Objects.requireNonNull(properties);
    Objects.requireNonNull(prefix);
    Objects.requireNonNull(suffix);
    for (final Map.Entry<String,String> entry : properties.entrySet())
      if (entry.getValue() != null)
        entry.setValue(interpolateDeep(new StringBuilder(entry.getValue()), properties, prefix, suffix));

    return properties;
  }

  /**
   * Interpolates the specified string by matching {@code prefix + key + suffix}
   * substring and replacing it with the <i>value</i> of the {@code key=value}
   * mapping in the properties {@code Map}.
   * <p>
   * <blockquote>
   * <i><b>Example:</b></i>
   * <p>
   * <b>Input</b>: text=<code>The ${subject} jumps over the ${object}</code>,
   * prefix=<code>"${"</code>, suffix=<code>"}"</code>
   * <p>
   * <table>
   * <caption>Properties</caption>
   * <tr><td><b>Key</b></td><td><b>Value</b></td></tr>
   * <tr><td>subject</td><td>quick brown fox</td></tr>
   * <tr><td>object</td><td>lazy dog</td></tr>
   * </table>
   * <p>
   * <b>Output</b>: {@code The quick brown fox jumps over the lazy dog}
   * </blockquote>
   *
   * @param text The string to interpolate.
   * @param properties The map with key=value entries for interpolation.
   * @param prefix String prefixing the key name.
   * @param suffix String suffixing the key name.
   * @return The interpolated string.
   * @see #interpolate(Map,String,String)
   * @throws NullPointerException If {@code text}, {@code properties},
   *           {@code prefix}, or {@code suffix} are null.
   */
  public static String interpolate(final String text, final Map<String,String> properties, final String prefix, final String suffix) {
    return interpolateDeep(new StringBuilder(Objects.requireNonNull(text)), Objects.requireNonNull(properties), Objects.requireNonNull(prefix), Objects.requireNonNull(suffix));
  }

  /**
   * Replaces each substring in the specified {@code StringBuilder} that matches
   * the literal target sequence with the specified literal replacement
   * sequence. The replacement proceeds from the beginning of the string to the
   * end, for example, replacing "aa" with "b" in the string "aaa" will result
   * in "ba" rather than "ab".
   *
   * @param builder The {@link StringBuilder}.
   * @param target The sequence of char values to be replaced
   * @param replacement The replacement sequence of char values
   * @return The resulting string
   * @throws NullPointerException If {@code builder}, {@code target}, or
   *           {@code replacement} are null.
   * @see String#replace(CharSequence, CharSequence)
   */
  public static StringBuilder replace(final StringBuilder builder, final CharSequence target, final CharSequence replacement) {
    final String tgtStr = target.toString();
    final String replStr = replacement.toString();
    int j = builder.lastIndexOf(tgtStr);
    if (j < 0)
      return builder;

    final int tgtLen = tgtStr.length();
    final int tgtLen1 = Math.max(tgtLen, 1);
    final int thisLen = builder.length();

    final int newLenHint = thisLen - tgtLen + replStr.length();
    if (newLenHint < 0)
      throw new OutOfMemoryError();

    do {
      builder.replace(j, j + tgtLen1, replStr);
    }
    while ((j = builder.lastIndexOf(tgtStr, j - tgtLen1)) > -1);
    return builder;
  }

  /**
   * Replaces each substring of the specified {@link StringBuilder} that matches
   * the given {@code target} sequence with the given {@code replacement}
   * sequence.
   *
   * @param builder The {@link StringBuilder} in which all substrings are to be
   *          replaced.
   * @param target The sequence to be replaced.
   * @param replacement The sequence to be substituted for each match.
   * @return The specified {@link StringBuilder} instance.
   */
  public static StringBuilder replaceAll(final StringBuilder builder, final CharSequence target, final CharSequence replacement) {
    while (builder.length() != replace(builder, target, replacement).length());
    return builder;
  }

  /**
   * Tests if the specified {@link StringBuilder} starts with the specified
   * prefix.
   *
   * @param builder The {@link StringBuilder}.
   * @param prefix The prefix.
   * @return {@code true} if the {@code prefix} character sequence is a prefix
   *         of {@code builder}; {@code false} otherwise. Note also that
   *         {@code true} will be returned if {@code prefix} is an empty string
   *         or is equal to {@code builder}.
   * @throws NullPointerException If {@code builder} or {@code prefix} are null.
   */
  public static boolean startsWith(final StringBuilder builder, final CharSequence prefix) {
    if (prefix.length() == 0)
      return true;

    if (builder.length() < prefix.length())
      return false;

    for (int i = 0; i < prefix.length(); ++i)
      if (builder.charAt(i) != prefix.charAt(i))
        return false;

    return true;
  }

  /**
   * Tests if the specified {@link StringBuilder} ends with the specified
   * suffix.
   *
   * @param builder The {@link StringBuilder}.
   * @param suffix The suffix.
   * @return {@code true} if the {@code suffix} character sequence is a suffix
   *         of {@code builder}; {@code false} otherwise. Note also that
   *         {@code true} will be returned if {@code suffix} is an empty string
   *         or is equal to {@code builder}.
   * @throws NullPointerException If {@code builder} or {@code suffix} are null.
   */
  public static boolean endsWith(final StringBuilder builder, final CharSequence suffix) {
    if (suffix.length() == 0)
      return true;

    if (builder.length() < suffix.length())
      return false;

    final int offset = builder.length() - suffix.length();
    for (int i = suffix.length() - 1; i >= 0; --i)
      if (builder.charAt(offset + i) != suffix.charAt(i))
        return false;

    return true;
  }

  /**
   * Converts the characters in the specified {@link StringBuilder} to lowercase
   * using case mapping information from the UnicodeData file.
   *
   * @param builder The {@link StringBuilder}.
   * @return The specified {@link StringBuilder}, with its characters converted to lowercase.
   * @throws NullPointerException If {@code builder} is null.
   * @see Character#toLowerCase(char)
   */
  public static StringBuilder toLowerCase(final StringBuilder builder) {
    for (int i = 0; i < builder.length(); ++i)
      builder.setCharAt(i, Character.toLowerCase(builder.charAt(i)));

    return builder;
  }

  /**
   * Converts the characters in the specified {@link StringBuilder} to uppercase
   * using case mapping information from the UnicodeData file.
   *
   * @param builder The {@link StringBuilder}.
   * @return The specified {@link StringBuilder}, with its characters converted to uppercase.
   * @throws NullPointerException If {@code builder} is null.
   * @see Character#toUpperCase(char)
   */
  public static StringBuilder toUppereCase(final StringBuilder builder) {
    for (int i = 0; i < builder.length(); ++i)
      builder.setCharAt(i, Character.toUpperCase(builder.charAt(i)));

    return builder;
  }

  private static StringBuilder changeCase(final StringBuilder builder, final boolean upper, final int beginIndex, final int endIndex) {
    if (builder.length() == 0)
      return builder;

    if (endIndex < beginIndex)
      throw new IllegalArgumentException("start index (" + beginIndex + ") > end index (" + endIndex + ")");

    if (builder.length() < beginIndex)
      throw new IllegalArgumentException("start index (" + beginIndex + ") > string length (" + builder.length() + ")");

    if (beginIndex == endIndex)
      return builder;

    for (int i = beginIndex; i < endIndex; ++i)
      builder.setCharAt(i, upper ? Character.toUpperCase(builder.charAt(i)) : Character.toLowerCase(builder.charAt(i)));

    return builder;
  }

  /**
   * Converts the characters in the specified {@link StringBuilder} spanning the
   * provided index range to lowercase using case mapping information from the
   * UnicodeData file.
   *
   * @param builder The {@link StringBuilder}.
   * @param beginIndex The beginning index, inclusive.
   * @param endIndex The ending index, exclusive.
   * @return The specified {@link StringBuilder}, with the characters spanning
   *         the index range converted to lowercase.
   * @exception IllegalArgumentException If the {@code beginIndex} is negative,
   *              or {@code endIndex} is larger than the length of the
   *              {@code StringBuilder}, or {@code beginIndex} is larger than
   *              {@code endIndex}.
   * @throws NullPointerException If {@code builder} is null.
   * @see Character#toLowerCase(char)
   */
  public static StringBuilder toLowerCase(final StringBuilder builder, final int beginIndex, final int endIndex) {
    return changeCase(builder, false, beginIndex, endIndex);
  }

  /**
   * Converts all of the characters in the specified {@link StringBuilder}
   * starting at the provided begin index to lowercase using case mapping
   * information from the UnicodeData file.
   *
   * @param builder The {@link StringBuilder}.
   * @param beginIndex The beginning index, inclusive.
   * @return The specified {@link StringBuilder}, with all the characters
   *         following the provided begin index converted to lowercase.
   * @exception IllegalArgumentException If the {@code beginIndex} is negative
   *              or larger than the length of the {@code StringBuilder}.
   * @throws NullPointerException If {@code builder} is null.
   * @see Character#toLowerCase(char)
   */
  public static StringBuilder toLowerCase(final StringBuilder builder, final int beginIndex) {
    return changeCase(builder, false, beginIndex, builder.length());
  }

  /**
   * Converts the characters in the specified {@link StringBuilder} spanning the
   * provided index range to uppercase using case mapping information from the
   * UnicodeData file.
   *
   * @param builder The {@link StringBuilder}.
   * @param beginIndex The beginning index, inclusive.
   * @param endIndex The ending index, exclusive.
   * @return The specified {@link StringBuilder}, with the characters spanning
   *         the index range converted to uppercase.
   * @exception IllegalArgumentException If the {@code beginIndex} is negative,
   *              or {@code endIndex} is larger than the length of the
   *              {@code StringBuilder}, or {@code beginIndex} is larger than
   *              {@code endIndex}.
   * @throws NullPointerException If {@code builder} is null.
   * @see Character#toLowerCase(char)
   */
  public static StringBuilder toUpperCase(final StringBuilder builder, final int beginIndex, final int endIndex) {
    return changeCase(builder, true, beginIndex, endIndex);
  }

  /**
   * Converts all of the characters in the specified {@link StringBuilder}
   * starting at the provided begin index to uppercase using case mapping
   * information from the UnicodeData file.
   *
   * @param builder The {@link StringBuilder}.
   * @param beginIndex The beginning index, inclusive.
   * @return The specified {@link StringBuilder}, with all the characters
   *         following the provided begin index converted to uppercase.
   * @exception IllegalArgumentException If the {@code beginIndex} is negative
   *              or larger than the length of the {@code StringBuilder}.
   * @throws NullPointerException If {@code builder} is null.
   * @see Character#toLowerCase(char)
   */
  public static StringBuilder toUpperCase(final StringBuilder builder, final int beginIndex) {
    return changeCase(builder, true, beginIndex, builder.length());
  }

  /**
   * Returns a left-padded representation of the specified length for the
   * provided string. If {@code length > string.length()}, preceding characters
   * are filled with spaces ({@code ' '}). If {@code length == string.length()},
   * the provided string instance is returned. If
   * {@code length < string.length()}, this method throws
   * {@code IllegalArgumentException}.
   * <p>
   * This method is equivalent to calling {@code padLeft(string, length, ' ')}.
   *
   * @param string The string to pad.
   * @param length The length of the returned, padded string.
   * @return A left-padded representation of the specified length for the
   *         provided string.
   * @throws IllegalArgumentException If {@code length} is less than
   *           {@code string.length()}.
   * @throws NullPointerException If {@code string} is null.
   */
  public static String padLeft(final String string, final int length) {
    return pad(string, length, false, ' ');
  }

  /**
   * Returns a left-padded representation of the specified length for the
   * provided string. If {@code length > string.length()}, preceding characters
   * are filled with the specified {@code pad} char. If
   * {@code length == string.length()}, the provided string instance is
   * returned. If {@code length < string.length()}, this method throws
   * {@code IllegalArgumentException}.
   *
   * @param string The string to pad.
   * @param length The length of the returned, padded string.
   * @param pad The padding character.
   * @return A left-padded representation of the specified length for the
   *         provided string.
   * @throws IllegalArgumentException If {@code length} is less than
   *           {@code string.length()}.
   * @throws NullPointerException If {@code string} is null.
   */
  public static String padLeft(final String string, final int length, final char pad) {
    return pad(string, length, false, pad);
  }

  /**
   * Returns a right-padded representation of the specified length for the
   * provided string. If {@code length > string.length()}, ending characters are
   * filled with spaces ({@code ' '}). If {@code length == string.length()}, the
   * provided string instance is returned. If {@code length < string.length()},
   * this method throws {@code IllegalArgumentException}.
   * <p>
   * This method is equivalent to calling {@code padRight(string, length, ' ')}.
   *
   * @param string The string to pad.
   * @param length The length of the returned, padded string.
   * @param pad The padding character.
   * @return A right-padded representation of the specified length for the
   *         provided string.
   * @throws IllegalArgumentException If {@code length} is less than
   *           {@code string.length()}.
   * @throws NullPointerException If {@code string} is null.
   */
  public static String padRight(final String string, final int length) {
    return pad(string, length, true, ' ');
  }

  /**
   * Returns a right-padded representation of the specified length for the
   * provided string. If {@code length > string.length()}, ending characters are
   * filled with the specified {@code pad} char. If
   * {@code length == string.length()}, the provided string instance is
   * returned. If {@code length < string.length()}, this method throws
   * {@code IllegalArgumentException}.
   *
   * @param string The string to pad.
   * @param length The length of the returned, padded string.
   * @return A right-padded representation of the specified length for the
   *         provided string.
   * @throws IllegalArgumentException If {@code length} is less than
   *           {@code string.length()}.
   * @throws NullPointerException If {@code string} is null.
   */
  public static String padRight(final String string, final int length, final char pad) {
    return pad(string, length, true, pad);
  }

  private static String pad(final String string, final int length, final boolean right, final char pad) {
    final int len = string.length();
    if (length == len)
      return string;

    if (length < len)
      throw new IllegalArgumentException("length (" + length + ") must be greater or equal to string length (" + len + ")");

    final char[] chars = new char[length];
    if (right) {
      Arrays.fill(chars, len, length, pad);
      for (int i = 0; i < len; ++i)
        chars[i] = string.charAt(i);
    }
    else {
      final int offset = length - len;
      Arrays.fill(chars, 0, offset, pad);
      for (int i = 0; i < len; ++i)
        chars[i + offset] = string.charAt(i);
    }

    return new String(chars);
  }

  /**
   * Returns the hexadecimal representation of the specified value up to the
   * provided digits. If the number of digits is less than the full length of
   * the hexadecimal representation, the extra most significant digits are
   * truncated. If the number of digits is less than the full length of the
   * hexadecimal representation, the resultant string is left-padded with zeros
   * ({@code '0'}).
   *
   * @param value The value to convert to hexadecimal representation.
   * @param digits The number of digits to return, least significant digits
   *          first.
   * @return The hexadecimal representation of the specified value up to the
   *         provided digits
   */
  static String hex(long value, final int digits) {
    final boolean negative = value < 0;
    if (negative)
      value = -value;

    String hex = Long.toString(value & ((1l << 4 * digits) - 1), 16);
    if (hex.length() < digits)
      hex = padLeft(hex, digits, '0');

    return negative ? "-" + hex : hex;
  }

  /**
   * Returns the UTF-8 literal hexadecimal encoding of the specified
   * {@code char}.
   *
   * @param ch The {@code char} to encode.
   * @return The UTF-8 literal hexadecimal encoding of the specified
   *         {@code char}.
   */
  public static String toUTF8Literal(final char ch) {
    return "\\x" + hex(ch, 2);
  }

  /**
   * Returns the string of UTF-8 literal hexadecimal encodings of characters of
   * the specified {@code String}.
   *
   * @param string The {@code String} to encode.
   * @return The string of UTF-8 literal hexadecimal encodings of characters of
   *         the specified {@code String}.
   */
  public static String toUTF8Literal(final String string) {
    final int len = string.length();
    final StringBuilder builder = new StringBuilder(len * 4);
    for (int i = 0; i < len; ++i)
      builder.append(toUTF8Literal(string.charAt(i)));

    return builder.toString();
  }

  public static String getAlpha(final int number) {
    int scale;
    return number < '{' - 'a' ? String.valueOf((char)('a' + number)) : getAlpha((scale = number / ('{' - 'a')) - 1) + String.valueOf((char)('a' + number - scale * ('{' - 'a')));
  }

  /**
   * Returns the prefix string that is shared amongst all members for the
   * specified {@code String} array.
   *
   * @param strings The {@code String} array in which to find a common prefix.
   * @return The prefix string that is shared amongst all members for the
   *         specified {@code String} array.
   */
  public static String getCommonPrefix(final String ... strings) {
    if (strings == null || strings.length == 0)
      return null;

    if (strings.length == 1)
      return strings[0];

    for (int i = 0; i < strings[0].length(); ++i)
      for (int j = 1; j < strings.length; ++j)
        if (i == strings[j].length() || strings[0].charAt(i) != strings[j].charAt(i))
          return strings[0].substring(0, i);

    return strings[0];
  }

  /**
   * Returns the prefix string that is shared amongst all members for the
   * specified {@code Collection}.
   *
   * @param strings The {@code Collection} of strings in which to find a common
   *          prefix.
   * @return The prefix string that is shared amongst all members for the
   *         specified {@code Collection}.
   */
  public static String getCommonPrefix(final Collection<String> strings) {
    if (strings == null || strings.size() == 0)
      return null;

    Iterator<String> iterator = strings.iterator();
    if (strings.size() == 1)
      return iterator.next();

    final String string0 = iterator.next();
    for (int i = 0; i < string0.length(); ++i) {
      if (i > 0) {
        iterator = strings.iterator();
        iterator.next();
      }

      while (iterator.hasNext()) {
        final String next = iterator.next();
        if (i == next.length() || string0.charAt(i) != next.charAt(i))
          return string0.substring(0, i);
      }
    }

    return string0;
  }

  /**
   * Returns a representation of the specified string that is able to be
   * contained in a {@code String} literal in Java.
   *
   * @param string The string to transform.
   * @return A representation of the specified string that is able to be
   *         contained in a {@code String} literal in Java.
   */
  public static String escapeForJava(final String string) {
    return string == null ? null : string.replace("\\", "\\\\").replace("\"", "\\\"");
  }

  public static String printColumns(final String ... columns) {
    // Split input strings into columns and rows
    final String[][] strings = new String[columns.length][];
    int maxLines = 0;
    for (int i = 0; i < columns.length; ++i) {
      strings[i] = columns[i] == null ? null : columns[i].split("\n");
      if (strings[i] != null && strings[i].length > maxLines)
        maxLines = strings[i].length;
    }

    // Store an array of column widths
    final int[] widths = new int[columns.length];
    // calculate column widths
    for (int i = 0, maxWidth = 0; i < columns.length; ++i) {
      if (strings[i] != null) {
        for (int j = 0; j < strings[i].length; ++j)
          if (strings[i][j].length() > maxWidth)
            maxWidth = strings[i][j].length();
      }
      else if (maxWidth < 4) {
        maxWidth = 4;
      }

      widths[i] = maxWidth + 1;
    }

    // Print the lines
    final StringBuilder builder = new StringBuilder();
    for (int j = 0; j < maxLines; ++j) {
      if (j > 0)
        builder.append('\n');

      for (int i = 0; i < strings.length; ++i) {
        final String line = strings[i] == null ? "null" : j < strings[i].length ? strings[i][j] : "";
        builder.append(String.format("%-" + widths[i] + "s", line));
      }
    }

    return builder.length() == 0 ? "null" : builder.toString();
  }

  /**
   * Returns a string consisting of a specific number of concatenated
   * repetitions of an input string. For example,
   * {@code Strings.repeat("ha", 3)} returns the string {@code "hahaha"}.
   *
   * @param string Any non-null string.
   * @param count A nonnegative number of times to repeat the string.
   * @return A string containing {@code string} repeated {@code count} times; an
   *         empty string if {@code count == 0}; the {@code string} if
   *         {@code count == 1}
   * @throws NullPointerException If {@code string == null}
   * @throws IllegalArgumentException If {@code count &lt; 0}
   * @throws ArrayIndexOutOfBoundsException If
   *           {@code string.length() * count &gt; Integer.MAX_VALUE}
   */
  public static String repeat(final String string, final int count) {
    if (count < 0)
      throw new IllegalArgumentException("count < 0");

    if (count == 0 || string.length() == 0)
      return "";

    if (count == 1)
      return string;

    final int length = string.length();
    final long longSize = (long)length * (long)count;
    final int size = (int)longSize;
    if (size != longSize)
      throw new ArrayIndexOutOfBoundsException("Required array size too large: " + longSize);

    final char[] chars = new char[size];
    string.getChars(0, length, chars, 0);
    int n = length;
    for (; n < size - n; n <<= 1)
      System.arraycopy(chars, 0, chars, n, n);

    System.arraycopy(chars, 0, chars, n, size - n);
    return new String(chars);
  }

  /**
   * Encodes the specified {@code String} into a sequence of bytes using the
   * named charset, storing the result into a new byte array.
   * <p>
   * This method differentiates itself from {@link String#getBytes(String)} by
   * throwing the unchecked {@link UnsupportedOperationException} instead of the
   * checked {@link UnsupportedEncodingException} if the named charset is not
   * supported.
   *
   * @param string The string to encode.
   * @param charsetName The name of a supported
   *          {@linkplain java.nio.charset.Charset charset}.
   * @return The resultant byte array.
   * @throws UnsupportedOperationException If the named charset is not
   *           supported.
   * @throws NullPointerException If {@code string} or {@code charsetName} are
   *           null.
   * @see String#getBytes(String)
   */
  public static byte[] getBytes(final String string, final String charsetName) {
    try {
      return string.getBytes(charsetName);
    }
    catch (final UnsupportedEncodingException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  /**
   * Returns the specified string with any leading and trailing characters
   * matching the provided {@code char} removed.
   *
   * @param string The string to be trimmed.
   * @param ch The {@code char} to remove from the front and back of the
   *          specified string.
   * @return The specified string with any leading and trailing characters
   *         matching the provided {@code char} removed.
   */
  public static String trim(final String string, final char ch) {
    if (string == null)
      return null;

    int i = 0;
    while (i < string.length() && string.charAt(i++) == ch);
    if (i == string.length())
      return "";

    int j = string.length() - 1;
    while (j > i + 1 && string.charAt(j++) == ch);
    return i == 0 && j == string.length() - 1 ? string : string.substring(i, j + 1);
  }

  public static int indexOfUnQuoted(final String string, final char ch, final int fromIndex) {
    boolean esacped = false;
    boolean inDoubleQuote = false;
    for (int i = fromIndex; i < string.length(); ++i) {
      final char c = string.charAt(i);
      if (c == '\\')
        esacped = true;
      else if (esacped)
        esacped = false;
      else if (c == ch && !inDoubleQuote)
        return i;
      else if (c == '"')
        inDoubleQuote = !inDoubleQuote;
    }

    return -1;
  }

  public static int indexOfUnQuoted(final String string, final char ch) {
    return indexOfUnQuoted(string, ch, 0);
  }

  public static int lastIndexOfUnQuoted(final String string, final char ch, final int fromIndex) {
    boolean esacped = false;
    boolean inDoubleQuote = false;
    char n = '\0';
    for (int i = fromIndex - 1; i >= 0; --i) {
      final char c = string.charAt(i);
      if (c == '\\')
        esacped = true;
      else if (esacped)
        esacped = false;
      else if (n == ch && !inDoubleQuote)
        return i + 1;
      else if (n == '"')
        inDoubleQuote = !inDoubleQuote;

      n = c;
    }

    return n == ch ? 0 : -1;
  }

  public static int lastIndexOfUnQuoted(final String string, final char ch) {
    return lastIndexOfUnQuoted(string, ch, string.length());
  }

  public static String abbreviate(final String string, final int length) {
    if (length < 4)
      throw new IllegalArgumentException("length < 4: " + length);

    return string.length() > length ? string.substring(0, length - 3) + "..." : string;
  }

  private static void appendElVar(final Map<String,String> variables, final StringBuilder builder, final StringBuilder var) {
    final String name = var.toString();
    final String value = variables.get(name);
    if (value != null)
      builder.append(value);
    else
      builder.append('$').append('{').append(name).append('}');

    var.setLength(0);
  }

  private static void appendElNoMatch(final StringBuilder builder, final StringBuilder var, final char close) {
    builder.append('$').append('{');
    if (var.length() > 0) {
      builder.append(var.toString());
      var.setLength(0);
    }

    if (close != '\0')
      builder.append(close);
  }

  /**
   * Dereferences all Expression Language-encoded names, such as
   * <code>${foo}</code> or <code>${bar}</code>, in the specified string with
   * values in the specified map.
   * <p>
   * Names encoded in Expression Language follow the same rules as <a href=
   * "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.8">Java
   * Identifiers</a>.
   *
   * @param s The string in which EL-encoded names are to be dereferenced.
   * @param variables The map of name to value pairs.
   * @return The specified string with EL-encoded names replaced with their
   *         mapped values. If a name is missing from the specified map, or if a
   *         name does not conform to the rules of <a href=
   *         "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.8">Java
   *         Identifiers</a>, or if the Expression Language encoding is
   *         malformed, it will remain in the string as-is.
   * @throws NullPointerException If {@code s} is null, or if {@code s} contains
   *           an EL-encoded name and {@code variables} is null.
   */
  public static String derefEL(final String s, final Map<String,String> variables) {
    if (s.length() < 4)
      return s;

    final StringBuilder builder = new StringBuilder();
    final StringBuilder var = new StringBuilder();
    boolean escape = false;
    final int len = s.length();
    for (int i = 0; i < len; ++i) {
      char ch = s.charAt(i);
      if (ch == '\\') {
        if (var.length() > 0) {
          builder.append('$').append('{').append(var.toString());
          var.setLength(0);
        }

        if (!(escape = !escape))
          builder.append(ch);
      }
      else if (!escape) {
        if (ch == '$') {
          if (var.length() > 0) {
            appendElVar(variables, builder, var);
          }

          if (++i == len) {
            builder.append('$');
          }
          else {
            ch = s.charAt(i);
            if (ch != '{') {
              var.setLength(0);
              builder.append('$');
              if (ch != '\\')
                builder.append(ch);
            }
            else if (++i == len) {
              appendElNoMatch(builder, var, '\0');
            }
            else {
              ch = s.charAt(i);
              if ('a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || ch == '_')
                var.append(ch);
              else
                appendElNoMatch(builder, var, ch);
            }
          }
        }
        else if (var.length() > 0) {
          if ('a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || '0' <= ch && ch <= '9' || ch == '_') {
            var.append(ch);
          }
          else if (ch != '}') {
            appendElNoMatch(builder, var, ch);
          }
          else {
            appendElVar(variables, builder, var);
            if (ch != '}')
              builder.append(ch);
          }
        }
        else {
          builder.append(ch);
        }
      }
      else {
        if (var.length() > 0)
          appendElVar(variables, builder, var);

        builder.append(ch);
        escape = false;
      }
    }

    if (var.length() > 0)
      appendElNoMatch(builder, var, '\0');

    return builder.toString();
  }

  private static void appendEvVar(final Map<String,String> variables, final StringBuilder builder, final StringBuilder var) {
    final String variable = variables.get(var.toString());
    if (variable != null)
      builder.append(variable);

    var.setLength(0);
  }

  /**
   * Dereferences all POSIX-compliant Environment Variable names, such as
   * <code>$FOO</code> or <code>${BAR}</code>, in the specified string with
   * values in the specified map.
   * <p>
   * Names encoded in POSIX format follow the rules defined in the POSIX
   * standard on shells <a href=
   * "http://pubs.opengroup.org/onlinepubs/9699919799/utilities/V3_chap02.html#tag_18_10_02">IEEE
   * Std 1003.1-2017</a>.
   *
   * @param s The string in which POSIX-compliant names are to be dereferenced.
   * @param variables The map of name to value pairs.
   * @return The specified string with POSIX-compliant names replaced with their
   *         mapped values. If a name is missing from the specified map, it will
   *         remain in the string as-is.
   * @throws ParseException If the encoding of the environment variable name is
   *           malformed.
   * @throws NullPointerException If {@code s} is null, or if {@code s} contains
   *           an POSIX-compliant name and {@code variables} is null.
   */
  public static String derefEV(final String s, final Map<String,String> variables) throws ParseException {
    if (s.length() < 2)
      return s;

    final StringBuilder builder = new StringBuilder();
    final StringBuilder var = new StringBuilder();
    boolean escape = false;
    boolean bracket = false;
    final int len = s.length();
    for (int i = 0; i < len; i++) {
      char ch = s.charAt(i);
      if (ch == '\\') {
        if (var.length() > 0)
          appendEvVar(variables, builder, var);

        if (!(escape = !escape))
          builder.append(ch);
      }
      else if (!escape) {
        if (ch == '$') {
          if (var.length() > 0)
            appendEvVar(variables, builder, var);

          if (++i == len) {
            builder.append('$');
          }
          else {
            ch = s.charAt(i);
            if (ch == '$')
              throw new ParseException("$$: not supported", i);

            if (ch == '{') {
              bracket = true;
              if (++i == len)
                throw new ParseException("${: bad substitution", i);

              ch = s.charAt(i);
            }

            if ('a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || ch == '_') {
              var.append(ch);
            }
            else if (!bracket) {
              builder.append('$');
              if (ch != '\\')
                builder.append(ch);
            }
            else {
              throw new ParseException("${" + ch + ": bad substitution", i);
            }
          }
        }
        else if (var.length() > 0) {
          if ('a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || '0' <= ch && ch <= '9' || ch == '_') {
            var.append(ch);
          }
          else if (bracket && ch != '}') {
            throw new ParseException("${" + var + ch + ": bad substitution", i);
          }
          else {
            appendEvVar(variables, builder, var);
            if (!bracket || ch != '}')
              builder.append(ch);
          }
        }
        else {
          builder.append(ch);
        }
      }
      else {
        if (var.length() > 0)
          appendEvVar(variables, builder, var);

        builder.append(ch);
        escape = false;
      }
    }

    if (var.length() > 0) {
      if (bracket)
        throw new ParseException("${" + var + ": bad substitution", len);

      appendEvVar(variables, builder, var);
    }

    return builder.toString();
  }

  private Strings() {
  }
}