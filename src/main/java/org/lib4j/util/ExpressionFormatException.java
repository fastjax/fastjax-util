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

public final class ExpressionFormatException extends RuntimeException {
  private static final long serialVersionUID = 4220959991784197879L;

  public ExpressionFormatException() {
    super();
  }

  public ExpressionFormatException(final String message) {
    super(message);
  }

  public ExpressionFormatException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public ExpressionFormatException(final Throwable cause) {
    super(cause);
  }
}