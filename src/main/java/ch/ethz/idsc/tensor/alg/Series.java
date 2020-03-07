// code by jph
package ch.ethz.idsc.tensor.alg;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Series.html">Series</a> */
public enum Series {
  ;
  /** polynomial evaluation
   * 
   * <pre>
   * Series.of({a, b, c, d}).apply(x)
   * == a + b*x + c*x^2 + d*x^3
   * == a + x*(b + x*(c + x*(d)))
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
