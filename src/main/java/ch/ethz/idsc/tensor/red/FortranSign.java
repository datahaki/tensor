// code by jph
package ch.ethz.idsc.tensor.red;

import java.util.function.BiFunction;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** Function returns a or a.negate() depending on sign of a and b.
 * 
 * The function appears in the Fortran language and old literature. */
public enum FortranSign implements BiFunction<Scalar, Scalar, Scalar> {
  bifunction;
  // ---
  @Override
  public Scalar apply(Scalar a, Scalar b) {
    int sa = ((RealScalar) a).getSignInt();
    int sb = ((RealScalar) b).getSignInt();
    return 0 <= sb ? (0 <= sa ? a : a.negate()) : (0 <= sa ? a.negate() : a);
  }
}
