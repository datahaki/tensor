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
