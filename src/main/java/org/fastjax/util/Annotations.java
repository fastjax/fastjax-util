/* Copyright (c) 2018 FastJAX
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

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Utility functions for operations pertaining to {@link Annotation}.
 */
public final class Annotations {
  /**
   * Returns a {@code toString()} representation of {@code annotation}, with its
   * property names sorted alphabetically.
   * <p>
   * This method is equivalent to calling
   * {@code toSortedString(annotation, null)}
   *
   * @param annotation The {@link Annotation}.
   * @return A {@code toString()} representation of {@code annotation}, with its
   *         property names sorted by {@code comparator}.
   */
  public static String toSortedString(final Annotation annotation) {
    return toSortedString(annotation, null);
  }

  /**
   * Returns a {@code toString()} representation of {@code annotation}, with its
   * property names sorted by {@code comparator}.
   *
   * @param annotation The {@link Annotation}.
   * @param comparator The {@link Comparator}.
   * @return A {@code toString()} representation of {@code annotation}, with its
   *         property names sorted by {@code comparator}.
   */
  public static String toSortedString(final Annotation annotation, final Comparator<String> comparator) {
    final String str = annotation.toString();
    final int start = str.indexOf('(') + 1;
    if (start == 0)
      return str;

    final int end = str.lastIndexOf(')');
    final TreeMap<String,String> map = comparator != null ? new TreeMap<>(comparator) : new TreeMap<>();
    for (int index = end; index > start;) {
      final int eq = Strings.lastIndexOfUnQuoted(str, '=', index);
      final int comma = str.lastIndexOf(',', eq);
      final String key = str.substring(comma > start ? comma + 1 : start, eq).trim();
      final String value = str.substring(eq + 1, index).trim();
      map.put(key, value);
      index = comma;
    }

    final StringBuilder properties = new StringBuilder();
    final Iterator<Map.Entry<String,String>> iterator = map.entrySet().iterator();
    for (int i = 0; iterator.hasNext(); ++i) {
      if (i > 0)
        properties.append(", ");

      final Map.Entry<String,String> entry = iterator.next();
      properties.append(entry.getKey()).append('=').append(entry.getValue());
    }

    return new StringBuilder(str).replace(start, end, properties.toString()).toString();
  }

  private Annotations() {
  }
}