/* Copyright (c) 2006 LibJ
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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassesTest {
  private static final Logger logger = LoggerFactory.getLogger(ClassesTest.class);

  private final Map<Class<?>[],Class<?>> classes = new HashMap<>();

  @Before
  public void setUp() {
    classes.put(new Class[] {String.class}, String.class);
    classes.put(new Class[] {String.class, Integer.class}, Object.class);
    classes.put(new Class[] {Long.class, Integer.class}, Number.class);
    classes.put(new Class[] {ArrayList.class, LinkedList.class}, AbstractList.class);
    classes.put(new Class[] {HashSet.class, LinkedHashSet.class}, HashSet.class);
    classes.put(new Class[] {FileInputStream.class, ByteArrayInputStream.class, DataInputStream.class, FilterInputStream.class}, InputStream.class);
  }

  @Test
  public void testGreatestCommonClass() throws Exception {
    try {
      Classes.getGreatestCommonSuperclass((Class<?>[])null);
      fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    for (final Map.Entry<Class<?>[],Class<?>> entry : classes.entrySet())
      assertEquals(entry.getValue(), Classes.getGreatestCommonSuperclass(entry.getKey()));
  }

  @Test
  public void testGetExecutionStack() {
    final Class<?>[] classes = Classes.getExecutionStack();
    for (final Class<?> cls : classes)
      logger.info(cls.getName());
  }

  protected static class Inn$r {
    protected static class $nner {
      protected static class $nner$ {
      }
    }
  }

  @Test
  public void testGetDeclaringClassName() {
    try {
      Classes.getDeclaringClassName(null);
      fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    try {
      Classes.getDeclaringClassName("");
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals("One", Classes.getDeclaringClassName("One$Two"));
    assertEquals("One", Classes.getDeclaringClassName("One$Two$"));
    assertEquals("$Two", Classes.getDeclaringClassName("$Two"));
    assertEquals("Two$", Classes.getDeclaringClassName("Two$"));
    assertEquals("foo.One", Classes.getDeclaringClassName("foo.One$Two"));
    assertEquals("foo.One", Classes.getDeclaringClassName("foo.One$Two$"));
    assertEquals("foo.bar.One", Classes.getDeclaringClassName("foo.bar.One$Two"));
    assertEquals("foo.bar.One", Classes.getDeclaringClassName("foo.bar.One$Two$"));
    assertEquals("foo.bar.One$Two", Classes.getDeclaringClassName("foo.bar.One$Two$Three"));
    assertEquals("foo.bar.One$Two", Classes.getDeclaringClassName("foo.bar.One$Two$Three$"));

    assertEquals("foo.bar.One.$Two", Classes.getDeclaringClassName("foo.bar.One.$Two"));
    assertEquals("foo.bar.One.$Two$", Classes.getDeclaringClassName("foo.bar.One.$Two$"));
    assertEquals("foo.bar.One.$Two", Classes.getDeclaringClassName("foo.bar.One.$Two$$Three"));
    assertEquals("foo.bar.One.$Two", Classes.getDeclaringClassName("foo.bar.One.$Two$$Three$"));
    // FIXME: This is a problem with Java's inner class naming spec...
    assertEquals("foo.bar.One.$Two$$Three", Classes.getDeclaringClassName("foo.bar.One.$Two$$Three$$$Four"));
    assertEquals("foo.bar.One.$Two$$Three", Classes.getDeclaringClassName("foo.bar.One.$Two$$Three$$$Four$"));
    assertEquals("foo.bar.One.$Two.$$Three", Classes.getDeclaringClassName("foo.bar.One.$Two.$$Three"));
    assertEquals("foo.bar.One.$Two.$$Three$", Classes.getDeclaringClassName("foo.bar.One.$Two.$$Three$"));
  }

  @Test
  public void testGetRootDeclaringClassName() {
    try {
      Classes.getRootDeclaringClassName(null);
      fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    try {
      Classes.getRootDeclaringClassName("");
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals("One", Classes.getRootDeclaringClassName("One$Two"));
    assertEquals("One", Classes.getRootDeclaringClassName("One$Two$"));
    assertEquals("$Two", Classes.getRootDeclaringClassName("$Two"));
    assertEquals("Two$", Classes.getRootDeclaringClassName("Two$"));
    assertEquals("foo.One", Classes.getRootDeclaringClassName("foo.One$Two"));
    assertEquals("foo.One", Classes.getRootDeclaringClassName("foo.One$Two$"));
    assertEquals("foo.bar.One", Classes.getRootDeclaringClassName("foo.bar.One$Two"));
    assertEquals("foo.bar.One", Classes.getRootDeclaringClassName("foo.bar.One$Two$"));
    assertEquals("foo.bar.One", Classes.getRootDeclaringClassName("foo.bar.One$Two$Three"));
    assertEquals("foo.bar.One", Classes.getRootDeclaringClassName("foo.bar.One$Two$Three$"));

    assertEquals("foo.bar.One.$Two", Classes.getRootDeclaringClassName("foo.bar.One.$Two"));
    assertEquals("foo.bar.One.$Two$", Classes.getRootDeclaringClassName("foo.bar.One.$Two$"));
    assertEquals("foo.bar.One.$Two", Classes.getRootDeclaringClassName("foo.bar.One.$Two$$Three"));
    assertEquals("foo.bar.One.$Two", Classes.getRootDeclaringClassName("foo.bar.One.$Two$$Three$"));
    assertEquals("foo.bar.One.$Two", Classes.getRootDeclaringClassName("foo.bar.One.$Two$$Three$$$Four"));
    assertEquals("foo.bar.One.$Two", Classes.getRootDeclaringClassName("foo.bar.One.$Two$$Three$$$Four$"));
    assertEquals("foo.bar.One.$Two.$$Three", Classes.getRootDeclaringClassName("foo.bar.One.$Two.$$Three"));
    assertEquals("foo.bar.One.$Two.$$Three$", Classes.getRootDeclaringClassName("foo.bar.One.$Two.$$Three$"));
  }

  @Test
  public void testToCanonicalClassName() {
    try {
      Classes.toCanonicalClassName(null);
      fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    try {
      Classes.toCanonicalClassName("");
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals("Id", Classes.toCanonicalClassName("Id"));
    assertEquals("Ids", Classes.toCanonicalClassName("Ids"));
    assertEquals("$Two", Classes.toCanonicalClassName("$Two"));
    assertEquals("Two$", Classes.toCanonicalClassName("Two$"));
    assertEquals("One.Two", Classes.toCanonicalClassName("One$Two"));
    assertEquals("One.Two$", Classes.toCanonicalClassName("One$Two$"));
    assertEquals("foo.One.Two", Classes.toCanonicalClassName("foo.One$Two"));
    assertEquals("foo.One.Two$", Classes.toCanonicalClassName("foo.One$Two$"));
    assertEquals("foo.bar.One.Two", Classes.toCanonicalClassName("foo.bar.One$Two"));
    assertEquals("foo.bar.One.Two$", Classes.toCanonicalClassName("foo.bar.One$Two$"));

    assertEquals("foo.bar.One.$Two", Classes.toCanonicalClassName("foo.bar.One.$Two"));
    assertEquals("foo.bar.One.$Two$", Classes.toCanonicalClassName("foo.bar.One.$Two$"));
    // FIXME: This is a problem with Java's inner class naming spec...
    assertEquals("foo.bar.One.$Two.$Three", Classes.toCanonicalClassName("foo.bar.One.$Two$$Three"));
    assertEquals("foo.bar.One.$Two.$Three$", Classes.toCanonicalClassName("foo.bar.One.$Two$$Three$"));
    assertEquals("foo.bar.One.$Two.$$Three", Classes.toCanonicalClassName("foo.bar.One.$Two.$$Three"));
    assertEquals("foo.bar.One.$Two.$$Three$", Classes.toCanonicalClassName("foo.bar.One.$Two.$$Three$"));
  }

  @Test
  public void testGetCompoundName() {
    try {
      Classes.getCompoundName(null);
      fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    assertEquals("Map$Entry", Classes.getCompoundName(Map.Entry.class));
  }

  @Test
  public void testGetCanonicalCompoundName() {
    try {
      Classes.getCompoundName(null);
      fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    assertEquals("Map.Entry", Classes.getCanonicalCompoundName(Map.Entry.class));
  }

  @SuppressWarnings({"rawtypes", "unused"})
  private static class GetGenericTypesTest {
    private String nonGeneric;
    private Optional rawGeneric;
    private Optional<?> wildGeneric;
    private Optional<String> stringGeneric;
    private Map<List<Integer>,Map<List<Integer>,String>> multiGeneric;
  }

  @Test
  public void testGetGenericTypes() throws NoSuchFieldException {
    assertArrayEquals(new Class[0], Classes.getGenericClasses(GetGenericTypesTest.class.getDeclaredField("nonGeneric")));
    assertArrayEquals(new Class[0], Classes.getGenericClasses(GetGenericTypesTest.class.getDeclaredField("rawGeneric")));
    assertArrayEquals(new Class[] {null}, Classes.getGenericClasses(GetGenericTypesTest.class.getDeclaredField("wildGeneric")));
    assertArrayEquals(new Class[] {String.class}, Classes.getGenericClasses(GetGenericTypesTest.class.getDeclaredField("stringGeneric")));
    assertArrayEquals(new Class[] {List.class, Map.class}, Classes.getGenericClasses(GetGenericTypesTest.class.getDeclaredField("multiGeneric")));
  }

  @Test
  public void testGetClassHierarchy() {
    try {
      Classes.getClassHierarchy(null, c -> false);
      fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    try {
      Classes.getClassHierarchy(String.class, null);
      fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    final Class<?>[] hierarchy = {LinkedList.class, AbstractSequentialList.class, List.class, Deque.class, Cloneable.class, Serializable.class, AbstractList.class, Collection.class, Queue.class, AbstractCollection.class, Iterable.class, Object.class};

    assertEquals(CollectionUtil.asCollection(new LinkedHashSet<>(), hierarchy), Classes.getClassHierarchy(LinkedList.class, c -> true));
    assertEquals(CollectionUtil.asCollection(new LinkedHashSet<>(), hierarchy, 0, 6), Classes.getClassHierarchy(LinkedList.class, c -> c != Serializable.class));
    assertEquals(CollectionUtil.asCollection(new LinkedHashSet<>(), hierarchy, 0, 9), Classes.getClassHierarchy(LinkedList.class, c -> c != Queue.class));
  }
}