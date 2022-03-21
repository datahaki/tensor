// code by jph
package ch.alpine.tensor.qty;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.ext.MergeIllegal;

/** immutable
 * 
 * all instances of UnitImpl are managed in a {@link Cache}
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
/* package */ class UnitImpl implements Unit, Serializable {
  private static final Collector<Entry<String, Scalar>, ?, NavigableMap<String, Scalar>> NEGATION = //
      Collectors.toMap(Entry::getKey, entry -> entry.getValue().negate(), MergeIllegal.operator(), TreeMap::new);
  private static final int MAX_SIZE = 1536;
  private static final Function<NavigableMap<String, Scalar>, Unit> CACHE = //
      Cache.of(UnitImpl::new, MAX_SIZE);

  /** @param navigableMap for example {"kg"=1, "m"=1, "s"=-2}, the scalar value shall not be zero
   * @return */
  public static Unit create(NavigableMap<String, Scalar> navigableMap) {
    return CACHE.apply(navigableMap);
  }

  // ---
  private final NavigableMap<String, Scalar> navigableMap;
  private final int hashCode;
  private final String string;

  private UnitImpl(NavigableMap<String, Scalar> navigableMap) {
    this.navigableMap = navigableMap;
    hashCode = navigableMap.hashCode();
    string = StaticHelper.toString(navigableMap);
  }

  @Override // from Unit
  public Unit negate() {
    return create(navigableMap.entrySet().stream().collect(NEGATION));
  }

  @Override // from Unit
  public Unit add(Unit unit) {
    NavigableMap<String, Scalar> map = new TreeMap<>(navigableMap);
    for (Entry<String, Scalar> entry : unit.map().entrySet())
      UnitParser.merge(map, entry.getKey(), entry.getValue()); // exponent is guaranteed to be non-zero
    return create(map);
  }

  @Override // from Unit
  public Unit multiply(Scalar scalar) {
    if (scalar instanceof Quantity)
      throw TensorRuntimeException.of(scalar);
    NavigableMap<String, Scalar> map = new TreeMap<>();
    for (Entry<String, Scalar> entry : navigableMap.entrySet()) {
      Scalar value = entry.getValue().multiply(scalar);
      if (Scalars.nonZero(value))
        map.put(entry.getKey(), value);
    }
    return create(map);
  }

  @Override // from Unit
  public NavigableMap<String, Scalar> map() {
    return Collections.unmodifiableNavigableMap(navigableMap);
  }

  // ---
  @Override // from Object
  public int hashCode() {
    return hashCode;
  }

  @Override // from Object
  public boolean equals(Object object) {
    return object instanceof Unit //
        && navigableMap.equals(((Unit) object).map());
  }

  @Override // from Object
  public String toString() {
    return string;
  }
}
