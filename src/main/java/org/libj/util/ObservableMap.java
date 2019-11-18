/* Copyright (c) 2016 LibJ
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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link DelegateMap} that provides callback methods to observe the addition
 * and removal of elements, either due to direct method invocation on the map
 * instance itself, or via {@link #entrySet()}, {@link #values()},
 * {@link #forEach(java.util.function.BiConsumer)}, and any other entrypoint
 * that facilitates modification of the elements in this map.
 *
 * @param <K> The type of keys maintained by this map.
 * @param <V> The type of mapped values.
 * @see #beforePut(Object,Object,Object)
 * @see #afterPut(Object,Object,Object,RuntimeException)
 * @see #beforeRemove(Object,Object)
 * @see #afterRemove(Object,Object,RuntimeException)
 */
public abstract class ObservableMap<K,V> extends DelegateMap<K,V> {
  /**
   * Creates a new {@link ObservableList} with the specified target
   * {@link Map}.
   *
   * @param map The target {@link Map}.
   * @throws NullPointerException If {@code map} is null.
   */
  public ObservableMap(final Map<K,V> map) {
    super(map);
  }

  /**
   * Callback method that is invoked immediately before an entry is put into the
   * enclosed {@link Map}.
   *
   * @param key The key of the entry being added to the enclosed {@link Map}.
   * @param oldValue The old value being replaced for the key in the enclosed
   *          {@link Map}, or null if there was no existing value for the key.
   * @param newValue The new value being put for the key in the enclosed
   *          {@link Map}.
   * @return If this method returns {@code false}, the subsequent put operation
   *         will not be performed; otherwise, the subsequent put
   *         operation will be performed.
   */
  protected boolean beforePut(final K key, final V oldValue, final V newValue) {
    return true;
  }

  /**
   * Callback method that is invoked immediately after an entry is put into the
   * enclosed {@link Map}.
   *
   * @param key The key of the entry being added to the enclosed {@link Map}.
   * @param oldValue The old value being replaced for the key in the enclosed
   *          {@link Map}, or null if there was no existing value for the key.
   * @param newValue The new value being put for the key in the enclosed
   *          {@link Map}.
   * @param re A {@link RuntimeException} that occurred during the put
   *          operation, or null if no exception occurred.
   */
  protected void afterPut(final K key, final V oldValue, final V newValue, final RuntimeException re) {
  }

  /**
   * Callback method that is invoked immediately before an entry is removed from
   * the enclosed {@link Map}.
   *
   * @param key The key of the entry being removed from the enclosed
   *          {@link Map}.
   * @param value The value for the key being removed in the enclosed
   *          {@link Map}, or null if there was no existing value for the key.
   * @return If this method returns {@code false}, the subsequent remove
   *         operation will not be performed; otherwise, the subsequent
   *         remove operation will be performed.
   */
  protected boolean beforeRemove(final Object key, final V value) {
    return true;
  }

  /**
   * Callback method that is invoked immediately after an entry is removed from
   * the enclosed {@link Map}.
   *
   * @param key The key of the entry being removed from the enclosed
   *          {@link Map}.
   * @param value The value for the key being removed in the enclosed
   *          {@link Map}, or null if there was no existing value for the key.
   * @param re A {@link RuntimeException} that occurred during the remove
   *          operation, or null if no exception occurred.
   */
  protected void afterRemove(final Object key, final V value, final RuntimeException re) {
  }

  /**
   * {@inheritDoc}
   * <p>
   * The callback methods {@link #beforePut(Object,Object,Object)}
   * and {@link #afterPut(Object,Object,Object,RuntimeException)} are called
   * immediately before and after the enclosed collection is modified.
   */
  @Override
  @SuppressWarnings("unchecked")
  public V put(final K key, final V value) {
    final V oldValue = (V)target.get(key);
    if (!beforePut(key, oldValue, value))
      return oldValue;

    RuntimeException re = null;
    try {
      target.put(key, value);
    }
    catch (final RuntimeException t) {
      re = t;
    }

    afterPut(key, oldValue, value, re);
    if (re != null)
      throw re;

    return oldValue;
  }

  /**
   * {@inheritDoc}
   * <p>
   * The callback methods {@link #beforePut(Object,Object,Object)} and
   * {@link #afterPut(Object,Object,Object,RuntimeException)} are called
   * immediately before and after the enclosed collection is modified.
   */
  @Override
  public V putIfAbsent(final K key, final V value) {
    final V previous = get(key);
    if (previous == null)
      put(key, value);

    return previous;
  }

  /**
   * {@inheritDoc}
   * <p>
   * The callback methods {@link #beforePut(Object,Object,Object)}
   * and {@link #afterPut(Object,Object,Object,RuntimeException)} are called
   * immediately before and after the enclosed collection is modified for the
   * addition of each entry in the argument map.
   */
  @Override
  public void putAll(final Map<? extends K,? extends V> m) {
    for (final Map.Entry<? extends K,? extends V> entry : m.entrySet())
      put(entry.getKey(), entry.getValue());
  }

  /**
   * {@inheritDoc}
   * <p>
   * The callback methods {@link #beforeRemove(Object,Object)} and
   * {@link #afterRemove(Object,Object,RuntimeException)} are called immediately
   * before and after the enclosed collection is modified.
   */
  @Override
  @SuppressWarnings("unchecked")
  public V remove(final Object key) {
    final V value = (V)target.get(key);
    if (!beforeRemove(key, value))
      return value;

    RuntimeException re = null;
    try {
      target.remove(key);
    }
    catch (final RuntimeException t) {
      re = t;
    }

    afterRemove(key, value, re);
    if (re != null)
      throw re;

    return value;
  }

  /**
   * {@inheritDoc}
   * <p>
   * The callback methods {@link #beforePut(Object,Object,Object)} and
   * {@link #afterPut(Object,Object,Object,RuntimeException)} are called
   * immediately before and after the enclosed collection is modified.
   */
  @Override
  public boolean replace(final K key, final V oldValue, final V newValue) {
    final V previous = get(key);
    if (oldValue == null || !oldValue.equals(previous))
      return false;

    put(key, newValue);
    return true;
  }

  /**
   * {@inheritDoc}
   * <p>
   * The callback methods {@link #beforePut(Object,Object,Object)} and
   * {@link #afterPut(Object,Object,Object,RuntimeException)} are called
   * immediately before and after the enclosed collection is modified.
   */
  @Override
  public V replace(final K key, final V value) {
    final V previous = get(key);
    if (previous == null)
      return null;

    put(key, value);
    return previous;
  }

  /**
   * {@inheritDoc}
   * <p>
   * The callback methods {@link #beforeRemove(Object,Object)} and
   * {@link #afterRemove(Object,Object,RuntimeException)} are called immediately
   * before and after the enclosed collection is modified for the removal of
   * each entry removed.
   */
  @Override
  public void clear() {
    final Iterator<K> iterator = keySet().iterator();
    while (iterator.hasNext()) {
      iterator.next();
      iterator.remove();
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * The callback methods {@link #beforeRemove(Object,Object)} and
   * {@link #afterRemove(Object,Object,RuntimeException)} are called immediately
   * before and after the an entry is removed in the enclosed collection. The
   * callback methods {@link #beforePut(Object,Object,Object)} and
   * {@link #afterPut(Object,Object,Object,RuntimeException)} are called
   * immediately before and after the an entry is put in the enclosed
   * collection.
   */
  @Override
  @SuppressWarnings("unchecked")
  public V compute(final K key, final BiFunction<? super K,? super V,? extends V> remappingFunction) {
    return (V)target.compute(key, (BiFunction<K,V,V>)(t, u) -> {
      final V value = remappingFunction.apply(t, u);
      if (value == null)
        remove(t);
      else
        put(t, value);

      return value;
    });
  }

  /**
   * {@inheritDoc}
   * <p>
   * The callback methods {@link #beforePut(Object,Object,Object)} and
   * {@link #afterPut(Object,Object,Object,RuntimeException)} are called
   * immediately before and after the an entry is put in the enclosed
   * collection.
   */
  @Override
  @SuppressWarnings("unchecked")
  public V computeIfAbsent(final K key, final Function<? super K,? extends V> mappingFunction) {
    return (V)target.computeIfAbsent(key, (Function<K,V>)t -> {
      final V value = mappingFunction.apply(t);
      put(t, value);
      return value;
    });
  }

  /**
   * {@inheritDoc}
   * <p>
   * The callback methods {@link #beforeRemove(Object,Object)} and
   * {@link #afterRemove(Object,Object,RuntimeException)} are called immediately
   * before and after the an entry is removed in the enclosed collection. The
   * callback methods {@link #beforePut(Object,Object,Object)} and
   * {@link #afterPut(Object,Object,Object,RuntimeException)} are called
   * immediately before and after the an entry is put in the enclosed
   * collection.
   */
  @Override
  @SuppressWarnings("unchecked")
  public V computeIfPresent(final K key, final BiFunction<? super K,? super V,? extends V> remappingFunction) {
    return (V)target.computeIfPresent(key, (BiFunction<K,V,V>)(t, u) -> {
      final V value = remappingFunction.apply(t, u);
      if (value == null)
        remove(t);
      else
        put(t, value);

      return value;
    });
  }

  /**
   * {@inheritDoc}
   * <p>
   * The callback methods {@link #beforeRemove(Object,Object)} and
   * {@link #afterRemove(Object,Object,RuntimeException)} are called immediately
   * before and after the an entry is removed in the enclosed collection. The
   * callback methods {@link #beforePut(Object,Object,Object)} and
   * {@link #afterPut(Object,Object,Object,RuntimeException)} are called
   * immediately before and after the an entry is put in the enclosed
   * collection.
   */
  @Override
  @SuppressWarnings({"unchecked", "unlikely-arg-type"})
  public V merge(final K key, final V value, final BiFunction<? super V,? super V,? extends V> remappingFunction) {
    return (V)(target.get(key) == null ? target.put(key, value) : target.merge(key, value, (BiFunction<V,V,V>)(t, u) -> {
      final V value1 = remappingFunction.apply(t, u);
      if (value1 == null)
        remove(t);
      else
        put(key, value1);

      return value1;
    }));
  }

  /**
   * {@inheritDoc}
   * <p>
   * The callback methods {@link #beforePut(Object,Object,Object)} and
   * {@link #afterPut(Object,Object,Object,RuntimeException)} are called
   * immediately before and after the an entry is put in the enclosed
   * collection.
   */
  @Override
  public void replaceAll(final BiFunction<? super K,? super V,? extends V> function) {
    target.replaceAll((BiFunction<K,V,V>)(t, u) -> {
      final V value = function.apply(t, u);
      put(t, value);
      return value;
    });
  }

  protected volatile ObservableSet<Map.Entry<K,V>> entrySet;

  @Override
  public Set<Map.Entry<K,V>> entrySet() {
    return entrySet == null ? entrySet = new ObservableSet<Map.Entry<K,V>>(target.entrySet()) {
      private final ThreadLocal<K> localKey = new ThreadLocal<>();
      private final ThreadLocal<V> localOldValue = new ThreadLocal<>();
      private final ThreadLocal<V> localNewValue = new ThreadLocal<>();

      @Override
      protected boolean beforeAdd(final Entry<K,V> e) {
        localKey.set(e.getKey());
        localNewValue.set(e.getValue());
        localOldValue.set(ObservableMap.this.get(e.getKey()));
        return ObservableMap.this.beforePut(e.getKey(), localOldValue.get(), e.getValue());
      }

      @Override
      protected void afterAdd(final Entry<K,V> e, final RuntimeException re) {
        ObservableMap.this.afterPut(localKey.get(), localOldValue.get(), localNewValue.get(), re);
      }

      @Override
      @SuppressWarnings("unchecked")
      protected boolean beforeRemove(final Object e) {
        final Map.Entry<K,V> entry = (Map.Entry<K,V>)e;
        localKey.set(entry.getKey());
        localNewValue.set(entry.getValue());
        return ObservableMap.this.beforeRemove(entry.getKey(), entry.getValue());
      }

      @Override
      protected void afterRemove(final Object e, final RuntimeException re) {
        ObservableMap.this.afterRemove(localKey.get(), localNewValue.get(), re);
      }
    } : entrySet;
  }

  protected volatile ObservableSet<K> keySet;

  @Override
  public Set<K> keySet() {
    return keySet == null ? keySet = new ObservableSet<K>(target.keySet()) {
      private final ThreadLocal<Object> localKey = new ThreadLocal<>();
      private final ThreadLocal<V> localNewValue = new ThreadLocal<>();

      @Override
      protected boolean beforeAdd(final K e) {
        throw new UnsupportedOperationException();
      }

      @Override
      @SuppressWarnings("unchecked")
      protected boolean beforeRemove(final Object e) {
        localKey.set(e);
        final V value = (V)ObservableMap.this.target.get(e);
        localNewValue.set(value);
        return ObservableMap.this.beforeRemove(e, value);
      }

      @Override
      protected void afterRemove(final Object e, final RuntimeException re) {
        ObservableMap.this.afterRemove(localKey.get(), localNewValue.get(), re);
      }
    } : keySet;
  }

  protected volatile TransCollection<Map.Entry<K,V>,V> values;

  @Override
  public Collection<V> values() {
    return values == null ? values = new TransCollection<>(entrySet(), Entry::getValue, null) : values;
  }
}