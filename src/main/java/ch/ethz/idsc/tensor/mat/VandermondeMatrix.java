// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.NestList;
import ch.ethz.idsc.tensor.ext.Integers;
import ch.ethz.idsc.tensor.itp.Fit;

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
