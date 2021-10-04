// code by jph
package ch.alpine.tensor.ext;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/** cache results of costly computations for fast lookup the function apply is thread
 * safe in the sense that the cache will never exceed the given maxSize. However,
 * in a multi-thread use of the cache, the given function may be called in parallel.
 * 
 * The function should map a key to the same value if initially f[1] = "abc", and
 * later f[1] = "def" then the function is not suitable for caching!
 * 
 * The values should be immutable to ensure that the receiver cannot modify the content
 * that may be queried by the next caller. A value may also be null.
 * 
 * Used in: Unit, CirclePoints, Binomial, GaussScalar, ... */
public class Cache<K, V> implements Function<K, V>, Serializable {
  /** @param function non-null, may also return null
   * @param maxSize non-negative
   * @return */
  public static <K, V> Cache<K, V> of(Function<K, V> function, int maxSize) {
    if (function instanceof Cache)
      throw new IllegalArgumentException();
    return new Cache<>(Objects.requireNonNull(function), maxSize);
  }

  // ---
  private final Function<K, V> function;
  private final Map<K, V> map;

  private Cache(Function<K, V> function, int maxSize) {
    this.function = function;
    map = new LruCache<>(maxSize);
  }

  /** @param key
   * @return the result of applying function to given key */
  @Override
  public V apply(K key) {
    V value = map.get(key);
    if (Objects.isNull(value)) {
      value = function.apply(key);
      synchronized (map) {
        map.put(key, value);
      }
    }
    return value;
  }

  /** @return number of elements currently stored in cache */
  public int size() {
    return map.size();
  }

  /** removes all of the mappings from this cache */
  public void clear() {
    map.clear();
  }
}
