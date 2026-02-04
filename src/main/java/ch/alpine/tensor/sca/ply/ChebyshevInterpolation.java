// code by jph
package ch.alpine.tensor.sca.ply;

import ch.alpine.tensor.api.ScalarUnaryOperator;

/** References:
 * "Barycentric Lagrange Interpolation"
 * by Jean-Paul Berrut, Lloyd N. Trefethen
 * 
 * "Barycentric interpolation in Chebyshev points"
 * Trefethen 5.2 eq. 5.13 */
public enum ChebyshevInterpolation {
  ;
  /** @param function
   * @param chebyshevNodes
   * @param n corresponds to (degree of polynomial + 1)
   * @return */
  public static ScalarUnaryOperator of(ScalarUnaryOperator function, ChebyshevNodes chebyshevNodes, int n) {
    return ClenshawChebyshev.of(chebyshevNodes.coeffs(function, n));
  }

  /** @param function
   * @param n corresponds to (degree of polynomial + 1)
   * @return */
  public static ScalarUnaryOperator of(ScalarUnaryOperator function, int n) {
    return of(function, ChebyshevNodes._1, n);
  }
}
