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
    Unit base = unit.negate();
    return scalar -> !(unitSystem.apply(StaticHelper.multiply(scalar, base)) instanceof Quantity);
  }
}
