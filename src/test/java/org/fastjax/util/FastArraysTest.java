/* Copyright (c) 2016 FastJAX
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

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import org.junit.Assert;
import org.junit.Test;

public class FastArraysTest {
  private static Object[] createRandomNestedArray() {
    final Object[] array = new Object[4];
    for (int i = 0; i < array.length; i++)
      array[i] = Math.random() < 0.2 ? createRandomNestedArray() : Strings.getRandomAlphaString(4);

    return array;
  }

  @Test
  public void testFlattenArrayRetainingReferences() {
    final Object[] array = {"a", "b", new Object[] {"c", "d"}, "e", new Object[] {"f", new Object[] {"g", "h"}}, "i"};
    final Object[] result = FastArrays.flatten(array);
    assertEquals("[" + java.util.Arrays.deepToString(array).replace("[", "").replace("]", "") + "]", java.util.Arrays.deepToString(result).toString());

    final Object[] expected = {array[0], array[1], array[2], ((Object[])array[2])[0], ((Object[])array[2])[1], array[3], array[4], ((Object[])array[4])[0], ((Object[])array[4])[1], ((Object[])((Object[])array[4])[1])[0], ((Object[])((Object[])array[4])[1])[1], array[5]};
    final Object[] resultRetainingReferences = FastArrays.flatten(array, true);
    assertArrayEquals(expected, resultRetainingReferences);
  }

  @Test
  public void testFlattenArray() {
    for (int i = 0; i < 100; i++) {
      final Object[] array = createRandomNestedArray();
      final Object[] result = FastArrays.flatten(array);
      assertEquals("[" + java.util.Arrays.deepToString(array).replace("[", "").replace("]", "") + "]", java.util.Arrays.deepToString(result).toString());
    }
  }

  @Test
  public void testBinaryClosestSearch() {
    final int[] sorted = new int[] {1, 3, 5, 9, 19};
    try {
      FastArrays.binaryClosestSearch(sorted, 4, 3, -10);
      Assert.fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    Assert.assertEquals(0, FastArrays.binaryClosestSearch(sorted, -10));
    Assert.assertEquals(0, FastArrays.binaryClosestSearch(sorted, -1));
    Assert.assertEquals(0, FastArrays.binaryClosestSearch(sorted, 0));
    Assert.assertEquals(1, FastArrays.binaryClosestSearch(sorted, 2));
    Assert.assertEquals(2, FastArrays.binaryClosestSearch(sorted, 4));
    Assert.assertEquals(3, FastArrays.binaryClosestSearch(sorted, 6));
    Assert.assertEquals(3, FastArrays.binaryClosestSearch(sorted, 9));
    Assert.assertEquals(4, FastArrays.binaryClosestSearch(sorted, 10));
    Assert.assertEquals(4, FastArrays.binaryClosestSearch(sorted, 14));
    Assert.assertEquals(4, FastArrays.binaryClosestSearch(sorted, 18));
    Assert.assertEquals(4, FastArrays.binaryClosestSearch(sorted, 19));
    Assert.assertEquals(5, FastArrays.binaryClosestSearch(sorted, 20));
    Assert.assertEquals(5, FastArrays.binaryClosestSearch(sorted, 40));
  }

  @Test
  public void testTransform() {
    Assert.assertArrayEquals(new String[] {"ONE", "TWO", "THREE"}, FastArrays.<String>replaceAll(new UnaryOperator<String>() {
      @Override
      public String apply(final String value) {
        return value.toUpperCase();
      }
    }, new String[] {"one", "two", "three"}));

    Assert.assertArrayEquals(new String[] {}, FastArrays.<String>replaceAll(new UnaryOperator<String>() {
      @Override
      public String apply(final String value) {
        return value.toUpperCase();
      }
    }, new String[] {}));
  }

  @Test
  public void testFilter() {
    final String[] expected = new String[] {"ONE", "TWO", "THREE"};
    final String[] filtered = FastArrays.filter(new Predicate<String>() {
      @Override
      public boolean test(final String value) {
        return value != null;
      }
    }, new String[] {"ONE", null, "TWO", null, "THREE"});
    Assert.assertArrayEquals(expected, filtered);
  }

  @Test
  public void testConcat() {
    final String[] one = new String[] {"a", "b", "c"};
    final String[] two = new String[] {"d", "e", "f"};
    final String[] concat = FastArrays.concat(one, two);
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d", "e", "f"}, concat);
  }

  @Test
  public void testSubArray() {
    final String[] array = new String[] {"a", "b", "c", "d", "e", "f"};
    Assert.assertArrayEquals(new String[] {"c", "d", "e"}, FastArrays.subArray(array, 2, 5));
    Assert.assertArrayEquals(new String[] {"c", "d", "e", "f"}, FastArrays.subArray(array, 2));
  }

  @Test
  public void testSplice() {
    final String[] array = new String[] {"a", "b", "c", "d", "e", "f"};

    Assert.assertArrayEquals(new String[] {"c", "d", "e", "f"}, FastArrays.splice(array, 0, 2));
    Assert.assertArrayEquals(new String[] {"a", "d", "e", "f"}, FastArrays.splice(array, 1, 2));
    Assert.assertArrayEquals(new String[] {"a", "b", "e", "f"}, FastArrays.splice(array, 2, 2));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "f"}, FastArrays.splice(array, 3, 2));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d"}, FastArrays.splice(array, 4, 2));
    try {
      FastArrays.splice(array, 5, 2);
      Assert.fail("Expected ArrayIndexOutOfBoundsException");
    }
    catch (final ArrayIndexOutOfBoundsException e) {
    }

    try {
      FastArrays.splice(array, -2, 3);
      Assert.fail("Expected ArrayIndexOutOfBoundsException");
    }
    catch (final ArrayIndexOutOfBoundsException e) {
    }
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d"}, FastArrays.splice(array, -2, 2));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "f"}, FastArrays.splice(array, -3, 2));
    Assert.assertArrayEquals(new String[] {"a", "b", "e", "f"}, FastArrays.splice(array, -4, 2));
    Assert.assertArrayEquals(new String[] {"a", "d", "e", "f"}, FastArrays.splice(array, -5, 2));
    Assert.assertArrayEquals(new String[] {"c", "d", "e", "f"}, FastArrays.splice(array, -6, 2));
    try {
      FastArrays.splice(array, -7, 2);
      Assert.fail("Expected ArrayIndexOutOfBoundsException");
    }
    catch (final ArrayIndexOutOfBoundsException e) {
    }

    Assert.assertArrayEquals(new String[] {}, FastArrays.splice(array, 0));
    Assert.assertArrayEquals(new String[] {"a"}, FastArrays.splice(array, 1));
    Assert.assertArrayEquals(new String[] {"a", "b"}, FastArrays.splice(array, 2));
    Assert.assertArrayEquals(new String[] {"a", "b", "c"}, FastArrays.splice(array, 3));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d"}, FastArrays.splice(array, 4));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d", "e"}, FastArrays.splice(array, 5));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d", "e", "f"}, FastArrays.splice(array, 6));
    try {
      FastArrays.splice(array, 7);
      Assert.fail("Expected ArrayIndexOutOfBoundsException");
    }
    catch (final ArrayIndexOutOfBoundsException e) {
    }

    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d", "e"}, FastArrays.splice(array, -1));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d"}, FastArrays.splice(array, -2));
    Assert.assertArrayEquals(new String[] {"a", "b", "c"}, FastArrays.splice(array, -3));
    Assert.assertArrayEquals(new String[] {"a", "b"}, FastArrays.splice(array, -4));
    Assert.assertArrayEquals(new String[] {"a"}, FastArrays.splice(array, -5));
    Assert.assertArrayEquals(new String[] {}, FastArrays.splice(array, -6));
    try {
      FastArrays.splice(array, -7);
      Assert.fail("Expected NegativeArraySizeException");
    }
    catch (final NegativeArraySizeException e) {
    }

    Assert.assertArrayEquals(new String[] {"x", "y", "z", "c", "d", "e", "f"}, FastArrays.splice(array, 0, 2, "x", "y", "z"));
    Assert.assertArrayEquals(new String[] {"a", "x", "y", "z", "d", "e", "f"}, FastArrays.splice(array, 1, 2, "x", "y", "z"));
    Assert.assertArrayEquals(new String[] {"a", "b", "x", "y", "z", "e", "f"}, FastArrays.splice(array, 2, 2, "x", "y", "z"));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "x", "y", "z", "f"}, FastArrays.splice(array, 3, 2, "x", "y", "z"));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d", "x", "y", "z"}, FastArrays.splice(array, 4, 2, "x", "y", "z"));
    try {
      FastArrays.splice(array, 5, 2, "x", "y", "z");
      Assert.fail("Expected ArrayIndexOutOfBoundsException");
    }
    catch (final ArrayIndexOutOfBoundsException e) {
    }

    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d", "x", "y", "z"}, FastArrays.splice(array, -2, 2, "x", "y", "z"));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "x", "y", "z", "f"}, FastArrays.splice(array, -3, 2, "x", "y", "z"));
    Assert.assertArrayEquals(new String[] {"a", "b", "x", "y", "z", "e", "f"}, FastArrays.splice(array, -4, 2, "x", "y", "z"));
    Assert.assertArrayEquals(new String[] {"a", "x", "y", "z", "d", "e", "f"}, FastArrays.splice(array, -5, 2, "x", "y", "z"));
    Assert.assertArrayEquals(new String[] {"x", "y", "z", "c", "d", "e", "f"}, FastArrays.splice(array, -6, 2, "x", "y", "z"));
    try {
      FastArrays.splice(array, -7, 2, "x", "y", "z");
      Assert.fail("Expected ArrayIndexOutOfBoundsException");
    }
    catch (final ArrayIndexOutOfBoundsException e) {
    }
  }

  @Test
  public void testCreateRepeat() {
    final String[] array = FastArrays.createRepeat("hello", 8);
    Assert.assertArrayEquals(new String[] {"hello", "hello", "hello", "hello", "hello", "hello", "hello", "hello"}, array);
  }
}