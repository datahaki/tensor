// adapted by jph
package ch.alpine.tensor.ext;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/** least recently used elements are given storage priority in the map of bounded size.
 * 
 * key == null is not permitted
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

  @Override // from LinkedHashMap
  public V get(Object key) {
    return super.get(Objects.requireNonNull(key));
  }

  @Override // from HashMap
  public V put(K key, V value) {
    return super.put(Objects.requireNonNull(key), value);
  }

  @Override // from HashMap
  public void putAll(Map<? extends K, ? extends V> map) {
    if (map.keySet().stream().anyMatch(Objects::isNull))
      throw new IllegalArgumentException();
    super.putAll(map);
  }

  @Override // from HashMap
  public V putIfAbsent(K key, V value) {
    return super.putIfAbsent(Objects.requireNonNull(key), value);
  }

  @Override // from HashMap
  public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    return super.compute(Objects.requireNonNull(key), remappingFunction);
  }

  @Override // from HashMap
  public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    return super.computeIfPresent(Objects.requireNonNull(key), remappingFunction);
  }

  @Override // from HashMap
  public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    return super.computeIfAbsent(Objects.requireNonNull(key), mappingFunction);
  }
}
