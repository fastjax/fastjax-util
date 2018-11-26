/* Copyright (c) 2013 FastJAX
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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utility functions for operations pertaining to {@code Throwable}.
 */
public final class Throwables {
  /**
   * Returns the string representation of the specified throwable and its
   * backtrace.
   *
   * @param t The throwable.
   * @return The string representation of the specified throwable and its
   *         backtrace.
   * @see Throwable#printStackTrace(java.io.PrintStream)
   */
  public static String toString(final Throwable t) {
    final StringWriter stringWriter = new StringWriter();
    t.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }

  private Throwables() {
  }
}