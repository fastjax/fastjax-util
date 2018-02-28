/* Copyright (c) 2017 lib4j
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

import java.util.Arrays;
import java.util.Map;

import org.lib4j.lang.Strings;
import org.lib4j.util.function.QuadFunction;

public final class JavaIdentifiers {
  private static final String[] reservedWords = {"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "false", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while"};

  // NOTE: This array is sorted
  private static final char[] discardTokens = {'!', '"', '#', '%', '&', '\'', '(', ')', '*', ',', '-', '.', '.', '/', ':', ';', '<', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~'};

  private static boolean isJavaReservedWord(final String word) {
    return Arrays.binarySearch(reservedWords, word) >= 0;
  }

  private static String transformNotReserved(final String string, final char prefix, final char suffix, final char substitute, final Map<Character,String> substitutes, final QuadFunction<String,Character,Character,Map<Character,String>,StringBuilder> transformer) {
    if (string == null || string.length() == 0)
      return string;

    final StringBuilder builder = transformer.apply(string, prefix, substitute, substitutes);
    final String word = builder.toString();
    return !isJavaReservedWord(word) ? word : suffix == '\0' ? builder.insert(0, prefix).toString() : builder.append(suffix).toString();
  }

  /**
   * Transforms a string into a valid Java Identifier. Strings that start with
   * an illegal character are prepended with <code>prefix</code>. Strings that
   * are Java Reserved Words are prepended with <code>prefix</code>. All other
   * illegal characters are substituted with the string value mapped to the key
   * of the character in <code>substitutes</code>. If the mapping is missing,
   * the character is substituted with the <code>substitute</code> char.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @param substitutes The mapping of illegal characters to their substitutions.
   *                    This mapping overrides the default substitution.
   * @return The string transformed to a valid Java Identifier.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toIdentifier(final String string, final char prefix, final char substitute, final Map<Character,String> substitutes) {
    return transformNotReserved(string, prefix, '\0', substitute, substitutes, JavaIdentifiers::toIdentifier0);
  }

  /**
   * Transforms a string into a valid Java Identifier. Strings that start with
   * an illegal character are prepended with <code>_</code>. Strings that
   * are Java Reserved Words are prepended with <code>_</code>. All other
   * illegal characters are substituted with the string value mapped to the key
   * of the character in <code>substitutes</code>. If the mapping is missing,
   * the illegal character is omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @param substitutes The mapping of illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toIdentifier(final String string, final Map<Character,String> substitutes) {
    return transformNotReserved(string, '_', '\0', '\0', substitutes, JavaIdentifiers::toIdentifier0);
  }

  /**
   * Transforms a string into a valid Java Identifier. Strings that start with
   * an illegal character are prepended with <code>prefix</code>. Strings that
   * are Java Reserved Words are prepended with <code>prefix</code>. All other
   * illegal characters are substituted with the string value mapped to the key
   * of the character in <code>substitutes</code>. If the mapping is missing,
   * the illegal character is omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @param substitutes The mapping of illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toIdentifier(final String string, final char prefix, final Map<Character,String> substitutes) {
    return transformNotReserved(string, prefix, '\0', '\0', substitutes, JavaIdentifiers::toIdentifier0);
  }

  /**
   * Transforms a string into a valid Java Identifier. Strings that start with
   * an illegal character are prepended with <code>prefix</code>. Strings that
   * are Java Reserved Words are prepended with <code>prefix</code>. All other
   * illegal characters are substituted with the <code>substitute</code> char.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @return The string transformed to a valid Java Identifier.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toIdentifier(final String string, final char prefix, final char substitute) {
    return transformNotReserved(string, prefix, '\0', substitute, null, JavaIdentifiers::toIdentifier0);
  }

  /**
   * Transforms a string into a valid Java Identifier. Strings that start with
   * an illegal character are prepended with <code>prefix</code>. Strings that
   * are Java Reserved Words are prepended with <code>prefix</code>. All other
   * illegal characters are omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @return The string transformed to a valid Java Identifier.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toIdentifier(final String string, final char prefix) {
    return transformNotReserved(string, prefix, '\0', '\0', null, JavaIdentifiers::toIdentifier0);
  }

  /**
   * Transforms a string into a valid Java Identifier. Strings that start with
   * an illegal character are prepended with <code>_</code>. Strings that
   * are Java Reserved Words are prepended with <code>_</code>. All other
   * illegal characters are omitted.
   *
   * @param string The input string.
   * @return The string transformed to a valid Java Identifier.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toIdentifier(final String string) {
    return transformNotReserved(string, '_', '\0', '\0', null, JavaIdentifiers::toIdentifier0);
  }

  private static StringBuilder toIdentifier0(final String string, final char prefix, final char substitute, final Map<Character,String> substitutes) {
    final StringBuilder builder = new StringBuilder(string.length());
    if (string.length() == 0)
      return builder;

    final char[] chars = string.toCharArray();
    char ch = chars[0];
    if (!Character.isJavaIdentifierStart(ch) && prefix != '\0')
      builder.append(prefix);

    builder.append(ch);
    for (int i = 1; i < chars.length; i++) {
      ch = chars[i];
      if (Character.isJavaIdentifierPart(ch)) {
        builder.append(ch);
        continue;
      }

      if (substitutes != null) {
        final String replacement = substitutes.get(ch);
        if (replacement != null) {
          builder.append(replacement);
          continue;
        }
      }

      if (substitute != '\0') {
        builder.append(substitute);
      }
    }

    return builder;
  }

  /**
   * Transforms a string into a valid Java Identifier that meets suggested
   * package name guidelines. Strings that are Java Reserved Words are
   * prepended with <code>prefix</code>. Strings that start with an illegal
   * character are prepended with <code>_</code>. All other illegal characters
   * are substituted <code>_</code>.
   *
   * If the domain name contains a hyphen, or any other special character not
   * allowed in an identifier, convert it into an underscore.
   *
   * If any of the resulting package name components are keywords, append an
   * underscore to them.
   *
   * If any of the resulting package name components start with a digit, or any
   * other character that is not allowed as an initial character of an
   * identifier, have an underscore prefixed to the component.
   *
   * @param string The input string.
   * @return The string transformed to a valid Java Identifier that meets
   *         suggested package name guidelines.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   * @see <a href="https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html">Package Names</a>
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-6.html#d5e8089">Unique Pacakge Names</a>
   */
  public static String toPackageCase(final String string) {
    return transformNotReserved(string, '_', '_', '_', null, JavaIdentifiers::toPacakgeCase0);
  }

  private static StringBuilder toPacakgeCase0(final String string, final char prefix, final char substitute, final Map<Character,String> substitutes) {
    return Strings.toLowerCase(toIdentifier0(string, prefix, substitute, substitutes));
  }

  /**
   * Transforms a string into a valid Java Identifier in camelCase. Strings
   * that start with an illegal character are prepended with <code>prefix</code>.
   * Strings that are Java Reserved Words are prepended with <code>prefix</code>.
   * All other illegal characters are substituted with the string value mapped
   * to the key of the character in <code>substitutes</code>. If the mapping is
   * missing, the character is substituted with the <code>substitute</code> char.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @param substitutes The mapping of illegal characters to their substitutions.
   *                    This mapping overrides the default substitution.
   * @return The string transformed to a valid Java Identifier in camelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toCamelCase(final String string, final char prefix, final char substitute, final Map<Character,String> substitutes) {
    return transformNotReserved(string, prefix, '\0', substitute, substitutes, JavaIdentifiers::toCamelCase0);
  }

  /**
   * Transforms a string into a valid Java Identifier in camelCase. Strings
   * that start with an illegal character are prepended with <code>prefix</code>.
   * Strings that are Java Reserved Words are prepended with <code>prefix</code>.
   * All other illegal characters are substituted with the string value mapped
   * to the key of the character in <code>substitutes</code>. If the mapping is
   * missing, the illegal character is omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @param substitutes The mapping of illegal characters to their substitutions.
   *                    This mapping overrides the default substitution.
   * @return The string transformed to a valid Java Identifier in camelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toCamelCase(final String string, final char prefix, final Map<Character,String> substitutes) {
    return transformNotReserved(string, prefix, '\0', '\0', substitutes, JavaIdentifiers::toCamelCase0);
  }

  /**
   * Transforms a string into a valid Java Identifier in camelCase. Strings
   * that start with an illegal character are prepended with <code>prefix</code>.
   * Strings that are Java Reserved Words are prepended with <code>prefix</code>.
   * All other illegal characters are substituted with the
   * <code>substitute</code> char.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @return The string transformed to a valid Java Identifier in camelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toCamelCase(final String string, final char prefix, final char substitute) {
    return transformNotReserved(string, prefix, '\0', substitute, null, JavaIdentifiers::toCamelCase0);
  }

  /**
   * Transforms a string into a valid Java Identifier in camelCase. Strings
   * that start with an illegal character are prepended with <code>prefix</code>.
   * Strings that are Java Reserved Words are prepended with <code>prefix</code>.
   * All other illegal characters are omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @return The string transformed to a valid Java Identifier in camelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toCamelCase(final String string, final char prefix) {
    return transformNotReserved(string, prefix, '\0', '\0', null, JavaIdentifiers::toCamelCase0);
  }

  /**
   * Transforms a string into a valid Java Identifier in camelCase. Strings
   * that start with an illegal character are prepended with <code>x</code>.
   * Strings that are Java Reserved Words are prepended with <code>x</code>.
   * All other illegal characters are omitted.
   *
   * @param string The input string.
   * @return The string transformed to a valid Java Identifier in camelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toCamelCase(final String string) {
    return transformNotReserved(string, 'x', '\0', '\0', null, JavaIdentifiers::toCamelCase0);
  }

  /**
   * Transforms a string into a valid Java Identifier in camelCase. Strings
   * that start with an illegal character are prepended with <code>x</code>.
   * Strings that are Java Reserved Words are prepended with <code>x</code>.
   * All other illegal characters are substituted with the string value mapped
   * to the key of the character in <code>substitutes</code>. If the mapping is
   * missing, the illegal character is omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @param substitutes The mapping of illegal characters to their substitutions.
   *                    This mapping overrides the default substitution.
   * @return The string transformed to a valid Java Identifier in camelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toCamelCase(final String string, final Map<Character,String> substitutes) {
    return transformNotReserved(string, 'x', '\0', '\0', substitutes, JavaIdentifiers::toCamelCase0);
  }

  private static StringBuilder toCamelCase0(final String string, final char prefix, final char substitute, final Map<Character,String> substitutes) {
    final StringBuilder builder = new StringBuilder(string.length());
    if (string.length() == 0)
      return builder;

    final char[] chars = string.toCharArray();
    boolean capNext = false;
    for (int i = 0; i < chars.length; i++) {
      if (i == 0 && !Character.isJavaIdentifierStart(chars[i]))
        builder.append(prefix);

      final char ch = chars[i];
      final int index = java.util.Arrays.binarySearch(discardTokens, ch);
      if (index >= 0) {
        capNext = i != 0;
        if (substitutes != null) {
          final String replacement = substitutes.get(ch);
          if (replacement != null) {
            builder.append(replacement);
            continue;
          }
        }

        if (substitute != '\0') {
          builder.append(substitute);
        }
      }
      else if (capNext) {
        builder.append(Character.toUpperCase(ch));
        capNext = false;
      }
      else {
        builder.append(ch);
      }
    }

    return builder;
  }

  /**
   * Transforms a string into a valid Java Identifier in lower-camelCase.
   * Strings that start with an illegal character are prepended with
   * <code>prefix</code>. Strings that are Java Reserved Words are prepended with
   * <code>prefix</code>. All other illegal characters are substituted with the
   * string value mapped to the key of the character in <code>substitutes</code>.
   * If the mapping is missing, the character is substituted with the
   * <code>substitute</code> char.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @param substitutes The mapping of illegal characters to their substitutions.
   *                    This mapping overrides the default substitution.
   * @return The string transformed to a valid Java Identifier in lower-CamelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toInstanceCase(final String string, final char prefix, final char substitute, final Map<Character,String> substitutes) {
    return transformNotReserved(string, prefix, '\0', substitute, substitutes, JavaIdentifiers::toInstanceCase0);
  }

  /**
   * Transforms a string into a valid Java Identifier in lower-camelCase.
   * Strings that start with an illegal character are prepended with
   * <code>prefix</code>. Strings that are Java Reserved Words are prepended with
   * <code>prefix</code>. All other illegal characters are substituted with the
   * string value mapped to the key of the character in <code>substitutes</code>.
   * If the mapping is missing, the illegal character is omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @param substitutes The mapping of illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier in lower-CamelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toInstanceCase(final String string, final char prefix, final Map<Character,String> substitutes) {
    return transformNotReserved(string, prefix, '\0', '\0', substitutes, JavaIdentifiers::toInstanceCase0);
  }

  /**
   * Transforms a string into a valid Java Identifier in lower-camelCase.
   * Strings that start with an illegal character are prepended with
   * <code>prefix</code>. Strings that are Java Reserved Words are prepended with
   * <code>prefix</code>. All other illegal characters are substituted with the
   * <code>substitute</code> char.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @return The string transformed to a valid Java Identifier in lower-CamelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toInstanceCase(final String string, final char prefix, final char substitute) {
    return transformNotReserved(string, prefix, '\0', substitute, null, JavaIdentifiers::toInstanceCase0);
  }

  /**
   * Transforms a string into a valid Java Identifier in lower-camelCase.
   * Strings that start with an illegal character are prepended with
   * <code>prefix</code>. Strings that are Java Reserved Words are prepended with
   * <code>prefix</code>. All other illegal characters are omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @return The string transformed to a valid Java Identifier in lower-CamelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toInstanceCase(final String string, final char prefix) {
    return transformNotReserved(string, prefix, '\0', '\0', null, JavaIdentifiers::toInstanceCase0);
  }

  /**
   * Transforms a string into a valid Java Identifier in lower-camelCase.
   * Strings that start with an illegal character are prepended with
   * <code>_</code>. Strings that are Java Reserved Words are prepended with
   * <code>_</code>. All other illegal characters are omitted.
   *
   * @param string The input string.
   * @return The string transformed to a valid Java Identifier in lower-CamelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toInstanceCase(final String string) {
    return transformNotReserved(string, '_', '\0', '\0', null, JavaIdentifiers::toInstanceCase0);
  }

  /**
   * Transforms a string into a valid Java Identifier in lower-camelCase.
   * Strings that start with an illegal character are prepended with
   * <code>_</code>. Strings that are Java Reserved Words are prepended with
   * <code>_</code>. All other illegal characters are substituted with the
   * string value mapped to the key of the character in <code>substitutes</code>.
   * If the mapping is missing, the illegal character is omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @param substitutes The mapping of illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier in lower-CamelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toInstanceCase(final String string, final Map<Character,String> substitutes) {
    return transformNotReserved(string, '_', '\0', '\0', substitutes, JavaIdentifiers::toInstanceCase0);
  }

  /**
   * Transforms a string into a legal Java [c]amelCase identifier, guaranteeing
   * the beginning string of upper-case characters (until the last) are changed
   * to lower case. All illegal characters are removed, and used to determine
   * the location of change of case.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @param substitutes The mapping of illegal characters to their substitutions.
   *                    This mapping overrides the default substitution.
   * @return The string transformed to a legal Java [c]amelCase identifier.
   */
  private static StringBuilder toInstanceCase0(final String string, final char prefix, final char substitute, final Map<Character,String> substitutes) {
    final StringBuilder builder = toCamelCase0(string, prefix, substitute, substitutes);
    final int len = builder.length();
    if (len == 0)
      return builder;

    if (len == 1) {
      if (Character.isUpperCase(builder.charAt(0)))
        builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));

      return builder;
    }

    int i;
    for (i = 0; i < len; i++) {
      final char ch = builder.charAt(i);
      if (('0' <= ch && ch <= '9') || ('a' <= ch && ch <= 'z'))
        break;
    }

    if (i <= 1)
      return builder;

    if (i == len)
      return Strings.toLowerCase(builder);

    for (int j = 0; j < i - 1; j++)
      builder.setCharAt(j, Character.toLowerCase(builder.charAt(j)));

    return builder;
  }

  private static String transform(final String string, final char prefix, final char substitute, final Map<Character,String> substitutes, final QuadFunction<String,Character,Character,Map<Character,String>,StringBuilder> transformer) {
    if (string == null || string.length() == 0)
      return string;

    final StringBuilder builder = transformer.apply(string, prefix, substitute, substitutes);
    if (Character.isUpperCase(string.charAt(0)))
      return builder.toString();

    builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
    return builder.toString();
  }

  /**
   * Transforms a string into a valid Java Identifier in Title-CamelCase.
   * Strings that start with an illegal character are prepended with
   * <code>prefix</code>. All other illegal characters are substituted with the
   * string value mapped to the key of the character in <code>substitutes</code>.
   * If the mapping is missing, the character is substituted with the
   * <code>substitute</code> char.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @param substitutes The mapping of illegal characters to their substitutions.
   *                    This mapping overrides the default substitution.
   * @return The string transformed to a valid Java Identifier in Title-CamelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toClassCase(final String string, final char prefix, final char substitute, final Map<Character,String> substitutes) {
    return transform(string, prefix, substitute, substitutes, JavaIdentifiers::toCamelCase0);
  }

  /**
   * Transforms a string into a valid Java Identifier in Title-CamelCase.
   * Strings that start with an illegal character are prepended with
   * <code>prefix</code>. All other illegal characters are substituted with the
   * string value mapped to the key of the character in <code>substitutes</code>.
   * If the mapping is missing, the illegal character is omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @param substitutes The mapping of illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier in Title-CamelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toClassCase(final String string, final char prefix, final Map<Character,String> substitutes) {
    return transform(string, prefix, '\0', substitutes, JavaIdentifiers::toCamelCase0);
  }

  /**
   * Transforms a string into a valid Java Identifier in Title-CamelCase.
   * Strings that start with an illegal character are prepended with
   * <code>prefix</code>. All other illegal characters are substituted with
   * the <code>substitute</code> char.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @return The string transformed to a valid Java Identifier in Title-CamelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toClassCase(final String string, final char prefix, final char substitute) {
    return transform(string, prefix, substitute, null, JavaIdentifiers::toCamelCase0);
  }

  /**
   * Transforms a string into a valid Java Identifier in Title-CamelCase.
   * Strings that start with an illegal character are prepended with
   * <code>prefix</code>. All other illegal characters are omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @return The string transformed to a valid Java Identifier in Title-CamelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toClassCase(final String string, final char prefix) {
    return transform(string, prefix, '\0', null, JavaIdentifiers::toCamelCase0);
  }

  /**
   * Transforms a string into a valid Java Identifier in Title-CamelCase.
   * Strings that start with an illegal character are prepended with
   * <code>X</code>. All other illegal characters are omitted.
   *
   * @param string The input string.
   * @return The string transformed to a valid Java Identifier in Title-CamelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toClassCase(final String string) {
    return transform(string, 'X', '\0', null, JavaIdentifiers::toCamelCase0);
  }

  /**
   * Transforms a string into a valid Java Identifier in Title-CamelCase.
   * Strings that start with an illegal character are prepended with
   * <code>X</code>. All other illegal characters are substituted with the
   * string value mapped to the key of the character in <code>substitutes</code>.
   * If the mapping is missing, the illegal character is omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the
   *        first character is not valid.
   * @param substitutes The mapping of illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier in Title-CamelCase.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toClassCase(final String string, final Map<Character,String> substitutes) {
    return transform(string, 'X', '\0', substitutes, JavaIdentifiers::toCamelCase0);
  }

  private JavaIdentifiers() {
  }
}