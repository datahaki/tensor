// code by jph
package ch.ethz.idsc.tensor.qty;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ext.Cache;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/UnitDimensions.html">UnitDimensions</a> */
public class UnitDimensions implements Serializable {
  private static final int MAX_SIZE = 768;
  // TODO obsolete, manage in unitSystem!
  @SuppressWarnings("unchecked")
  private final Cache<Unit, Unit> cache = //
      Cache.of((Function<Unit, Unit> & Serializable) this::base, MAX_SIZE);
  private final Map<String, Scalar> map;

  public UnitDimensions(UnitSystem unitSystem) {
    map = unitSystem.map();
  }

  /** Example: "kW*h" -> "kg*m^2*s^-2"
   * 
   * @param unit
   * @return */
  public Unit toBase(Unit unit) {
    return cache.apply(unit);
  }

  private Unit base(Unit unit) {
    return Factor.of(map, unit).getUnit(unit);
  }
}
