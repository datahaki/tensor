// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.Power;

/* package */ enum StaticHelper {
  ;
  /** atomic unit may consist of roman letters in lower case a-z,
   * upper case A-Z, as well as the underscore character '_', and
   * the percent character `%` */
  private static final Pattern PATTERN = Pattern.compile("[%A-Z_a-z]+");

  /** @param key atomic unit expression, for instance "kg"
   * @return given key
   * @throws Exception if given key is not an atomic unit expression */
  public static String requireAtomic(String key) {
    if (PATTERN.matcher(key).matches())
      return key;
    throw new IllegalArgumentException(key);
  }

  /** @param map
   * @param key
   * @param exponent non-zero */
  /* package */ static void merge(NavigableMap<String, Scalar> map, String key, Scalar exponent) {
    if (map.containsKey(key)) {
      Scalar sum = map.get(key).add(exponent);
      if (Scalars.isZero(sum))
        map.remove(key); // exponents cancel out
      else
        map.put(key, sum); // exponent is updated
    } else
      map.put(key, exponent); // unit is introduced
  }

  /** @param unitSystem
   * @param prev a base unit of the given unitSystem
   * @param next not a base unit of the given unitSystem, unless next equals prev
   * @return */
  public static Scalar conversion(UnitSystem unitSystem, String prev, String next) {
    Scalar factor = unitSystem.map().get(next);
    Unit unit = Unit.of(next);
    if (Objects.isNull(factor) && //
    // LONGTERM KnownUnitQ.in(unitSystem) rebuilds a map every time: avoid?
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

  // only used in tests
  /* package */ static Set<Unit> atoms(Unit unit) {
    return unit.map().entrySet().stream() //
        .map(SimpleUnitSystem::format) //
        .collect(Collectors.toSet());
  }
}
