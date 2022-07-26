// code by jph
package ch.alpine.tensor.qty;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/UnitConvert.html">UnitConvert</a> */
public class UnitConvert implements Serializable {
  private static final UnitConvert SI = of(UnitSystem.SI());

  /** @param unitSystem non-null
   * @return */
  public static UnitConvert of(UnitSystem unitSystem) {
    return new UnitConvert(Objects.requireNonNull(unitSystem));
  }

  /** @return instance of UnitConvert that uses the built-in SI convention */
  public static UnitConvert SI() {
    return SI;
  }

  // ---
  private final UnitSystem unitSystem;

  /** @param unitSystem non-null
   * @throws Exception if given {@link UnitSystem} is null */
  private UnitConvert(UnitSystem unitSystem) {
    this.unitSystem = unitSystem;
  }

  /** Example:
   * <code>
   * UnitConvert.SI().to(Unit.of("N")).apply(Quantity.of(981, "cm*kg*s^-2"))
   * == Quantity.fromString("981/100[N]")
   * </code>
   * 
   * @param unit
   * @return operator that maps a quantity to the quantity of given unit */
  public ScalarUnaryOperator to(Unit unit) {
    return new Inner(unit);
  }

  /** Example:
   * <code>
   * UnitConvert.SI().to("N").apply(Quantity.of(981, "cm*kg*s^-2"))
   * == Quantity.fromString("981/100[N]")
   * </code>
   * 
   * @param string
   * @return operator that maps a quantity to the quantity of given unit */
  public ScalarUnaryOperator to(String string) {
    return to(Unit.of(string));
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
      return Quantity.of(unitSystem.apply(StaticHelper.multiply(scalar, base)), unit);
    }

    @Override // from Object
    public String toString() {
      return MathematicaFormat.concise("UnitConvert", unitSystem, unit);
    }
  }
}
