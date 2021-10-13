// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.sca.Abs;

/** vector Infinity-norm
 * 
 * ||{a, b, c}||_inf = Max[|a|, |b|, |c|] */
public enum VectorInfinityNorm {
  ;
  public static final TensorUnaryOperator NORMALIZE = Normalize.with(VectorInfinityNorm::of);

  /** @param vector
   * @return infinity-norm of given vector, i.e. max_i |a_i| */
  public static Scalar of(Tensor vector) {
    return vector.stream() //
        .map(Scalar.class::cast) //
        .map(Abs.FUNCTION) //
        .reduce(Max::of) //
        .orElseThrow();
  }

  /** @param v1 vector
   * @param v2 vector
   * @return infinity-norm of vector difference || v1 - v2 || */
  public static Scalar between(Tensor v1, Tensor v2) {
    return of(v1.subtract(v2));
  }
}
