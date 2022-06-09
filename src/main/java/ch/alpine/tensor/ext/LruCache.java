// adapted by jph
package ch.alpine.tensor.ext;

import java.util.LinkedHashMap;
import java.util.Map;

/** least recently used elements are given storage priority in the map of bounded size.
 * 
 * Remark: implementation is not thread safe!
 * For multi-threaded access wrap lru cache into synchronized structure, for example
 * <pre>
 * Map<K, V> map = Collections.synchronizedMap(new LruCache<>(size));
 * </pre> */
public class LruCache<K, V> extends LinkedHashMap<K, V> {
  private final int maxSize;

  /** @param maxSize non-negative
   * @param loadFactor positive */
  public LruCache(int maxSize, float loadFactor) {
    super(Math.multiplyExact(maxSize, 4) / 3, loadFactor, true);
    this.maxSize = maxSize;
  }

  /** @param maxSize non-negative */
  public LruCache(int maxSize) {
    this(maxSize, 0.75f);
  }

  @Override // from LinkedHashMap
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    return maxSize < size();
  }
}
