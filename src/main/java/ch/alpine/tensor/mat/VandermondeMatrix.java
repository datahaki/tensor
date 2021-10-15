// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.NestList;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.itp.Fit;

/** Reference:
 * https://en.wikipedia.org/wiki/Vandermonde_matrix
 * 
 * <pre>
 * 1 x0 x0^2 ... x0^d
 * 1 x1 x1^2 ... x1^d
 * 1 x2 x2^2 ... x2^d
 * ...
 * 1 xn xn^2 ... xn^d
 * </pre>
 * 
 * @see Fit */
public enum VandermondeMatrix {
  ;
  /** @param vector
   * @param degree non negative
   * @return */
  public static Tensor of(Tensor vector, int degree) {
    Integers.requirePositiveOrZero(degree);
    return Tensor.of(vector.stream() //
        .map(Scalar.class::cast) //
        .map(scalar -> NestList.of(scalar::multiply, scalar.one(), degree))); //
  }

  /** @param vector non-empty
   * @return
   * @throws Exception if vector is empty */
  public static Tensor of(Tensor vector) {
    return of(vector, vector.length() - 1);
  }
}
