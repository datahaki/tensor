// code by jph
package ch.alpine.tensor.qty;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

/** Example:
 * The comparator of the SI unit system sorts the sequence
 * {4[rad], 300[deg], 2, 180[rad], -1[rad]} to
 * {-1[rad], 2, 4[rad], 300[deg], 180[rad]} */
public class QuantityComparator implements Comparator<Scalar>, Serializable {
  private static final QuantityComparator SI = of(UnitSystem.SI());

  /** @param unitSystem non-null
   * @return */
  public static QuantityComparator of(UnitSystem unitSystem) {
    return new QuantityComparator(Objects.requireNonNull(unitSystem));
  }

  /** @return */
  public static QuantityComparator SI() {
    return SI;
  }

  // ---
  private final UnitSystem unitSystem;

  /** @param unitSystem */
  private QuantityComparator(UnitSystem unitSystem) {
    this.unitSystem = unitSystem;
  }

  @Override // from Comparator<Scalar>
  public int compare(Scalar scalar1, Scalar scalar2) {
    return Scalars.compare( //
        unitSystem.apply(scalar1), //
        unitSystem.apply(scalar2));
  }

  /** @param s1
   * @param s2
   * @return true if s1 < s2
   * @throws Exception if s1 and s2 do not have compatible units */
  public boolean lessThan(Scalar s1, Scalar s2) {
    return compare(s1, s2) < 0;
  }

  /** @param s1
   * @param s2
   * @return true if s1 <= s2
   * @throws Exception if s1 and s2 do not have compatible units */
  public boolean lessEquals(Scalar s1, Scalar s2) {
    return compare(s1, s2) <= 0;
  }
}
