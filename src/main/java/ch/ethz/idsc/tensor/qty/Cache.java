// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import ch.ethz.idsc.tensor.alg.Binomial;

/** caches results of costly computations for fast lookup
 * the function {@link #retrieve(Object)} is thread safe
 * the cache does not exceed given maxSize
 * 
 * @see Unit
 * @see Binomial */
public class Cache<K, V> {
  private final Function<K, V> function;
  private final Map<K, V> map;

  /** @param function
   * @param maxSize */
  public Cache(Function<K, V> function, int maxSize) {
    this.function = function;
    map = new LruCache<>(maxSize);
  }

  /** @param key
   * @return the result of applying function to given key */
  public V retrieve(K key) {
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
