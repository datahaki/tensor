// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Series.html">Series</a> */
public enum Series {
  ;
  /** polynomial evaluation
   * 
   * <pre>
   * Series.of({a, b, c, d}).apply(x)
   * == a + b*x + c*x^2 + d*x^3
   * == a + (b + (c + d ** x) ** x ) ** x
   * </pre>
   * 
   * Given an empty list of coefficients the operator evaluates to zero for any parameter x
   * <pre>
   * Series.of({}).apply(x) == 0
   * </pre>
   * 
   * @param coeffs of polynomial
   * @return evaluation of polynomial for scalar input
   * @throws Exception if input is not a vector */
  public static ScalarUnaryOperator of(Tensor coeffs) {
    return new HornerScheme(Reverse.of(VectorQ.require(coeffs)));
  }
}
