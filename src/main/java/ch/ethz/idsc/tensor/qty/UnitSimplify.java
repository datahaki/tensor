// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;

/** The current implementation of UnitSimplify checks whether a target unit
 * exists that is compatible to the unit of a given scalar.
 * 
 * Example:
 * if "km*h^-1" is contained in the set of target units, then any {@link Quantity} with
 * compatible unit, for instance "m*s^-1" will be converted to "km*h^-1".
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/UnitSimplify.html">UnitSimplify</a>
 * 
 * @see UnitConvert
 * @see UnitDimensions */
public class UnitSimplify implements ScalarUnaryOperator {
  private static final long serialVersionUID = 6850802412643722064L;

  /** @param unitSystem
   * @param set of target units for example, consisting of the units "kW", "kW*h^-1", and "N*m", etc.
   * @return
   * @throws Exception if any two units in given set are compatible units in the given
   * unit system according to {@link CompatibleUnitQ} */
  public static ScalarUnaryOperator of(UnitSystem unitSystem, Set<Unit> set) {
    return new UnitSimplify(unitSystem, set);
  }

  /** @param unitSystem
   * @param set of target units for example, consisting of the strings "kW", "kW*h^-1", and "N*m", etc.
   * @return
   * @throws Exception if any string in given set cannot be converted with {@link Unit#of(String)} */
  public static ScalarUnaryOperator from(UnitSystem unitSystem, Set<String> set) {
    return new UnitSimplify(unitSystem, set.stream().map(Unit::of).collect(Collectors.toSet()));
  }

  /***************************************************/
  private final UnitSystem unitSystem;
  private final Map<Unit, ScalarUnaryOperator> map = new HashMap<>();
  private final UnitDimensions unitDimensions;

  private UnitSimplify(UnitSystem unitSystem, Set<Unit> set) {
    this.unitSystem = unitSystem;
    unitDimensions = new UnitDimensions(unitSystem);
    UnitConvert unitConvert = UnitConvert.of(unitSystem);
    for (Unit target : set) { // N
      Unit unit = unitDimensions.toBase(target); // kg*m*s^-2
      if (map.containsKey(unit))
        throw new IllegalArgumentException(map.get(unit) + " ~ " + target + " duplicate");
      map.put(unit, unitConvert.to(target));
    }
  }

  @Override
  public Scalar apply(Scalar scalar) {
    Unit unit = unitDimensions.toBase(QuantityUnit.of(scalar));
    return map.containsKey(unit) //
        ? map.get(unit).apply(scalar)
        : unitSystem.apply(scalar);
  }
}
