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

package org.fastjax.util.memory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.fastjax.util.MemoryURLConnection;
import org.fastjax.util.MemoryURLStreamHandler;

/**
 * Handler class extending {@link MemoryURLStreamHandler}. This class is used
 * for handler registration with the {@code "java.protocol.handler.pkgs"} system
 * property.
 */
public class Handler extends MemoryURLStreamHandler {
  @Override
  protected URLConnection openConnection(final URL url) throws IOException {
    if (!"memory".equals(url.getProtocol()))
      throw new MalformedURLException("Unsupported protocol: " + url.getProtocol());

    final byte[] data = idToData.get(url.getPath());
    if (data == null)
      throw new IOException("URL not registered: " + url);

    return new MemoryURLConnection(url, data);
  }
}