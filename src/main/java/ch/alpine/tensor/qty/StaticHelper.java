// code by jph
package ch.alpine.tensor.qty;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Power;

/* package */ enum StaticHelper {
  ;
  private static String exponentString(Scalar exponent) {
    String string = exponent.toString();
    return string.equals("1") //
        ? ""
        : Unit.POWER_DELIMITER + string; // delimited by '^'
  }

  /** @param map
   * @return for instance "m*s^-2" */
  public static String toString(Map<String, Scalar> map) {
    return map.entrySet().stream() //
        .map(entry -> entry.getKey() + exponentString(entry.getValue())) //
        .collect(Collectors.joining(Unit.JOIN_DELIMITER)); // delimited by '*'
  }

  /** @param scalar
   * @param base
   * @return product of scalar and 1[base] where the multiplicative 1 is not used explicitly */
  public static Scalar multiply(Scalar scalar, Unit base) {
    if (scalar instanceof Quantity) {
      Quantity quantity = (Quantity) scalar;
      return Quantity.of(quantity.value(), quantity.unit().add(base));
    }
    return Quantity.of(scalar, base);
  }

  /** @param unitSystem
   * @param prev a base unit of the given unitSystem
   * @param next not a base unit of the given unitSystem, unless next equals prev
   * @return */
  public static Scalar conversion(UnitSystem unitSystem, String prev, String next) {
    Scalar factor = unitSystem.map().get(next);
    Unit unit = Unit.of(next);
    if (Objects.isNull(factor) && //
    // TODO KnownUnitQ.in(unitSystem) rebuilds a map every time: avoid?
        KnownUnitQ.in(unitSystem).require(unit).equals(Unit.of(prev)))
      return RealScalar.ONE;
    Unit rhs = QuantityUnit.of(factor);
    for (Entry<String, Scalar> entry : rhs.map().entrySet())
      if (!entry.getKey().equals(prev))
        unit = unit.add(Unit.of(entry.getKey()).multiply(entry.getValue()).negate());
    return Power.of(Quantity.of( //
        QuantityMagnitude.singleton(rhs).apply(factor).reciprocal(), //
        unit), //
        rhs.map().get(prev).reciprocal());
  }

  /** Example: for the SI unit system, the set of known atomic units contains
   * "m", "K", "W", "kW", "s", "Hz", ...
   * 
   * @return set of all atomic units known by the unit system including those that
   * are not further convertible */
  public static Set<String> buildSet(UnitSystem unitSystem) {
    Set<String> set = new HashSet<>();
    for (Entry<String, Scalar> entry : unitSystem.map().entrySet()) {
      set.add(entry.getKey());
      set.addAll(QuantityUnit.of(entry.getValue()).map().keySet());
    }
    return set;
  }
}
