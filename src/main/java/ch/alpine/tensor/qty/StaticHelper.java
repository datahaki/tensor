// code by jph
package ch.alpine.tensor.qty;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.sca.pow.Power;

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
    return scalar instanceof Quantity quantity //
        ? Quantity.of(quantity.value(), quantity.unit().add(base))
        : Quantity.of(scalar, base);
  }

  /** @param unitSystem
   * @param prev a base unit of the given unitSystem
   * @param next not a base unit of the given unitSystem, unless next equals prev
   * @return */
  public static Scalar conversion(UnitSystem unitSystem, String prev, String next) {
    Scalar factor = unitSystem.map().get(next);
    Unit unit = Unit.of(next);
    if (Objects.isNull(factor) && //
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

  public static Map<String, Scalar> stringScalarMap(Properties properties) {
    return properties.stringPropertyNames().stream().collect(Collectors.toMap( //
        Function.identity(), // example: "kW"
        key -> Scalars.fromString(properties.getProperty(key)))); // example: 1000[m^2*kg*s^-3]
  }

  /** @param collection
   * @return base units, for instance the set [m, A, s, kg, cd, K, mol] */
  public static Set<String> base(Collection<Scalar> collection) {
    return collection.stream() //
        .map(QuantityUnit::of) //
        .map(Unit::map) //
        .map(Map::keySet) //
        .flatMap(Collection::stream) //
        .collect(Collectors.toSet());
  }
}
