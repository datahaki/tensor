// code by jph
package ch.alpine.tensor.nrm;

import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.AbsSquared;

/** Euclidean norm squared
 * 
 * ||{a, b, c}||_2^2 = a^2 + b^2 + c^2
 * 
 * @see AbsSquared */
public enum Vector2NormSquared {
  ;
  /** @param vector
   * @return squared Euclidean norm of given vector, i.e. || v1 || ^ 2
   * @throws Exception if vector is empty */
  public static Scalar of(Tensor vector) {
    return of(vector.stream().map(Scalar.class::cast));
  }

  /** @param stream of scalars
   * @return sum of squares of scalars in given stream */
  public static Scalar of(Stream<Scalar> stream) {
    return stream.map(AbsSquared.FUNCTION).reduce(Scalar::add).get();
  }

  /** @param v1 vector
   * @param v2 vector
   * @return squared Euclidean norm of vector difference, i.e. || v1 - v2 || ^ 2 */
  public static Scalar between(Tensor v1, Tensor v2) {
    return of(v1.subtract(v2));
  }
}
