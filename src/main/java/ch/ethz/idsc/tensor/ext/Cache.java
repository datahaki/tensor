// code by jph
package ch.ethz.idsc.tensor.ext;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/** cache results of costly computations for fast lookup
 * the function apply is thread safe
 * the cache does not exceed given maxSize
 * 
 * Remark: the function should map a key to the same value
 * if initially f[1] = "abc", and later f[1] = "def" then
 * the function is not suitable for caching!
 * 
 * Remark: the values should be immutable to ensure that the
 * receiver cannot modify the content that may be queried by
 * the next caller.
 * 
 * Used in: Unit, CirclePoints, Binomial */
public class Cache<K, V> implements Function<K, V>, Serializable {
  private static final long serialVersionUID = -2414569340981942360L;

  /** @param function
   * @param maxSize
   * @return */
  public static <K, V> Function<K, V> of(Function<K, V> function, int maxSize) {
    if (function instanceof Cache)
      throw new IllegalArgumentException();
    return new Cache<>(Objects.requireNonNull(function), maxSize);
  }

  /***************************************************/
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
    V unit = map.get(key);
    if (Objects.isNull(unit)) {
      unit = function.apply(key);
      synchronized (map) {
        map.put(key, unit);
      }
    }
    return unit;
  }

  /** @return number of elements currently stored in cache */
  public int size() {
    return map.size();
  }
}
