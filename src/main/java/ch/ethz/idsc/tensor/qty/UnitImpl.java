// code by jph
package ch.ethz.idsc.tensor.qty;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.ext.Cache;

/** immutable
 * 
 * all instances of UnitImpl are managed in a LRU cache */
/* package */ class UnitImpl implements Unit, Serializable {
  private static final long serialVersionUID = -2807221907647012658L;
  /* package */ static final Collector<Entry<String, Scalar>, ?, NavigableMap<String, Scalar>> NEGATION = //
      Collectors.toMap(Entry::getKey, entry -> entry.getValue().negate(), (e1, e2) -> null, TreeMap::new);
  private static final int MAX_SIZE = 1536;
  private static final Function<NavigableMap<String, Scalar>, Unit> CACHE = //
      Cache.of(UnitImpl::new, MAX_SIZE);

  /** @param navigableMap for example {"kg"=1, "m"=1, "s"=-2}, the scalar value shall not be zero
   * @return */
  public static Unit create(NavigableMap<String, Scalar> navigableMap) {
    return CACHE.apply(navigableMap);
  }

  /***************************************************/
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
      StaticHelper.merge(map, entry.getKey(), entry.getValue()); // exponent is guaranteed to be non-zero
    return create(map);
  }

  @Override // from Unit
  public Unit multiply(Scalar factor) {
    if (factor instanceof Quantity)
      throw TensorRuntimeException.of(factor);
    NavigableMap<String, Scalar> map = new TreeMap<>();
    for (Entry<String, Scalar> entry : navigableMap.entrySet()) {
      Scalar value = entry.getValue().multiply(factor);
      if (Scalars.nonZero(value))
        map.put(entry.getKey(), value);
    }
    return create(map);
  }

  @Override // from Unit
  public NavigableMap<String, Scalar> map() {
    return Collections.unmodifiableNavigableMap(navigableMap);
  }

  /***************************************************/
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
