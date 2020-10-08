// adapted by jph
package ch.ethz.idsc.tensor.qty;

import java.util.LinkedHashMap;
import java.util.Map;

/** least recently used elements are given storage priority in the map of bounded size.
 * 
 * Remark: implementation is not thread safe!
 * For multi-threaded access wrap lru cache into synchronized structure */
/* package */ class LruCache<K, V> extends LinkedHashMap<K, V> {
  private final int maxSize;

  /** @param maxSize
   * @param loadFactor */
  public LruCache(int maxSize, float loadFactor) {
    super(Math.multiplyExact(maxSize, 4) / 3, loadFactor, true);
    this.maxSize = maxSize;
  }

  /** @param maxSize */
  public LruCache(int maxSize) {
    this(maxSize, 0.75f);
  }

  @Override // from LinkedHashMap
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    return maxSize < size();
  }
}
