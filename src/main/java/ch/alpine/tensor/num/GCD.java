// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.sca.Mod;

/** Mathematica always returns non-negative divisor, for example
 * <pre>
 * GCD[5, 10] == GCD[5, -10] == GCD[-5, 10] == GCD[-5, -10] == 5
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/GCD.html">GCD</a> */
public enum GCD {
  ;
  /** @param a scalar in exact precision
   * @param b scalar in exact precision
   * @return greatest common divider of a and b
   * @throws Exception if either parameter is not in exact precision */
  public static Scalar of(Scalar a, Scalar b) {
    ExactScalarQ.require(a);
    ExactScalarQ.require(b);
    while (Scalars.nonZero(b)) {
      Scalar c = a;
      a = b;
      b = Mod.function(b).apply(c);
    }
    return StaticHelper.normalForm(a);
  }
}
