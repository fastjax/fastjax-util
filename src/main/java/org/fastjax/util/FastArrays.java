/* Copyright (c) 2014 FastJAX
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

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Utility functions for operations pertaining to arrays.
 */
public final class FastArrays {
  /**
   * Returns the length of the specified array, summed with the lengths of all
   * nested arrays at every depth. The value of
   * {@code member.getClass().isArray()} is used to determine whether an array
   * member represents an array for further recursion.
   * <p>
   * Array members that reference an array are <i>not included</i> in the count.
   * This is the equivalent of calling {@code lengthDeep(Object[],false)}.
   *
   * @param a The array.
   * @return The length of the specified array, summed with the lengths of all
   *         nested arrays at every depth.
   * @throws NullPointerException If {@code a} is null.
   */
  public static int lengthDeep(final Object[] a) {
    return lengthDeep(a, false);
  }

  /**
   * Returns the length of the specified array, summed with the lengths of all
   * nested arrays at every depth. The value of
   * {@code member.getClass().isArray()} is used to determine whether an array
   * member represents an array for further recursion.
   *
   * @param <T> The component type of the specified array.
   * @param a The array.
   * @param countArrayReferences If {@code true}, array members that reference
   *          an array are included in the count; if {@code false}, they are not
   *          included in the count.
   * @return The length of the specified array, summed with the lengths of all
   *         nested arrays at every depth.
   * @throws NullPointerException If {@code a} is null.
   */
  public static <T>int lengthDeep(final T[] a, final boolean countArrayReferences) {
    return lengthDeep(a, null, countArrayReferences);
  }

  /**
   * Returns the length of the specified array, summed with the lengths of all
   * nested arrays at every depth. The specified resolver {@link Function}
   * provides a layer of indirection between an array member, and a higher-layer
   * value. This is useful in the situation where the array contains symbolic
   * references to other arrays. The {@code resolver} parameter is provided to
   * dereference such a symbolic references.
   *
   * @param <T> The component type of the specified array.
   * @param a The array.
   * @param resolver A {@link Function} to provide a layer of indirection
   *          between an array member, and a higher-layer value. If
   *          {@code resolver} is null, {@code member.getClass().isArray()} is
   *          used to determine whether the member value represents an array.
   * @param countArrayReferences If {@code true}, array members that reference
   *          an array are included in the count; if {@code false}, they are not
   *          included in the count.
   * @return The length of the specified array, summed with the lengths of all
   *         nested arrays at every depth.
   * @throws NullPointerException If {@code a} is null.
   */
  @SuppressWarnings("unchecked")
  public static <T>int lengthDeep(final T[] a, final Function<T,T[]> resolver, final boolean countArrayReferences) {
    int size = 0;
    for (int i = 0; i < a.length; ++i) {
      final T member = a[i];
      final T[] inner = member == null ? null : resolver != null ? resolver.apply(member) : member.getClass().isArray() ? (T[])member : null;
      if (inner != null) {
        size += lengthDeep(inner, resolver, countArrayReferences);
        if (countArrayReferences)
          ++size;
      }
      else {
        ++size;
      }
    }

    return size;
  }

  /**
   * Returns a one-dimensional array with the members of the specified array,
   * and the members of all nested arrays at every depth. The value of
   * {@code member.getClass().isArray()} is used to determine whether an array
   * member represents an array for further recursion.
   * <p>
   * Array members that reference an array are <i>not included</i> in the
   * resulting array. This is the equivalent of calling
   * {@code flatten(Object[],false)}.
   *
   * @param a The array.
   * @return A one-dimensional array with the members of the specified array,
   *         and the members of all nested arrays at every depth.
   * @throws NullPointerException If {@code a} is null.
   */
  public static Object[] flatten(final Object[] a) {
    return flatten(a, null, false);
  }

  /**
   * Returns a one-dimensional array with the members of the specified array,
   * and the members of all nested arrays at every depth. The value of
   * {@code member.getClass().isArray()} is used to determine whether an array
   * member represents an array for further recursion.
   *
   * @param a The array.
   * @param retainArrayReferences If {@code true}, array members that reference
   *          an array are included in the resulting array; if {@code false},
   *          they are not included in the resulting array.
   * @return A one-dimensional array with the members of the specified array,
   *         and the members of all nested arrays at every depth.
   * @throws NullPointerException If {@code a} is null.
   */
  public static Object[] flatten(final Object[] a, final boolean retainArrayReferences) {
    return flatten(a, null, retainArrayReferences);
  }

  /**
   * Returns a one-dimensional array with the members of the specified array,
   * and the members of all nested arrays at every depth. The specified resolver
   * {@link Function} provides a layer of indirection between an array member,
   * and a higher-layer value. This is useful in the situation where the array
   * contains symbolic references to other arrays. The {@code resolver}
   * parameter is provided to dereference such a symbolic references.
   *
   * @param <T> The component type of the specified array.
   * @param a The array.
   * @param resolver A {@link Function} to provide a layer of indirection
   *          between an array member, and a higher-layer value. If
   *          {@code resolver} is null, {@code member.getClass().isArray()} is
   *          used to determine whether the member value represents an array.
   * @param retainArrayReferences If {@code true}, array members that reference
   *          an array are included in the resulting array; if {@code false}, they are not
   *          included in the resulting array.
   * @return A one-dimensional array with the members of the specified array,
   *         and the members of all nested arrays at every depth.
   * @throws NullPointerException If {@code a} is null.
   */
  @SuppressWarnings("unchecked")
  public static <T>T[] flatten(final T[] a, final Function<T,T[]> resolver, final boolean retainArrayReferences) {
    final T[] out = (T[])Array.newInstance(a.getClass().getComponentType(), lengthDeep(a, resolver, retainArrayReferences));
    flatten0(a, out, resolver, retainArrayReferences, -1);
    return out;
  }

  @SuppressWarnings("unchecked")
  private static <T>int flatten0(final T[] in, final Object[] out, final Function<T,T[]> resolver, final boolean retainArrayReferences, int index) {
    for (int i = 0; i < in.length; ++i) {
      final T member = in[i];
      final T[] inner = member == null ? null : resolver != null ? resolver.apply(member) : member.getClass().isArray() ? (T[])member : null;
      if (inner != null) {
        if (retainArrayReferences)
          out[++index] = member;

        index = flatten0(inner, out, resolver, retainArrayReferences, index);
      }
      else {
        out[++index] = member;
      }
    }

    return index;
  }

  /**
   * Find the index of the sorted array whose value most closely matches the
   * value provided.
   *
   * @param <T> Type parameter of {@link Comparable} object.
   * @param a The sorted array.
   * @param key The value to match.
   * @return The closest index of the sorted array matching the desired value.
   */
  public static <T extends Comparable<T>>int binaryClosestSearch(final T[] a, final T key) {
    return binaryClosestSearch0(a, 0, a.length, key);
  }

  /**
   * Find the index of the sorted array whose value most closely matches the
   * value provided.
   *
   * @param <T> Type parameter of {@link Comparable} object.
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   * @return The closest index of the sorted array matching the desired value.
   */
  public static <T extends Comparable<T>>int binaryClosestSearch(final T[] a, final int from, final int to, final T key) {
    rangeCheck(a.length, from, to);
    return binaryClosestSearch0(a, from, to, key);
  }

  /**
   * Find the index of the sorted array whose value most closely matches the
   * value provided.
   *
   * @param <T> Type parameter of object.
   * @param a The sorted array.
   * @param key The value to match.
   * @param comparator The {@link Comparator} for objects of type {@code <T>}.
   * @return The closest index of the sorted array matching the desired value.
   */
  public static <T>int binaryClosestSearch(final T[] a, final T key, final Comparator<T> comparator) {
    return binaryClosestSearch0(a, 0, a.length, key, comparator);
  }

  /**
   * Find the index of the sorted array whose value most closely matches the
   * value provided.
   *
   * @param <T> Type parameter of object.
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   * @param comparator The {@link Comparator} for objects of type {@code <T>}.
   * @return The closest index of the sorted array matching the desired value.
   */
  public static <T>int binaryClosestSearch(final T[] a, final int from, final int to, final T key, final Comparator<T> comparator) {
    rangeCheck(a.length, from, to);
    return binaryClosestSearch0(a, from, to, key, comparator);
  }

  /**
   * Checks that {@code fromIndex} and {@code toIndex} are in the range and
   * throws an exception if they aren't.
   */
  private static void rangeCheck(final int arrayLength, final int fromIndex, final int toIndex) {
    if (fromIndex > toIndex)
      throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");

    if (fromIndex < 0)
      throw new ArrayIndexOutOfBoundsException(fromIndex);

    if (toIndex > arrayLength)
      throw new ArrayIndexOutOfBoundsException(toIndex);
  }

  private static <T extends Comparable<T>>int binaryClosestSearch0(final T[] a, int from, int to, final T key) {
    for (int mid; from < to;) {
      mid = (from + to) / 2;
      final int comparison = key.compareTo(a[mid]);
      if (comparison < 0)
        to = mid;
      else if (comparison > 0)
        from = mid + 1;
      else
        return mid;
    }

    return (from + to) / 2;
  }

  private static <T>int binaryClosestSearch0(final T[] a, int from, int to, final T key, final Comparator<T> comparator) {
    for (int mid; from < to;) {
      mid = (from + to) / 2;
      final int comparison = comparator.compare(key, a[mid]);
      if (comparison < 0)
        to = mid;
      else if (comparison > 0)
        from = mid + 1;
      else
        return mid;
    }

    return (from + to) / 2;
  }

  /**
   * Find the index of the sorted array whose value most closely matches the
   * value provided.
   *
   * @param a The sorted array.
   * @param key The value to match.
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final byte[] a, final byte key) {
    return binaryClosestSearch0(a, 0, a.length, key);
  }

  /**
   * Find the index of the sorted array whose value most closely matches the
   * value provided.
   *
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final byte[] a, final int from, final int to, final byte key) {
    rangeCheck(a.length, from, to);
    return binaryClosestSearch0(a, from, to, key);
  }

  private static int binaryClosestSearch0(final byte[] a, int from, int to, final byte key) {
    for (int mid; from < to;) {
      mid = (from + to) / 2;
      if (key < a[mid])
        to = mid;
      else if (key > a[mid])
        from = mid + 1;
      else
        return mid;
    }

    return (from + to) / 2;
  }

  /**
   * Find the index of the sorted array whose value most closely matches the
   * value provided.
   *
   * @param a The sorted array.
   * @param key The value to match.
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final short[] a, final short key) {
    return binaryClosestSearch0(a, 0, a.length, key);
  }

  /**
   * Find the index of the sorted array whose value most closely matches the
   * value provided.
   *
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final short[] a, final int from, final int to, final short key) {
    rangeCheck(a.length, from, to);
    return binaryClosestSearch0(a, from, to, key);
  }

  private static int binaryClosestSearch0(final short[] a, int from, int to, final short key) {
    for (int mid; from < to;) {
      mid = (from + to) / 2;
      if (key < a[mid])
        to = mid;
      else if (key > a[mid])
        from = mid + 1;
      else
        return mid;
    }

    return (from + to) / 2;
  }

  /**
   * Find the index of the sorted array whose value most closely matches the
   * value provided.
   *
   * @param a The sorted array.
   * @param key The value to match.
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final int[] a, final int key) {
    return binaryClosestSearch0(a, 0, a.length, key);
  }

  /**
   * Find the index of the sorted array whose value most closely matches the
   * value provided.
   *
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final int[] a, final int from, final int to, final int key) {
    rangeCheck(a.length, from, to);
    return binaryClosestSearch0(a, from, to, key);
  }

  private static int binaryClosestSearch0(final int[] a, int from, int to, final int key) {
    for (int mid; from < to;) {
      mid = (from + to) / 2;
      if (key < a[mid])
        to = mid;
      else if (key > a[mid])
        from = mid + 1;
      else
        return mid;
    }

    return (from + to) / 2;
  }

  /**
   * Find the index of the sorted array whose value most closely matches the
   * value provided.
   *
   * @param a The sorted array.
   * @param key The value to match.
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final float[] a, final float key) {
    return binaryClosestSearch0(a, 0, a.length, key);
  }

  /**
   * Find the index of the sorted array whose value most closely matches the
   * value provided.
   *
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final float[] a, final int from, final int to, final float key) {
    rangeCheck(a.length, from, to);
    return binaryClosestSearch0(a, from, to, key);
  }

  private static int binaryClosestSearch0(final float[] a, int from, int to, final float key) {
    for (int mid; from < to;) {
      mid = (from + to) / 2;
      if (key < a[mid])
        to = mid;
      else if (key > a[mid])
        from = mid + 1;
      else
        return mid;
    }

    return (from + to) / 2;
  }

  /**
   * Find the index of the sorted array whose value most closely matches the
   * value provided.
   *
   * @param a The sorted array.
   * @param key The value to match.
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final double[] a, final double key) {
    return binaryClosestSearch0(a, 0, a.length, key);
  }

  /**
   * Find the index of the sorted array whose value most closely matches the
   * value provided.
   *
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final double[] a, final int from, final int to, final double key) {
    rangeCheck(a.length, from, to);
    return binaryClosestSearch0(a, from, to, key);
  }

  private static int binaryClosestSearch0(final double[] a, int from, int to, final double key) {
    for (int mid; from < to;) {
      mid = (from + to) / 2;
      if (key < a[mid])
        to = mid;
      else if (key > a[mid])
        from = mid + 1;
      else
        return mid;
    }

    return (from + to) / 2;
  }

  /**
   * Find the index of the sorted array whose value most closely matches the
   * value provided.
   *
   * @param a The sorted array.
   * @param key The value to match.
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final long[] a, final long key) {
    return binaryClosestSearch0(a, 0, a.length, key);
  }

  /**
   * Find the index of the sorted array whose value most closely matches the
   * value provided.
   *
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final long[] a, final int from, final int to, final long key) {
    rangeCheck(a.length, from, to);
    return binaryClosestSearch0(a, from, to, key);
  }

  private static int binaryClosestSearch0(final long[] a, int from, int to, final long key) {
    for (int mid; from < to;) {
      mid = (from + to) / 2;
      if (key < a[mid])
        to = mid;
      else if (key > a[mid])
        from = mid + 1;
      else
        return mid;
    }

    return (from + to) / 2;
  }

  /**
   * Replace all members of the provided array with the provided
   * {@link UnaryOperator}.
   *
   * @param <T> Type parameter of object.
   * @param operator The {@link UnaryOperator} that defines the replacement
   *          operation.
   * @param array The array whose members are to be replaced.
   * @return The the original array instance with its members replaced by the
   *         operator.
   */
  @SafeVarargs
  public static <T>T[] replaceAll(final UnaryOperator<T> operator, final T ... array) {
    for (int i = 0; i < array.length; ++i)
      array[i] = operator.apply(array[i]);

    return array;
  }

  /**
   * Filter the provided array with the provided {@link Predicate}. This
   * method recursive walks the members of the array to attain highest runtime
   * and memory performance.
   *
   * @param <T> Type parameter of object.
   * @param precicate The {@link Predicate} that defines the filter.
   * @param array The array whose members are to be filtered.
   * @return A new array instance with members that pass the filter.
   */
  @SafeVarargs
  public static <T>T[] filter(final Predicate<T> precicate, final T ... array) {
    return filter0(precicate, 0, 0, array);
  }

  @SuppressWarnings("unchecked")
  private static <T>T[] filter0(final Predicate<T> precicate, final int index, final int depth, final T ... array) {
    if (index == array.length)
      return (T[])Array.newInstance(array.getClass().getComponentType(), depth);

    final boolean accept = precicate.test(array[index]);
    final T[] filtered = filter0(precicate, index + 1, accept ? depth + 1 : depth, array);
    if (accept)
      filtered[depth] = array[index];

    return filtered;
  }

  @SafeVarargs
  @SuppressWarnings("unchecked")
  private static <T>T[] concat0(final T[] ... arrays) {
    int length = 0;
    for (final T[] array : arrays)
      length += array.length;

    final T[] concat = (T[])Array.newInstance(arrays[0].getClass().getComponentType(), length);
    for (int i = 0, l = 0; i < arrays.length; l += arrays[i].length, ++i)
      System.arraycopy(arrays[i], 0, concat, l, arrays[i].length);

    return concat;
  }

  /**
   * Concatenate the provided arrays of a common type into a single array.
   *
   * @param <T> Type parameter of object.
   * @param arrays The arrays to be concatenated.
   * @return The concatenated array.
   */
  @SafeVarargs
  public static <T>T[] concat(final T[] ... arrays) {
    return concat0(arrays);
  }

  /**
   * Concatenate the provided arrays of a common type into a single array.
   *
   * @param <T> Type parameter of object.
   * @param array The base array to be concatenated.
   * @param elements The additional elements to be concatenated.
   * @return The concatenated array.
   */
  @SafeVarargs
  public static <T>T[] concat(final T[] array, final T ... elements) {
    return concat0(array, elements);
  }

  /**
   * Concatenate the provided arrays of a common type into a single array.
   *
   * @param <T> Type parameter of object.
   * @param array The base array to be concatenated.
   * @param element The additional element to be concatenated.
   * @return The concatenated array.
   */
  @SuppressWarnings("unchecked")
  public static <T>T[] concat(final T[] array, final T element) {
    final T[] concat = (T[])Array.newInstance(array.getClass().getComponentType(), array.length + 1);
    System.arraycopy(array, 0, concat, 0, array.length);
    concat[array.length] = element;
    return concat;
  }

  /**
   * Create a new array by removing existing elements from the provided array.
   *
   * @param <T> Type parameter of object.
   * @param array The array to be spliced.
   * @param start Index at the greater of which to remove all elements in the
   *          array (with origin 0). If negative, index will be set to its
   *          calculated value from the end of the array (with origin 1).
   * @return A new array with elements removed from the provided array.
   */
  public static <T>T[] splice(final T[] array, int start) {
    if (start < 0)
      start += array.length;

    return splice(array, start, array.length - start);
  }

  /**
   * Create a new array by removing existing elements from the provided array.
   *
   * @param <T> Type parameter of object.
   * @param array The array to be spliced.
   * @param start Index at which to begin changing the array (with origin 0). If
   *          negative, index will be set to its calculated value from the end
   *          of the array (with origin 1).
   * @param deleteCount An integer indicating the number of array elements to
   *          remove. If deleteCount is 0, no elements are removed, but a new
   *          reference to the array is returned.
   * @return A new array with elements removed from the provided array.
   */
  @SuppressWarnings("unchecked")
  public static <T>T[] splice(final T[] array, int start, final int deleteCount) {
    if (deleteCount == 0)
      return array.clone();

    if (start < 0)
      start += array.length;

    final T[] spliced = (T[])Array.newInstance(array.getClass().getComponentType(), array.length - deleteCount);
    if (start != 0)
      System.arraycopy(array, 0, spliced, 0, start);

    if (start + deleteCount != array.length)
      System.arraycopy(array, start + deleteCount, spliced, start, array.length - start - deleteCount);

    return spliced;
  }

  /**
   * Create a new array by removing existing elements from the provided array.
   *
   * @param <T> Type parameter of object.
   * @param array The array to be spliced.
   * @param start Index at which to begin changing the array (with origin 0). If
   *          negative, index will be set to its calculated value from the end
   *          of the array (with origin 1).
   * @param deleteCount An integer indicating the number of array elements to
   *          remove. If deleteCount is 0, no elements are removed, but a new
   *          reference to the array is returned.
   * @param items The elements to add to the array, beginning at the start
   *          index.
   * @return A new array with elements removed from the provided array.
   */
  @SuppressWarnings("unchecked")
  public static <T>T[] splice(final T[] array, int start, final int deleteCount, final T ... items) {
    if (items.length == 0)
      return splice(array, start, deleteCount);

    if (start < 0)
      start += array.length;

    final T[] spliced = (T[])Array.newInstance(array.getClass().getComponentType(), array.length - deleteCount + items.length);
    if (start != 0)
      System.arraycopy(array, 0, spliced, 0, start);

    System.arraycopy(items, 0, spliced, start, items.length);

    if (start + deleteCount != array.length)
      System.arraycopy(array, start + deleteCount, spliced, start + items.length, array.length - start - deleteCount);

    return spliced;
  }

  /**
   * Find the index of an object in an array.
   *
   * @param <T> Type parameter of object.
   * @param array The array to search.
   * @param obj The object to locate.
   * @return The index of the object if it is found, or -1 otherwise.
   */
  public static <T>int indexOf(final T[] array, final T obj) {
    for (int i = 0; i < array.length; ++i)
      if (obj.equals(array[i]))
        return i;

    return -1;
  }

  /**
   * Check for the existence of an object in an array.
   *
   * @param <T> Type parameter of object.
   * @param array The array to search.
   * @param obj The object to locate.
   * @return {@code true} if the object exists, {@code false}
   *         otherwise.
   */
  public static <T>boolean contains(final T[] array, final T obj) {
    return indexOf(array, obj) >= 0;
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final byte[] array, final char delimiter) {
    return array == null ? null : toString(array, delimiter, 0, array.length);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final byte[] array, final char delimiter, final int offset) {
    return array == null ? null : toString(array, delimiter, offset, array.length - offset);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @param length The number of array elements to be included.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final byte[] array, final char delimiter, final int offset, final int length) {
    if (array == null)
      return null;

    if (array.length < offset)
      throw new ArrayIndexOutOfBoundsException(offset);

    if (array.length == offset)
      return "";

    if (array.length == offset + 1)
      return String.valueOf(array[offset]);

    final StringBuilder buffer = new StringBuilder(String.valueOf(array[offset]));
    for (int i = offset + 1; i < length + offset; ++i)
      buffer.append(delimiter).append(String.valueOf(array[i]));

    return buffer.toString();
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final byte[] array, final String delimiter) {
    return array == null ? null : toString(array, delimiter, 0, array.length);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final byte[] array, final String delimiter, final int offset) {
    return array == null ? null : toString(array, delimiter, offset, array.length - offset);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @param length The number of array elements to be included.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final byte[] array, final String delimiter, final int offset, final int length) {
    if (array == null)
      return null;

    if (array.length < offset)
      throw new ArrayIndexOutOfBoundsException(offset);

    if (array.length == offset)
      return "";

    if (array.length == offset + 1)
      return String.valueOf(array[offset]);

    final StringBuilder buffer = new StringBuilder(String.valueOf(array[offset]));
    for (int i = offset + 1; i < length + offset; ++i)
      buffer.append(delimiter).append(String.valueOf(array[i]));

    return buffer.toString();
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final char[] array, final char delimiter) {
    return array == null ? null : toString(array, delimiter, 0, array.length);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final char[] array, final char delimiter, final int offset) {
    return array == null ? null : toString(array, delimiter, offset, array.length - offset);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @param length The number of array elements to be included.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final char[] array, final char delimiter, final int offset, final int length) {
    if (array == null)
      return null;

    if (array.length < offset)
      throw new ArrayIndexOutOfBoundsException(offset);

    if (array.length == offset)
      return "";

    if (array.length == offset + 1)
      return String.valueOf(array[offset]);

    final StringBuilder buffer = new StringBuilder(String.valueOf(array[offset]));
    for (int i = offset + 1; i < length + offset; ++i)
      buffer.append(delimiter).append(String.valueOf(array[i]));

    return buffer.toString();
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final char[] array, final String delimiter) {
    return array == null ? null : toString(array, delimiter, 0, array.length);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final char[] array, final String delimiter, final int offset) {
    return array == null ? null : toString(array, delimiter, offset, array.length - offset);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @param length The number of array elements to be included.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final char[] array, final String delimiter, final int offset, final int length) {
    if (array == null)
      return null;

    if (array.length < offset)
      throw new ArrayIndexOutOfBoundsException(offset);

    if (array.length == offset)
      return "";

    if (array.length == offset + 1)
      return String.valueOf(array[offset]);

    final StringBuilder buffer = new StringBuilder(String.valueOf(array[offset]));
    for (int i = offset + 1; i < length + offset; ++i)
      buffer.append(delimiter).append(String.valueOf(array[i]));

    return buffer.toString();
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final short[] array, final char delimiter) {
    return array == null ? null : toString(array, delimiter, 0, array.length);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final short[] array, final char delimiter, final int offset) {
    return array == null ? null : toString(array, delimiter, offset, array.length - offset);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @param length The number of array elements to be included.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final short[] array, final char delimiter, final int offset, final int length) {
    if (array == null)
      return null;

    if (array.length < offset)
      throw new ArrayIndexOutOfBoundsException(offset);

    if (array.length == offset)
      return "";

    if (array.length == offset + 1)
      return String.valueOf(array[offset]);

    final StringBuilder buffer = new StringBuilder(String.valueOf(array[offset]));
    for (int i = offset + 1; i < length + offset; ++i)
      buffer.append(delimiter).append(String.valueOf(array[i]));

    return buffer.toString();
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final short[] array, final String delimiter) {
    return array == null ? null : toString(array, delimiter, 0, array.length);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final short[] array, final String delimiter, final int offset) {
    return array == null ? null : toString(array, delimiter, offset, array.length - offset);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @param length The number of array elements to be included.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final short[] array, final String delimiter, final int offset, final int length) {
    if (array == null)
      return null;

    if (array.length < offset)
      throw new ArrayIndexOutOfBoundsException(offset);

    if (array.length == offset)
      return "";

    if (array.length == offset + 1)
      return String.valueOf(array[offset]);

    final StringBuilder buffer = new StringBuilder(String.valueOf(array[offset]));
    for (int i = offset + 1; i < length + offset; ++i)
      buffer.append(delimiter).append(String.valueOf(array[i]));

    return buffer.toString();
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final int[] array, final char delimiter) {
    return array == null ? null : toString(array, delimiter, 0, array.length);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final int[] array, final char delimiter, final int offset) {
    return array == null ? null : toString(array, delimiter, offset, array.length - offset);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @param length The number of array elements to be included.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final int[] array, final char delimiter, final int offset, final int length) {
    if (array == null)
      return null;

    if (array.length < offset)
      throw new ArrayIndexOutOfBoundsException(offset);

    if (array.length == offset)
      return "";

    if (array.length == offset + 1)
      return String.valueOf(array[offset]);

    final StringBuilder buffer = new StringBuilder(String.valueOf(array[offset]));
    for (int i = offset + 1; i < length + offset; ++i)
      buffer.append(delimiter).append(String.valueOf(array[i]));

    return buffer.toString();
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final int[] array, final String delimiter) {
    return array == null ? null : toString(array, delimiter, 0, array.length);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final int[] array, final String delimiter, final int offset) {
    return array == null ? null : toString(array, delimiter, offset, array.length - offset);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @param length The number of array elements to be included.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final int[] array, final String delimiter, final int offset, final int length) {
    if (array == null)
      return null;

    if (array.length < offset)
      throw new ArrayIndexOutOfBoundsException(offset);

    if (length == 0 || array.length == offset)
      return "";

    if (array.length == offset + 1)
      return String.valueOf(array[offset]);

    final StringBuilder buffer = new StringBuilder(String.valueOf(array[offset]));
    for (int i = offset + 1; i < length + offset; ++i)
      buffer.append(delimiter).append(String.valueOf(array[i]));

    return buffer.toString();
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final long[] array, final char delimiter) {
    return array == null ? null : toString(array, delimiter, 0, array.length);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final long[] array, final char delimiter, final int offset) {
    return array == null ? null : toString(array, delimiter, offset, array.length - offset);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @param length The number of array elements to be included.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final long[] array, final char delimiter, final int offset, final int length) {
    if (array == null)
      return null;

    if (array.length <= offset)
      return "";

    if (array.length == offset + 1)
      return String.valueOf(array[offset]);

    final StringBuilder buffer = new StringBuilder(String.valueOf(array[offset]));
    for (int i = offset + 1; i < length + offset; ++i)
      buffer.append(delimiter).append(String.valueOf(array[i]));

    return buffer.toString();
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final long[] array, final String delimiter) {
    return array == null ? null : toString(array, delimiter, 0, array.length);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final long[] array, final String delimiter, final int offset) {
    return array == null ? null : toString(array, delimiter, offset, array.length - offset);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @param length The number of array elements to be included.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final long[] array, final String delimiter, final int offset, final int length) {
    if (array == null)
      return null;

    if (array.length <= offset)
      return "";

    if (array.length == offset + 1)
      return String.valueOf(array[offset]);

    final StringBuilder buffer = new StringBuilder(String.valueOf(array[offset]));
    for (int i = offset + 1; i < length + offset; ++i)
      buffer.append(delimiter).append(String.valueOf(array[i]));

    return buffer.toString();
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final Object[] array, final char delimiter) {
    return array == null ? null : toString(array, delimiter, 0, array.length);
  }

  /**
   * Create a string representation of the specified array by calling the
   * provided function on each member, delimited by the provided delimiter
   * {@code char}.
   *
   * @param <T> The component type of the specified array.
   * @param array The array.
   * @param delimiter The delimiter.
   * @param function The {@code toString} function.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   * @throws NullPointerException If the specified array is not null and the
   *           function is null.
   */
  public static <T>String toString(final T[] array, final char delimiter, final Function<T,String> function) {
    return array == null ? null : toString(array, delimiter, 0, array.length, function);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final Object[] array, final char delimiter, final int offset) {
    return array == null ? null : toString(array, delimiter, offset, array.length - offset);
  }

  /**
   * Create a string representation of the specified array by calling the
   * provided function on each member, delimited by the provided delimiter
   * {@code char}.
   *
   * @param <T> The component type of the specified array.
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @param function The {@code toString} function.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   * @throws NullPointerException If the specified array is not null and the
   *           function is null.
   */
  public static <T>String toString(final T[] array, final char delimiter, final int offset, final Function<T,String> function) {
    return array == null ? null : toString(array, delimiter, offset, array.length - offset, function);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * {@code char}.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @param length The number of array elements to be included.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final Object[] array, final char delimiter, final int offset, final int length) {
    if (array == null)
      return null;

    if (array.length <= offset)
      return "";

    if (array.length == offset + 1)
      return String.valueOf(array[offset]);

    final StringBuilder buffer = new StringBuilder(String.valueOf(array[offset]));
    for (int i = offset + 1; i < length + offset; ++i)
      buffer.append(delimiter).append(String.valueOf(array[i]));

    return buffer.toString();
  }

  /**
   * Create a string representation of the specified array by calling the
   * provided function on each member, delimited by the provided delimiter
   * {@code char}.
   *
   * @param <T> The component type of the specified array.
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @param length The number of array elements to be included.
   * @param function The {@code toString} function.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   * @throws NullPointerException If the specified array is not null and the
   *           function is null.
   */
  public static <T>String toString(final T[] array, final char delimiter, final int offset, final int length, final Function<T,String> function) {
    if (array == null)
      return null;

    if (array.length <= offset)
      return "";

    if (array.length == offset + 1)
      return function.apply(array[offset]);

    final StringBuilder buffer = new StringBuilder(function.apply(array[offset]));
    for (int i = offset + 1; i < length + offset; ++i)
      buffer.append(delimiter).append(function.apply(array[i]));

    return buffer.toString();
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final Object[] array, final String delimiter) {
    return array == null ? null : toString(array, delimiter, 0, array.length);
  }

  /**
   * Create a string representation of the specified array by calling the
   * provided function on each member, delimited by the provided delimiter
   * {@code char}.
   *
   * @param <T> The component type of the specified array.
   * @param array The array.
   * @param delimiter The delimiter.
   * @param function The {@code toString} function.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   * @throws NullPointerException If the specified array is not null and the
   *           function is null.
   */
  public static <T>String toString(final T[] array, final String delimiter, final Function<T,String> function) {
    return array == null ? null : toString(array, delimiter, 0, array.length, function);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final Object[] array, final String delimiter, final int offset) {
    return array == null ? null : toString(array, delimiter, offset, array.length - offset);
  }

  /**
   * Create a string representation of the specified array by calling the
   * provided function on each member, delimited by the provided delimiter
   * string.
   *
   * @param <T> The component type of the specified array.
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @param function The {@code toString} function.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   * @throws NullPointerException If the specified array is not null and the
   *           function is null.
   */
  public static <T>String toString(final T[] array, final String delimiter, final int offset, final Function<T,String> function) {
    return array == null ? null : toString(array, delimiter, offset, array.length - offset, function);
  }

  /**
   * Create a string representation of the specified array by calling each
   * member's {@code toString()} method, delimited by the provided delimiter
   * string.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @param length The number of array elements to be included.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   */
  public static String toString(final Object[] array, final String delimiter, final int offset, final int length) {
    if (array == null)
      return null;

    if (array.length <= offset)
      return "";

    if (array.length == offset + 1)
      return String.valueOf(array[offset]);

    final StringBuilder buffer = new StringBuilder(String.valueOf(array[offset]));
    for (int i = offset + 1; i < length + offset; ++i)
      buffer.append(delimiter).append(String.valueOf(array[i]));

    return buffer.toString();
  }

  /**
   * Create a string representation of the specified array by calling the
   * provided function on each member, delimited by the provided delimiter
   * string.
   *
   * @param <T> The component type of the specified array.
   * @param array The array.
   * @param delimiter The delimiter.
   * @param offset The starting offset in the array.
   * @param length The number of array elements to be included.
   * @param function The {@code toString} function.
   * @return The delimiter delimited {@code toString()} representation of the
   *         array, or {@code null} if the specified array is null.
   * @throws NullPointerException If the specified array is not null and the
   *           function is null.
   */
  public static <T>String toString(final T[] array, final String delimiter, final int offset, final int length, final Function<T,String> function) {
    if (array == null)
      return null;

    if (array.length <= offset)
      return "";

    if (array.length == offset + 1)
      return function.apply(array[offset]);

    final StringBuilder buffer = new StringBuilder(function.apply(array[offset]));
    for (int i = offset + 1; i < length + offset; ++i)
      buffer.append(delimiter).append(function.apply(array[i]));

    return buffer.toString();
  }

  /**
   * Fill the provided array with +1 incremental values starting at the provided
   * {@code start} value from {@code array[0]}, to {@code array[array.length - 1]}.
   *
   * @param array The target array.
   * @param start The starting value.
   * @return The provided array.
   */
  public static byte[] fillIncremental(final byte[] array, byte start) {
    for (int i = 0; i < array.length; ++i)
      array[i] = start++;

    return array;
  }

  /**
   * Fill the provided array with +1 incremental values starting at the provided
   * {@code start} value from {@code array[0]}, to {@code array[array.length - 1]}.
   *
   * @param array The target array.
   * @param start The starting value.
   * @return The provided array.
   */
  public static char[] fillIncremental(final char[] array, char start) {
    for (int i = 0; i < array.length; ++i)
      array[i] = start++;

    return array;
  }

  /**
   * Fill the provided array with +1 incremental values starting at the provided
   * {@code start} value from {@code array[0]}, to {@code array[array.length - 1]}.
   *
   * @param array The target array.
   * @param start The starting value.
   * @return The provided array.
   */
  public static short[] fillIncremental(final short[] array, short start) {
    for (int i = 0; i < array.length; ++i)
      array[i] = start++;

    return array;
  }

  /**
   * Fill the provided array with +1 incremental values starting at the provided
   * {@code start} value from {@code array[0]}, to {@code array[array.length - 1]}.
   *
   * @param array The target array.
   * @param start The starting value.
   * @return The provided array.
   */
  public static int[] fillIncremental(final int[] array, int start) {
    for (int i = 0; i < array.length; ++i)
      array[i] = start++;

    return array;
  }

  /**
   * Fill the provided array with +1 incremental values starting at the provided
   * {@code start} value from {@code array[0]}, to {@code array[array.length - 1]}.
   *
   * @param array The target array.
   * @param start The starting value.
   * @return The provided array.
   */
  public static long[] fillIncremental(final long[] array, long start) {
    for (int i = 0; i < array.length; ++i)
      array[i] = start++;

    return array;
  }

  /**
   * Fill the provided array with +1 incremental values starting at the provided
   * {@code start} value from {@code array[0]}, to {@code array[array.length - 1]}.
   *
   * @param array The target array.
   * @param start The starting value.
   * @return The provided array.
   */
  public static float[] fillIncremental(final float[] array, float start) {
    for (int i = 0; i < array.length; ++i)
      array[i] = start++;

    return array;
  }

  /**
   * Fill the provided array with +1 incremental values starting at the provided
   * {@code start} value from {@code array[0]}, to {@code array[array.length - 1]}.
   *
   * @param array The target array.
   * @param start The starting value.
   * @return The provided array.
   */
  public static double[] fillIncremental(final double[] array, double start) {
    for (int i = 0; i < array.length; ++i)
      array[i] = start++;

    return array;
  }

  /**
   * Create a new array by repeating the provided {@code value} by the
   * provided {@code length} number of times.
   *
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with {@code length} number of repeated
   *         {@code value} members.
   */
  public static boolean[] createRepeat(final boolean value, final int length) {
    final boolean[] array = new boolean[length];
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Create a new array by repeating the provided {@code value} by the
   * provided {@code length} number of times.
   *
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with {@code length} number of repeated
   *         {@code value} members.
   */
  public static byte[] createRepeat(final byte value, final int length) {
    final byte[] array = new byte[length];
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Create a new array by repeating the provided {@code value} by the
   * provided {@code length} number of times.
   *
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with {@code length} number of repeated
   *         {@code value} members.
   */
  public static char[] createRepeat(final char value, final int length) {
    final char[] array = new char[length];
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Create a new array by repeating the provided {@code value} by the
   * provided {@code length} number of times.
   *
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with {@code length} number of repeated
   *         {@code value} members.
   */
  public static double[] createRepeat(final double value, final int length) {
    final double[] array = new double[length];
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Create a new array by repeating the provided {@code value} by the
   * provided {@code length} number of times.
   *
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with {@code length} number of repeated
   *         {@code value} members.
   */
  public static float[] createRepeat(final float value, final int length) {
    final float[] array = new float[length];
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Create a new array by repeating the provided {@code value} by the
   * provided {@code length} number of times.
   *
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with {@code length} number of repeated
   *         {@code value} members.
   */
  public static int[] createRepeat(final int value, final int length) {
    final int[] array = new int[length];
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Create a new array by repeating the provided {@code value} by the
   * provided {@code length} number of times.
   *
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with {@code length} number of repeated
   *         {@code value} members.
   */
  public static long[] createRepeat(final long value, final int length) {
    final long[] array = new long[length];
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Create a new array by repeating the provided {@code value} by the
   * provided {@code length} number of times.
   *
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with {@code length} number of repeated
   *         {@code value} members.
   */
  public static short[] createRepeat(final short value, final int length) {
    final short[] array = new short[length];
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Create a new array by repeating the provided {@code value} by the
   * provided {@code length} number of times.
   *
   * @param <T> Type parameter of object.
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with {@code length} number of repeated
   *         {@code value} members.
   */
  @SuppressWarnings("unchecked")
  public static <T>T[] createRepeat(final T value, final int length) {
    final T[] array = (T[])Array.newInstance(value.getClass(), length);
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Returns an array that is the subArray of the provided array. Calling this
   * method is the equivalent of calling Arrays.subArray(array, beginIndex,
   * array.length).
   *
   * @param <T> Type parameter of object.
   * @param array The specified {@code array}.
   * @param beginIndex The index to become the start of the new array.
   * @return The subArray of the specified {@code array}.
   */
  public static <T>T[] subArray(final T[] array, final int beginIndex) {
    return subArray(array, beginIndex, array.length);
  }

  /**
   * Returns an array that is the subArray of the provided array.
   *
   * @param <T> Type parameter of object.
   * @param array The specified {@code array}.
   * @param beginIndex The index to become the start of the new array.
   * @param endIndex The index to become the end of the new array.
   * @return The subArray of the specified {@code array}.
   */
  @SuppressWarnings("unchecked")
  public static <T>T[] subArray(final T[] array, final int beginIndex, final int endIndex) {
    if (endIndex < beginIndex)
      throw new IllegalArgumentException("endIndex < beginIndex");

    final Class<?> componentType = array.getClass().getComponentType();
    final T[] subArray = (T[])Array.newInstance(componentType, endIndex - beginIndex);
    if (beginIndex == endIndex)
      return subArray;

    System.arraycopy(array, beginIndex, subArray, 0, endIndex - beginIndex);
    return subArray;
  }

  /**
   * Swaps the two specified elements in the specified array.
   */
  private static void swap(final boolean[] arr, final int i, final int j) {
    final boolean tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }

  /**
   * Swaps the two specified elements in the specified array.
   */
  private static void swap(final byte[] arr, final int i, final int j) {
    final byte tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }

  /**
   * Swaps the two specified elements in the specified array.
   */
  private static void swap(final char[] arr, final int i, final int j) {
    final char tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }

  /**
   * Swaps the two specified elements in the specified array.
   */
  private static void swap(final short[] arr, final int i, final int j) {
    final short tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }

  /**
   * Swaps the two specified elements in the specified array.
   */
  private static void swap(final int[] arr, final int i, final int j) {
    final int tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }

  /**
   * Swaps the two specified elements in the specified array.
   */
  private static void swap(final long[] arr, final int i, final int j) {
    final long tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }

  /**
   * Swaps the two specified elements in the specified array.
   */
  private static void swap(final float[] arr, final int i, final int j) {
    final float tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }

  /**
   * Swaps the two specified elements in the specified array.
   */
  private static void swap(final double[] arr, final int i, final int j) {
    final double tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }

  /**
   * Swaps the two specified elements in the specified array.
   */
  private static void swap(final Object[] arr, final int i, final int j) {
    final Object tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }

  private static Random DEFAULT_RANDOM;

  /**
   * Randomly permutes the specified array using a default source of randomness.
   * All permutations occur with approximately equal likelihood.
   * <p>
   * The hedge "approximately" is used in the foregoing description because
   * default source of randomness is only approximately an unbiased source of
   * independently chosen bits. If it were a perfect source of randomly chosen
   * bits, then the algorithm would choose permutations with perfect uniformity.
   * <p>
   * This implementation traverses the array backwards, from the last element up
   * to the second, repeatedly swapping a randomly selected element into the
   * "current position". Elements are randomly selected from the portion of the
   * array that runs from the first element to the current position, inclusive.
   * <p>
   * This method runs in linear time.
   *
   * @param array The list to be shuffled.
   */
  public static void shuffle(final boolean[] array) {
    shuffle(array, DEFAULT_RANDOM == null ? DEFAULT_RANDOM = new Random() : DEFAULT_RANDOM); // harmless race.
  }

  /**
   * Randomly permutes the specified array using a default source of randomness.
   * All permutations occur with approximately equal likelihood.
   * <p>
   * The hedge "approximately" is used in the foregoing description because
   * default source of randomness is only approximately an unbiased source of
   * independently chosen bits. If it were a perfect source of randomly chosen
   * bits, then the algorithm would choose permutations with perfect uniformity.
   * <p>
   * This implementation traverses the array backwards, from the last element up
   * to the second, repeatedly swapping a randomly selected element into the
   * "current position". Elements are randomly selected from the portion of the
   * array that runs from the first element to the current position, inclusive.
   * <p>
   * This method runs in linear time.
   *
   * @param array The list to be shuffled.
   */
  public static void shuffle(final byte[] array) {
    shuffle(array, DEFAULT_RANDOM == null ? DEFAULT_RANDOM = new Random() : DEFAULT_RANDOM); // harmless race.
  }

  /**
   * Randomly permutes the specified array using a default source of randomness.
   * All permutations occur with approximately equal likelihood.
   * <p>
   * The hedge "approximately" is used in the foregoing description because
   * default source of randomness is only approximately an unbiased source of
   * independently chosen bits. If it were a perfect source of randomly chosen
   * bits, then the algorithm would choose permutations with perfect uniformity.
   * <p>
   * This implementation traverses the array backwards, from the last element up
   * to the second, repeatedly swapping a randomly selected element into the
   * "current position". Elements are randomly selected from the portion of the
   * array that runs from the first element to the current position, inclusive.
   * <p>
   * This method runs in linear time.
   *
   * @param array The list to be shuffled.
   */
  public static void shuffle(final short[] array) {
    shuffle(array, DEFAULT_RANDOM == null ? DEFAULT_RANDOM = new Random() : DEFAULT_RANDOM); // harmless race.
  }

  /**
   * Randomly permutes the specified array using a default source of randomness.
   * All permutations occur with approximately equal likelihood.
   * <p>
   * The hedge "approximately" is used in the foregoing description because
   * default source of randomness is only approximately an unbiased source of
   * independently chosen bits. If it were a perfect source of randomly chosen
   * bits, then the algorithm would choose permutations with perfect uniformity.
   * <p>
   * This implementation traverses the array backwards, from the last element up
   * to the second, repeatedly swapping a randomly selected element into the
   * "current position". Elements are randomly selected from the portion of the
   * array that runs from the first element to the current position, inclusive.
   * <p>
   * This method runs in linear time.
   *
   * @param array The list to be shuffled.
   */
  public static void shuffle(final int[] array) {
    shuffle(array, DEFAULT_RANDOM == null ? DEFAULT_RANDOM = new Random() : DEFAULT_RANDOM); // harmless race.
  }

  /**
   * Randomly permutes the specified array using a default source of randomness.
   * All permutations occur with approximately equal likelihood.
   * <p>
   * The hedge "approximately" is used in the foregoing description because
   * default source of randomness is only approximately an unbiased source of
   * independently chosen bits. If it were a perfect source of randomly chosen
   * bits, then the algorithm would choose permutations with perfect uniformity.
   * <p>
   * This implementation traverses the array backwards, from the last element up
   * to the second, repeatedly swapping a randomly selected element into the
   * "current position". Elements are randomly selected from the portion of the
   * array that runs from the first element to the current position, inclusive.
   * <p>
   * This method runs in linear time.
   *
   * @param array The list to be shuffled.
   */
  public static void shuffle(final long[] array) {
    shuffle(array, DEFAULT_RANDOM == null ? DEFAULT_RANDOM = new Random() : DEFAULT_RANDOM); // harmless race.
  }

  /**
   * Randomly permutes the specified array using a default source of randomness.
   * All permutations occur with approximately equal likelihood.
   * <p>
   * The hedge "approximately" is used in the foregoing description because
   * default source of randomness is only approximately an unbiased source of
   * independently chosen bits. If it were a perfect source of randomly chosen
   * bits, then the algorithm would choose permutations with perfect uniformity.
   * <p>
   * This implementation traverses the array backwards, from the last element up
   * to the second, repeatedly swapping a randomly selected element into the
   * "current position". Elements are randomly selected from the portion of the
   * array that runs from the first element to the current position, inclusive.
   * <p>
   * This method runs in linear time.
   *
   * @param array The list to be shuffled.
   */
  public static void shuffle(final float[] array) {
    shuffle(array, DEFAULT_RANDOM == null ? DEFAULT_RANDOM = new Random() : DEFAULT_RANDOM); // harmless race.
  }

  /**
   * Randomly permutes the specified array using a default source of randomness.
   * All permutations occur with approximately equal likelihood.
   * <p>
   * The hedge "approximately" is used in the foregoing description because
   * default source of randomness is only approximately an unbiased source of
   * independently chosen bits. If it were a perfect source of randomly chosen
   * bits, then the algorithm would choose permutations with perfect uniformity.
   * <p>
   * This implementation traverses the array backwards, from the last element up
   * to the second, repeatedly swapping a randomly selected element into the
   * "current position". Elements are randomly selected from the portion of the
   * array that runs from the first element to the current position, inclusive.
   * <p>
   * This method runs in linear time.
   *
   * @param array The list to be shuffled.
   */
  public static void shuffle(final double[] array) {
    shuffle(array, DEFAULT_RANDOM == null ? DEFAULT_RANDOM = new Random() : DEFAULT_RANDOM); // harmless race.
  }

  /**
   * Randomly permutes the specified array using a default source of randomness.
   * All permutations occur with approximately equal likelihood.
   * <p>
   * The hedge "approximately" is used in the foregoing description because
   * default source of randomness is only approximately an unbiased source of
   * independently chosen bits. If it were a perfect source of randomly chosen
   * bits, then the algorithm would choose permutations with perfect uniformity.
   * <p>
   * This implementation traverses the array backwards, from the last element up
   * to the second, repeatedly swapping a randomly selected element into the
   * "current position". Elements are randomly selected from the portion of the
   * array that runs from the first element to the current position, inclusive.
   * <p>
   * This method runs in linear time.
   *
   * @param array The list to be shuffled.
   */
  public static void shuffle(final Object[] array) {
    shuffle(array, DEFAULT_RANDOM == null ? DEFAULT_RANDOM = new Random() : DEFAULT_RANDOM); // harmless race.
  }

  /**
   * Randomly permute the specified array using the specified source of
   * randomness. All permutations occur with equal likelihood assuming that the
   * source of randomness is fair.
   * <p>
   * This implementation traverses the array backwards, from the last element up
   * to the second, repeatedly swapping a randomly selected element into the
   * "current position". Elements are randomly selected from the portion of the
   * array that runs from the first element to the current position, inclusive.
   * <p>
   * This method runs in linear time.
   *
   * @param array The array to be shuffled.
   * @param random The source of randomness to use to shuffle the array.
   */
  public static void shuffle(final boolean[] array, final Random random) {
    for (int i = array.length; i > 1; --i)
      swap(array, i - 1, random.nextInt(i));
  }

  /**
   * Randomly permute the specified array using the specified source of
   * randomness. All permutations occur with equal likelihood assuming that the
   * source of randomness is fair.
   * <p>
   * This implementation traverses the array backwards, from the last element up
   * to the second, repeatedly swapping a randomly selected element into the
   * "current position". Elements are randomly selected from the portion of the
   * array that runs from the first element to the current position, inclusive.
   * <p>
   * This method runs in linear time.
   *
   * @param array the array to be shuffled.
   * @param random The source of randomness to use to shuffle the array.
   */
  public static void shuffle(final byte[] array, final Random random) {
    for (int i = array.length; i > 1; --i)
      swap(array, i - 1, random.nextInt(i));
  }

  /**
   * Randomly permute the specified array using the specified source of
   * randomness. All permutations occur with equal likelihood assuming that the
   * source of randomness is fair.
   * <p>
   * This implementation traverses the array backwards, from the last element up
   * to the second, repeatedly swapping a randomly selected element into the
   * "current position". Elements are randomly selected from the portion of the
   * array that runs from the first element to the current position, inclusive.
   * <p>
   * This method runs in linear time.
   *
   * @param array The array to be shuffled.
   * @param random The source of randomness to use to shuffle the array.
   */
  public static void shuffle(final char[] array, final Random random) {
    for (int i = array.length; i > 1; --i)
      swap(array, i - 1, random.nextInt(i));
  }

  /**
   * Randomly permute the specified array using the specified source of
   * randomness. All permutations occur with equal likelihood assuming that the
   * source of randomness is fair.
   * <p>
   * This implementation traverses the array backwards, from the last element up
   * to the second, repeatedly swapping a randomly selected element into the
   * "current position". Elements are randomly selected from the portion of the
   * array that runs from the first element to the current position, inclusive.
   * <p>
   * This method runs in linear time.
   *
   * @param array The array to be shuffled.
   * @param random The source of randomness to use to shuffle the array.
   */
  public static void shuffle(final short[] array, final Random random) {
    for (int i = array.length; i > 1; --i)
      swap(array, i - 1, random.nextInt(i));
  }

  /**
   * Randomly permute the specified array using the specified source of
   * randomness. All permutations occur with equal likelihood assuming that the
   * source of randomness is fair.
   * <p>
   * This implementation traverses the array backwards, from the last element up
   * to the second, repeatedly swapping a randomly selected element into the
   * "current position". Elements are randomly selected from the portion of the
   * array that runs from the first element to the current position, inclusive.
   * <p>
   * This method runs in linear time.
   *
   * @param array The array to be shuffled.
   * @param random The source of randomness to use to shuffle the array.
   */
  public static void shuffle(final int[] array, final Random random) {
    for (int i = array.length; i > 1; --i)
      swap(array, i - 1, random.nextInt(i));
  }

  /**
   * Randomly permute the specified array using the specified source of
   * randomness. All permutations occur with equal likelihood assuming that the
   * source of randomness is fair.
   * <p>
   * This implementation traverses the array backwards, from the last element up
   * to the second, repeatedly swapping a randomly selected element into the
   * "current position". Elements are randomly selected from the portion of the
   * array that runs from the first element to the current position, inclusive.
   * <p>
   * This method runs in linear time.
   *
   * @param array The array to be shuffled.
   * @param random The source of randomness to use to shuffle the array.
   */
  public static void shuffle(final long[] array, final Random random) {
    for (int i = array.length; i > 1; --i)
      swap(array, i - 1, random.nextInt(i));
  }

  /**
   * Randomly permute the specified array using the specified source of
   * randomness. All permutations occur with equal likelihood assuming that the
   * source of randomness is fair.
   * <p>
   * This implementation traverses the array backwards, from the last element up
   * to the second, repeatedly swapping a randomly selected element into the
   * "current position". Elements are randomly selected from the portion of the
   * array that runs from the first element to the current position, inclusive.
   * <p>
   * This method runs in linear time.
   *
   * @param array The array to be shuffled.
   * @param random The source of randomness to use to shuffle the array.
   */
  public static void shuffle(final float[] array, final Random random) {
    for (int i = array.length; i > 1; --i)
      swap(array, i - 1, random.nextInt(i));
  }

  /**
   * Randomly permute the specified array using the specified source of
   * randomness. All permutations occur with equal likelihood assuming that the
   * source of randomness is fair.
   * <p>
   * This implementation traverses the array backwards, from the last element up
   * to the second, repeatedly swapping a randomly selected element into the
   * "current position". Elements are randomly selected from the portion of the
   * array that runs from the first element to the current position, inclusive.
   * <p>
   * This method runs in linear time.
   *
   * @param array The array to be shuffled.
   * @param random The source of randomness to use to shuffle the array.
   */
  public static void shuffle(final double[] array, final Random random) {
    for (int i = array.length; i > 1; --i)
      swap(array, i - 1, random.nextInt(i));
  }

  /**
   * Randomly permute the specified array using the specified source of
   * randomness. All permutations occur with equal likelihood assuming that the
   * source of randomness is fair.
   * <p>
   * This implementation traverses the array backwards, from the last element up
   * to the second, repeatedly swapping a randomly selected element into the
   * "current position". Elements are randomly selected from the portion of the
   * array that runs from the first element to the current position, inclusive.
   * <p>
   * This method runs in linear time.
   *
   * @param array The array to be shuffled.
   * @param random The source of randomness to use to shuffle the array.
   */
  public static void shuffle(final Object[] array, final Random random) {
    for (int i = array.length; i > 1; --i)
      swap(array, i - 1, random.nextInt(i));
  }

  private FastArrays() {
  }
}