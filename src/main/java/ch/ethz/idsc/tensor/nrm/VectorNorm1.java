// code by jph
package ch.ethz.idsc.tensor.nrm;

import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Abs;

public enum VectorNorm1 {
  ;
  public static final TensorUnaryOperator NORMALIZE = Normalize.with(VectorNorm1::of);

  /** @param vector
   * @return 1-norm of given vector, i.e. |a_1| + ... + |a_n| also known as ManhattanDistance */
  public static Scalar of(Tensor vector) {
    return of(vector.stream().map(Scalar.class::cast));
  }

  /** @param stream of scalars
   * @return sum of absolute values of scalars in given stream
   * @throws Exception if stream is empty */
  public static Scalar of(Stream<Scalar> stream) {
    return stream.map(Abs.FUNCTION).reduce(Scalar::add).get();
  }

  /** @param v1 vector
   * @param v2 vector
   * @return 1-norm of vector difference || v1 - v2 || */
  public static Scalar between(Tensor v1, Tensor v2) {
    return of(v1.subtract(v2));
  }
}
