// code by jph
package ch.alpine.tensor.qty;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;

/** Quote from Mathematica::QuantityMagnitude
 * "gives the amount of the specified quantity"
 * "gives the magnitude value of a Quantity"
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/QuantityMagnitude.html">QuantityMagnitude</a> */
public class QuantityMagnitude implements Serializable {
  private static final QuantityMagnitude SI = new QuantityMagnitude(UnitSystem.SI());
  private static final QuantityMagnitude EMPTY = //
      new QuantityMagnitude(SimpleUnitSystem.from(Collections.emptyMap()));

  /** @return instance of QuantityMagnitude that uses the built-in SI convention */
  public static QuantityMagnitude SI() {
    return SI;
  }

  /** @param unit
   * @return operator that extracts the value from a Quantity of given unit */
  public static ScalarUnaryOperator singleton(Unit unit) {
    return EMPTY.in(unit);
  }

  /** @param string
   * @return operator that extracts the value from a Quantity of unit specified by given string */
  public static ScalarUnaryOperator singleton(String string) {
    return singleton(Unit.of(string));
  }

  // ---
  private final UnitSystem unitSystem;

  /** creates instance for quantity conversion and magnitude extraction
   * that is backed by given unitSystem
   * 
   * @param unitSystem
   * @throws Exception if given {@link UnitSystem} is null */
  public QuantityMagnitude(UnitSystem unitSystem) {
    this.unitSystem = Objects.requireNonNull(unitSystem);
  }

  /** Example:
   * <pre>
   * QuantityMagnitude.SI().in(Unit.of("K*m^2")).apply(Quantity.of(2, "K*km^2"))
   * == RealScalar.of(2_000_000)
   * <pre>
   * 
   * @param unit
   * @return operator that maps a quantity to the equivalent scalar of given unit */
  public ScalarUnaryOperator in(Unit unit) {
    return new Inner(unit);
  }

  /** @param string
   * @return
   * @see #in(Unit) */
  public ScalarUnaryOperator in(String string) {
    return in(Unit.of(string));
  }

  private class Inner implements ScalarUnaryOperator {
    private final Unit unit;
    private final Unit base;

    public Inner(Unit unit) {
      this.unit = unit;
      this.base = unit.negate();
    }

    @Override
    public Scalar apply(Scalar scalar) {
      Scalar result = unitSystem.apply(StaticHelper.multiply(scalar, base));
      if (result instanceof Quantity)
        throw new Throw(scalar, unit);
      return result;
    }

    @Override // from Object
    public String toString() {
      return MathematicaFormat.concise("QuantityMagnitude", unitSystem, unit);
    }
  }
}
