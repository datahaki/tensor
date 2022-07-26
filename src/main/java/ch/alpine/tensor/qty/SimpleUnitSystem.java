// code by jph
package ch.alpine.tensor.qty;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Properties;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.sca.pow.Power;

/** reference implementation of {@link UnitSystem} with emphasis on simplicity
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
public class SimpleUnitSystem implements UnitSystem {
  /** given properties map a unit expression to a {@link Quantity}
   * 
   * <p>Example from the built-in file "/unit/si.properties":
   * <pre>
   * K=1[K]
   * rad=1
   * Hz=1[s^-1]
   * N=1[m*kg*s^-2]
   * Pa=1[m^-1*kg*s^-2]
   * ...
   * </pre>
   * 
   * @param properties
   * @return
   * @throws Exception if keys do not define unit conversions */
  public static UnitSystem from(Properties properties) {
    return from(StaticHelper.stringScalarMap(properties));
  }

  /** @param map
   * @return unit system */
  public static UnitSystem from(Map<String, Scalar> map) {
    return _from(requireTransitionFree(map));
  }

  /* package */ static UnitSystem _from(Map<String, Scalar> map) {
    return new SimpleUnitSystem(map.entrySet().stream().collect(Collectors.toMap( //
        entry -> UnitParser.requireAtomic(entry.getKey()), // example: "kV"
        entry -> requireNumeric(entry.getValue())))); // example: 1000[m^2*kg*s^-3*A^-1]
  }

  private static Map<String, Scalar> requireTransitionFree(Map<String, Scalar> map) {
    for (Scalar scalar : map.values())
      for (String atom : QuantityUnit.of(scalar).map().keySet()) // example: m, kg, s, A
        if (map.containsKey(atom)) {
          Scalar value = ((Quantity) scalar).value();
          Unit alt = QuantityUnit.of(map.get(atom));
          if (Scalars.isZero(value)) // non-zero
            throw new IllegalArgumentException(atom + " " + value);
          if (!value.one().equals(value)) // not multiplicative 1
            throw new IllegalArgumentException(atom + " " + value);
          if (!alt.toString().equals(atom))
            throw new IllegalArgumentException(atom + " " + value);
        }
    return map;
  }

  // helper function
  private static Scalar requireNumeric(Scalar scalar) {
    if (scalar instanceof StringScalar)
      throw new Throw(scalar);
    return scalar;
  }

  // ---
  private final Map<String, Scalar> map;
  private final Cache<Unit, Factor> cache;

  @SuppressWarnings("unchecked")
  private SimpleUnitSystem(Map<String, Scalar> map) {
    this.map = map;
    cache = Cache.of((Function<Unit, Factor> & Serializable) this::factor, 3 * map.size());
  }

  private Factor factor(Unit unit) {
    NavigableMap<String, Scalar> navigableMap = new TreeMap<>();
    Scalar product = null; // avoids to introduce a multiplicative 1
    for (Entry<String, Scalar> entry : unit.map().entrySet()) {
      Scalar lookup = map.get(entry.getKey());
      if (Objects.isNull(lookup)) // in case of base unit, e.g. "m" for SI
        navigableMap.put(entry.getKey(), entry.getValue());
      else { // in case of unit definitions, e.g. "Pa" for SI
        navigableMap.remove(entry.getKey());
        Scalar factor = Power.of(lookup, entry.getValue());
        product = Objects.isNull(product) //
            ? factor
            : product.multiply(factor);
      }
    }
    return Objects.isNull(product) //
        ? FactorIdentity.INSTANCE // when unit consist of known atomics: e.g. kg*m^2*s^-3
        : new FactorProduct(StaticHelper.multiply(product, UnitImpl.create(navigableMap)));
  }

  private interface Factor {
    /** @param quantity
     * @return */
    Scalar times(Quantity quantity);

    /** @param unit
     * @return */
    Unit dimensions(Unit unit);
  }

  private enum FactorIdentity implements Factor {
    INSTANCE;

    @Override // from Factor
    public Scalar times(Quantity quantity) {
      return quantity;
    }

    @Override // from Factor
    public Unit dimensions(Unit unit) {
      return unit;
    }
  }

  private static class FactorProduct implements Factor, Serializable {
    private final Scalar scalar;
    private final Unit base;

    /** @param scalar may or may not be of instance {@link Quantity} */
    public FactorProduct(Scalar scalar) {
      this.scalar = scalar;
      base = QuantityUnit.of(scalar);
    }

    @Override // from Factor
    public Scalar times(Quantity quantity) {
      return quantity.value().multiply(scalar);
    }

    @Override // from Factor
    public Unit dimensions(Unit unit) {
      return base;
    }
  }

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof Quantity) {
      Quantity quantity = (Quantity) scalar;
      return cache.apply(quantity.unit()).times(quantity);
    }
    return Objects.requireNonNull(scalar);
  }

  @Override // from UnitSystem
  public Map<String, Scalar> map() {
    return Collections.unmodifiableMap(map);
  }

  @Override // from UnitSystem
  public Unit dimensions(Unit unit) {
    return cache.apply(unit).dimensions(unit);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("UnitSystem", map);
  }
}
