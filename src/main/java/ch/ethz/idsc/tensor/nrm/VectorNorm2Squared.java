// code by jph
package ch.ethz.idsc.tensor.nrm;

import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.AbsSquared;

/** @see AbsSquared */
public enum VectorNorm2Squared {
  ;
  /** @param vector
   * @return squared Euclidean norm of given vector, i.e. || v1 || ^ 2
   * @throws Exception if vector is empty */
  public static Scalar of(Tensor vector) {
    return ofVector(vector.stream().map(Scalar.class::cast));
  }

  /** @param stream of scalars
   * @return sum of squares of scalars in given stream */
  public static Scalar ofVector(Stream<Scalar> stream) {
    return stream.map(AbsSquared.FUNCTION).reduce(Scalar::add).get();
  }

  /** @param v1 vector
   * @param v2 vector
   * @return squared Euclidean norm of vector difference, i.e. || v1 - v2 || ^ 2 */
  public static Scalar between(Tensor v1, Tensor v2) {
    return VectorNorm2Squared.of(v1.subtract(v2));
  }
}
