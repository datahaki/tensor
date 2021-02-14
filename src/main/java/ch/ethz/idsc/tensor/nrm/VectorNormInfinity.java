// code by jph
package ch.ethz.idsc.tensor.nrm;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Abs;

public enum VectorNormInfinity {
  ;
  public static final TensorUnaryOperator NORMALIZE = Normalize.with(VectorNormInfinity::of);

  /** @param vector
   * @return infinity-norm of given vector, i.e. max_i |a_i| */
  public static Scalar of(Tensor vector) {
    return vector.stream() //
        .map(Scalar.class::cast) //
        .map(Abs.FUNCTION) //
        .reduce(Max::of).get();
  }

  /** @param v1 vector
   * @param v2 vector
   * @return infinity-norm of vector difference || v1 - v2 || */
  public static Scalar between(Tensor v1, Tensor v2) {
    return of(v1.subtract(v2));
  }
}
