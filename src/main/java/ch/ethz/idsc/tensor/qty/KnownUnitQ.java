// code by jph
package ch.ethz.idsc.tensor.qty;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import ch.ethz.idsc.tensor.Scalar;

/** Predicate determines if unit is defined by a given unit system.
 * 
 * <p>A use case of {@link KnownUnitQ} is the validation of compatibility of user defined
 * scalar or tensor parameters with the unit system of an application.
 * 
 * <p>Use {@link QuantityUnit} to obtain {@link Unit} of a {@link Scalar}, and then
 * check containment of unit in a given {@link UnitSystem}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/KnownUnitQ.html">KnownUnitQ</a> */
public class KnownUnitQ implements Predicate<Unit>, Serializable {
  private static final long serialVersionUID = 5835184585615654231L;
  private static final KnownUnitQ SI = in(UnitSystem.SI());

  /** @param unitSystem non-null
   * @return predicate according to given unit system */
  public static KnownUnitQ in(UnitSystem unitSystem) {
    return new KnownUnitQ(all(unitSystem.map()));
  }

  /** Examples:
   * <pre>
   * KnownUnitQ.SI().of(Unit.of("V*K*CD*kOhm^-2")) == true
   * KnownUnitQ.SI().of(Unit.of("CHF")) == false
   * </pre>
   * 
   * @return predicate according to built-in unit system */
  public static KnownUnitQ SI() {
    return SI;
  }

  /** Example: in the SI unit system defined by the tensor library the units
   * "A", "cd", "kg", "m", "mol", "s"
   * are included in the set provided by units(), but are not keys in {@link #map()}
   * "K" is a key in the map with value 1[K]
   * 
   * @return set of all atomic units known by the unit system including those that
   * are not further convertible */
  private static Set<String> all(Map<String, Scalar> map) {
    Set<String> set = new HashSet<>();
    for (Entry<String, Scalar> entry : map.entrySet()) {
      set.add(entry.getKey());
      set.addAll(QuantityUnit.of(entry.getValue()).map().keySet());
    }
    return set;
  }

  /***************************************************/
  private final Set<String> set;

  private KnownUnitQ(Set<String> set) {
    this.set = set;
  }

  /** @param unit
   * @return true if all atomic units of given unit are defined in unit system */
  @Override // from Predicate
  public boolean test(Unit unit) {
    return set.containsAll(unit.map().keySet());
  }

  /** @param unit
   * @return unit
   * @throws Exception if given unit is not known to unit system */
  public Unit require(Unit unit) {
    if (test(unit))
      return unit;
    throw new IllegalArgumentException("" + unit);
  }
}
