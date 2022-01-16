// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.NestList;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.itp.Fit;

/** Given a vector with entries {x0, x1, ..., xn}, the Vandermonde matrix
 * is the square matrix of the form
 * <pre>
 * 1 x0 x0^2 ... x0^d
 * 1 x1 x1^2 ... x1^d
 * 1 x2 x2^2 ... x2^d
 * ...
 * 1 xn xn^2 ... xn^d
 * </pre>
 * 
 * <p>Remark: the transpose of the matrix above is also referred to by the
 * same name.
 * 
 * <p>References:
 * https://en.wikipedia.org/wiki/Vandermonde_matrix
 * NR 2007
 * 
 * "Linear Algebra and Learning from Data", p.180
 * by Gilbert Strang, 2019
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
