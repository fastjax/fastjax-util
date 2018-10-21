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

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class StringsTest {
  private static final String UPPER_CASE = "HELLO WORLD";
  private static final String LOWER_CASE = "hello world";

  @Test
  public void testGetRandomAlphaString() {
    try {
      Strings.getRandomAlphaString(-1);
      fail("Expecting an IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    for (int length = 0; length < 100; length++) {
      final String random = Strings.getRandomAlphaString(length);
      assertEquals(random.length(), length);
      assertTrue(random, random.matches("^[a-zA-Z]*$"));
    }
  }

  @Test
  public void testGetRandomAlphaNumericString() {
    try {
      Strings.getRandomAlphaNumericString(-1);
      fail("Expecting an IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    for (int length = 0; length < 100; length++) {
      final String random = Strings.getRandomAlphaNumericString(length);
      assertEquals(random.length(), length);
      assertTrue(random, random.matches("^[0-9a-zA-Z]*$"));
    }
  }

  private static void assertInterpolate(final String expected, final String test, final Map<String,String> properties, final String open, final String close) throws ParseException {
    final String actual = Strings.interpolate(test, properties, open, close);
    assertEquals(expected, actual);
  }

  @Test
  public void testInterpolate() throws ParseException {
    final String open = "{{";
    final String close = "}}";
    final Map<String,String> properties = new HashMap<>();
    properties.put("prop1", "prop1");
    properties.put("prop2", "prop2");
    properties.put("prop3", "prop3");
    properties.put("prop4", "{{prop2}}");
    properties.put("prop5", "{{prop4}} plus {{prop3}}");
    properties.put("prop6", "{{prop5}} plus {{prop6}}");

    assertInterpolate("Bla bla prop1 with prop2 and prop3", "Bla bla {{prop1}} with {{prop2}} and {{prop3}}", properties, open, close);
    assertInterpolate("Bla bla prop2 with prop3 and prop2", "Bla bla {{prop2}} with {{prop3}} and {{prop4}}", properties, open, close);
    assertInterpolate("Bla bla prop3 with prop2 and prop2 plus prop3", "Bla bla {{prop3}} with {{prop4}} and {{prop5}}", properties, open, close);

    try {
      Strings.interpolate("Bla bla {{prop4}} with {{prop5}} and {{prop6}}", properties, "{{", "}}");
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
      if (!"Loop detected".equals(e.getMessage()))
        throw e;
    }

    try {
      Strings.interpolate(properties, "{{", "}}");
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
      if (!"Loop detected".equals(e.getMessage()))
        throw e;
    }

    properties.remove("prop6");
    Strings.interpolate(properties, "{{", "}}");
    assertEquals("prop2 plus prop3", properties.get("prop5"));
  }

  @Test
  public void testReplace() throws Exception {
    assertEquals("xde", Strings.replace(new StringBuilder("abcde"), "abc", "x").toString());
    assertEquals("axde", Strings.replace(new StringBuilder("abcde"), "bc", "x").toString());
    assertEquals("abxe", Strings.replace(new StringBuilder("abcde"), "cd", "x").toString());
    assertEquals("abcx", Strings.replace(new StringBuilder("abcde"), "de", "x").toString());
    assertEquals("xxxxx", Strings.replace(new StringBuilder("aaaaa"), "a", "x").toString());
    assertEquals("xxx", Strings.replace(new StringBuilder("ababab"), "ab", "x").toString());
    assertEquals("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", Strings.replace(new StringBuilder("ababab"), "ab", "xxxxxxxxxx").toString());
  }

  @Test
  public void testChangeCase() throws Exception {
    try {
      Strings.toLowerCase(null, 0, 1);
      fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    try {
      Strings.toLowerCase(UPPER_CASE, 10, 4);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Strings.toLowerCase(UPPER_CASE, 12, 13);
      fail("Expected StringIndexOutOfBoundsException");
    }
    catch (final StringIndexOutOfBoundsException e) {
    }

    try {
      Strings.toLowerCase(UPPER_CASE, -1, 1);
      fail("Expected StringIndexOutOfBoundsException");
    }
    catch (final StringIndexOutOfBoundsException e) {
    }

    try {
      Strings.toLowerCase(UPPER_CASE, -2, -1);
      fail("Expected StringIndexOutOfBoundsException");
    }
    catch (final StringIndexOutOfBoundsException e) {
    }

    try {
      Strings.toLowerCase(UPPER_CASE, 1, 12);
      fail("Expected StringIndexOutOfBoundsException");
    }
    catch (final Exception e) {
    }

    assertEquals("", Strings.toLowerCase("", 0, 0));
    assertEquals(UPPER_CASE, Strings.toLowerCase(UPPER_CASE, 0, 0));
    assertEquals("hELLO WORLD", Strings.toLowerCase(UPPER_CASE, 0, 1));
    assertEquals("HeLLO WORLD", Strings.toLowerCase(UPPER_CASE, 1, 2));
    assertEquals("HelLO WORLD", Strings.toLowerCase(UPPER_CASE, 1, 3));
    assertEquals("HELLO WORLd", Strings.toLowerCase(UPPER_CASE, 10, 11));
    assertEquals("HELLO WORld", Strings.toLowerCase(UPPER_CASE, 9, 11));
    assertEquals("HELLO WOrld", Strings.toLowerCase(UPPER_CASE, 8));
    assertEquals("HELLO world", Strings.toLowerCase(UPPER_CASE, 6));

    assertEquals("", Strings.toUpperCase("", 0, 0));
    assertEquals(LOWER_CASE, Strings.toLowerCase(LOWER_CASE, 0, 0));
    assertEquals("Hello world", Strings.toUpperCase(LOWER_CASE, 0, 1));
    assertEquals("hEllo world", Strings.toUpperCase(LOWER_CASE, 1, 2));
    assertEquals("hELlo world", Strings.toUpperCase(LOWER_CASE, 1, 3));
    assertEquals("hello worlD", Strings.toUpperCase(LOWER_CASE, 10, 11));
    assertEquals("hello worLD", Strings.toUpperCase(LOWER_CASE, 9, 11));
    assertEquals("hello woRLD", Strings.toUpperCase(LOWER_CASE, 8));
    assertEquals("hello WORLD", Strings.toUpperCase(LOWER_CASE, 6));
  }

  @Test
  public void testGetAlpha() {
    assertEquals("a", Strings.getAlpha(0));
    assertEquals("aa", Strings.getAlpha(26));
    assertEquals("aaa", Strings.getAlpha(26 * 26 + 26));
    assertEquals("aaaa", Strings.getAlpha(26 * 26 * 26 + 26 * 26 + 26));

    assertEquals("f", Strings.getAlpha(5));
    assertEquals("z", Strings.getAlpha(25));

    assertEquals("ac", Strings.getAlpha(28));
    assertEquals("za", Strings.getAlpha(676));
  }

  @Test
  public void testGetCommonPrefix() {
    assertNull(Strings.getCommonPrefix((String[])null));
    assertNull(Strings.getCommonPrefix((String)null));
    try {
      Strings.getCommonPrefix(null, null);
      fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    assertEquals("a", Strings.getCommonPrefix("a"));
    assertEquals("a", Strings.getCommonPrefix(Arrays.asList("a")));

    assertEquals("", Strings.getCommonPrefix("", "b"));
    assertEquals("", Strings.getCommonPrefix(Arrays.asList("", "b")));

    assertEquals("", Strings.getCommonPrefix("a", ""));
    assertEquals("", Strings.getCommonPrefix(Arrays.asList("a", "")));

    assertEquals("", Strings.getCommonPrefix("a", "b"));
    assertEquals("", Strings.getCommonPrefix(Arrays.asList("a", "b")));

    assertEquals("", Strings.getCommonPrefix("aa", "b"));
    assertEquals("", Strings.getCommonPrefix(Arrays.asList("aa", "b")));

    assertEquals("", Strings.getCommonPrefix("a", "bb"));
    assertEquals("", Strings.getCommonPrefix(Arrays.asList("a", "bb")));

    assertEquals("a", Strings.getCommonPrefix("aa", "ab"));
    assertEquals("a", Strings.getCommonPrefix(Arrays.asList("aa", "ab")));

    assertEquals("a", Strings.getCommonPrefix("aaa", "ab"));
    assertEquals("a", Strings.getCommonPrefix(Arrays.asList("aaa", "ab")));

    assertEquals("a", Strings.getCommonPrefix("aa", "abb"));
    assertEquals("a", Strings.getCommonPrefix(Arrays.asList("aa", "abb")));

    assertEquals("aa", Strings.getCommonPrefix("aaa", "aab"));
    assertEquals("aa", Strings.getCommonPrefix(Arrays.asList("aaa", "aab")));

    assertEquals("aa", Strings.getCommonPrefix("aaaa", "aab"));
    assertEquals("aa", Strings.getCommonPrefix(Arrays.asList("aaaa", "aab")));

    assertEquals("aa", Strings.getCommonPrefix("aaa", "aabb"));
    assertEquals("aa", Strings.getCommonPrefix(Arrays.asList("aaa", "aabb")));
  }

  @Test
  public void testRepeat() {
    try {
      Strings.repeat(null, 10);
      fail("Expected a NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    try {
      Strings.repeat("", -1);
      fail("Expected a IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals("a", Strings.repeat("a", 1));
    assertEquals("aa", Strings.repeat("a", 2));
    assertEquals("abab", Strings.repeat("ab", 2));
    assertEquals("ab ab ab ", Strings.repeat("ab ", 3));

    try {
      Strings.repeat("abcdefghijklmnopqrstuvwxyz", 353892843);
      fail("Expected an ArrayIndexOutOfBoundsException");
    }
    catch (final ArrayIndexOutOfBoundsException e) {
    }
  }

  @Test
  public void testTrim() {
    assertNull(Strings.trim(null, '\0'));
    assertEquals("string", Strings.trim("xstring", 'x'));
    assertEquals("string", Strings.trim("stringx", 'x'));
    assertEquals("string", Strings.trim("xstringx", 'x'));
    assertEquals("string", Strings.trim("xxstringxx", 'x'));
    assertEquals("string", Strings.trim("xxxstringxxx", 'x'));
    assertEquals("string", Strings.trim("\0string\0", '\0'));
  }

  @Test
  public void testIndexOfUnQuoted() {
    try {
      Strings.indexOfUnQuoted(null, '\0');
      fail("Expected a NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    final String testString = "random 'x' \"quoted \\'x\\' \\\"t\\\" \\\\\"s\\\\\"\" te'\\''xts";
    assertEquals(-1, Strings.indexOfUnQuoted(testString, '1'));
    assertEquals(0, Strings.indexOfUnQuoted(testString, 'r'));
    assertEquals(4, Strings.indexOfUnQuoted(testString, 'o'));
    assertEquals(-1, Strings.indexOfUnQuoted(testString, 'o', 5));
    assertEquals(-1, Strings.indexOfUnQuoted(testString, 'q'));
    assertEquals(41, Strings.indexOfUnQuoted(testString, 'e'));
    assertEquals(46, Strings.indexOfUnQuoted(testString, 'x'));
    assertEquals(48, Strings.indexOfUnQuoted(testString, 's'));

    final String doubleQuote = "\"The \\\"meaning\\\" of life\"";
    assertEquals(doubleQuote.length() - 1, Strings.indexOfUnQuoted(doubleQuote, '"', 1));

    final String singleQuote = "'The \\'meaning\\' of life'";
    assertEquals(singleQuote.length() - 1, Strings.indexOfUnQuoted(singleQuote, '\'', 1));
  }

  @Test
  public void testLastIndexOfUnQuoted() {
    try {
      Strings.indexOfUnQuoted(null, '\0');
      fail("Expected a NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    final String testString = "ran'\\''dom 'n' \"quoted \\'n\\' \\\"d\\\" \\\\\"s\\\\\"\" texts";
    assertEquals(-1, Strings.lastIndexOfUnQuoted(testString, '1'));
    assertEquals(0, Strings.lastIndexOfUnQuoted(testString, 'r'));
    assertEquals(-1, Strings.lastIndexOfUnQuoted(testString, 'q'));
    assertEquals(2, Strings.lastIndexOfUnQuoted(testString, 'n'));
    assertEquals(7, Strings.lastIndexOfUnQuoted(testString, 'd'));
    assertEquals(8, Strings.lastIndexOfUnQuoted(testString, 'o'));
    assertEquals(8, Strings.lastIndexOfUnQuoted(testString, 'o', 9));

    final String doubleQuote = "\"The \\\"meaning\\\" of life\"";
    assertEquals(0, Strings.lastIndexOfUnQuoted(doubleQuote, '"', doubleQuote.length() - 1));

    final String singleQuote = "'The \\'meaning\\' of life'";
    assertEquals(0, Strings.lastIndexOfUnQuoted(singleQuote, '\'', singleQuote.length() - 1));
  }

  @Test
  public void testToTruncatedString() {
    try {
      Strings.toTruncatedString("", 3);
      fail("Expected a IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals("null", Strings.toTruncatedString(null, 4));
    assertEquals("", Strings.toTruncatedString("", 4));
    assertEquals("a", Strings.toTruncatedString("a", 4));
    assertEquals("aa", Strings.toTruncatedString("aa", 4));
    assertEquals("aaa", Strings.toTruncatedString("aaa", 4));
    assertEquals("aaaa", Strings.toTruncatedString("aaaa", 4));
    assertEquals("aaaaa", Strings.toTruncatedString("aaaaa", 5));
    assertEquals("aa...", Strings.toTruncatedString("aaaaaa", 5));
    assertEquals("aaa...", Strings.toTruncatedString("aaaaaaa", 6));
  }
}