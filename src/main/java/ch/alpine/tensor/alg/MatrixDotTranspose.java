// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.Tensor;

/** dot product Dot[a, Transpose[b]] optimized for matrices (and restricted to them) */
public enum MatrixDotTranspose {
  ;
  /** the return value of the function is always identical to
   * <pre>
   * Dot[a, Transpose[b]]
   * </pre>
   * However, the expression Dot[a, Transpose[b]] works for a broader range of input.
   * 
   * @param a matrix
   * @param b tensor of rank at least 2
   * @return a . Transpose[b]
   * @throws Exception if a is not a matrix, or b has rank lower 2 */
  public static Tensor of(Tensor a, Tensor b) {
    a.Get(0, 0); // fail fast if parameter 'a' is not a matrix
    return Tensor.of(a.stream().map(row -> Tensor.of(b.stream().map(row::dot))));
  }
}
