// code by jph
package ch.ethz.idsc.tensor.qty;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Predicate;

import ch.ethz.idsc.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/CompatibleUnitQ.html">CompatibleUnitQ</a> */
public class CompatibleUnitQ implements Serializable {
  private static final long serialVersionUID = 3067446056713733961L;
  private static final CompatibleUnitQ SI = in(UnitSystem.SI());

  /** @param unitSystem non-null
   * @return predicate supplier according to given unit system */
  public static CompatibleUnitQ in(UnitSystem unitSystem) {
    return new CompatibleUnitQ(Objects.requireNonNull(unitSystem));
  }

  /** Examples:
   * <pre>
   * CompatibleUnitQ.SI().with(Unit.of("PS^2")).test(Quantity.of(2, "W^2")) == true
   * CompatibleUnitQ.SI().with(Unit.of("m*s^-1")).test(Quantity.of(2, "m*s")) == false
   * </pre>
   * 
   * @return predicate supplier according to built-in unit system */
  public static CompatibleUnitQ SI() {
    return SI;
  }

  /***************************************************/
  private final UnitSystem unitSystem;

  private CompatibleUnitQ(UnitSystem unitSystem) {
    this.unitSystem = unitSystem;
  }

  /** @param unit
   * @return */
  public Predicate<Scalar> with(Unit unit) {
    return new Inner(unit);
  }

  /** @param string
   * @return */
  public Predicate<Scalar> with(String string) {
    return with(Unit.of(string));
  }

  private class Inner implements Predicate<Scalar>, Serializable {
    private static final long serialVersionUID = 3755197923097596074L;
    // ---
    private final Unit unit;
    private final Unit base;

    public Inner(Unit unit) {
      this.unit = unit;
      this.base = unit.negate();
    }

    @Override // from Predicate
    public boolean test(Scalar scalar) {
      // LONGTERM the check also tracks the value part, which is not needed
      return !(unitSystem.apply(StaticHelper.multiply(scalar, base)) instanceof Quantity);
    }

    @Override
    public String toString() {
      return String.format("%s[%s, %s]", CompatibleUnitQ.class.getSimpleName(), unitSystem, unit);
    }
  }
}
