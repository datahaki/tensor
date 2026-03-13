// code by jph
package ch.alpine.tensor.qty;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;

/** resolves unit conflict by comparing conversion to respective other unit
 * and choosing the result with the shorter string expression. */
public class UnitDimensionsBase implements UnitDimensions, Serializable {
  public static final UnitDimensions SI = new UnitDimensionsBase(UnitSystem.SI());
  // ---
  private final UnitSystem unitSystem;
  private final CompatibleUnitQ compatibleUnitQ;
  private final UnitConvert unitConvert;

  public UnitDimensionsBase(UnitSystem unitSystem) {
    this.unitSystem = unitSystem;
    compatibleUnitQ = CompatibleUnitQ.in(unitSystem);
    unitConvert = UnitConvert.of(unitSystem);
  }

  @Override
  public Scalar normalForm(Scalar scalar) {
    return unitSystem.apply(scalar);
  }

  @Override
  public Scalar plus(Scalar a, Scalar b) {
    if (b instanceof DateTime)
      return b.add(a);
    Unit ua = QuantityUnit.of(a);
    boolean test = compatibleUnitQ.with(ua).test(b);
    if (!test)
      throw new Throw(a, b);
    Unit ub = QuantityUnit.of(b);
    Scalar sa = unitConvert.to(ub).apply(a);
    Scalar sb = unitConvert.to(ua).apply(b);
    if (sb instanceof Quantity && sa instanceof Quantity) {
      Scalar aqb = a.add(sb);
      Scalar bqa = b.add(sa);
      String sab = aqb.toString();
      String sba = bqa.toString();
      int la = sab.length();
      int lb = sba.length();
      if (la == lb)
        return sab.compareTo(sba) < 0 ? aqb : bqa;
      return la < lb ? aqb : bqa;
    }
    Scalar qa = normalForm(a);
    Scalar qb = normalForm(b);
    return qa.add(qb);
  }
}
