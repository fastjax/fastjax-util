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

package org.fastjax.util.retry;

/**
 * Class representing a retryable exception that qualifies for retried
 * invocation, which is the default behavior defined in
 * {@link RetryPolicy#retryOn(Exception)}.
 */
public class RetryException extends RuntimeException {
  private static final long serialVersionUID = -215964084300420516L;

  /**
   * Creates a {@code RetryException} with no detail message.
   */
  public RetryException() {
  }

  /**
   * Creates a {@code RetryException} with the specified detail message.
   *
   * @param message The detail message.
   */
  public RetryException(final String message) {
    super(message);
  }

  /**
   * Constructs a {@code RetryException} with the specified exception that was
   * the cause of this exception.
   *
   * @param cause The exception that was the cause of this exception.
   */
  public RetryException(final Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a {@code RetryException} with the specified detail message and
   * exception that was the cause of this exception.
   *
   * @param message The detail message.
   * @param cause The exception that was the cause of this exception.
   */
  public RetryException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a {@code RetryException} with the specified detail message,
   * cause, suppression enabled or disabled, and writable stack trace enabled or
   * disabled.
   *
   * @param message The detail message.
   * @param cause The exception that was the cause of this exception.
   * @param enableSuppression Whether or not suppression is enabled or disabled.
   * @param writableStackTrace Whether or not the stack trace should be
   *          writable.
   */
  public RetryException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}