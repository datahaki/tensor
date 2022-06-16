// code by jph
package ch.alpine.tensor.nrm;

import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.Abs;

/** vector 1-norm
 * 
 * ||{a, b, c}||_1 = |a| + |b| + |c| */
public enum Vector1Norm {
  ;
  public static final TensorUnaryOperator NORMALIZE = Normalize.with(Vector1Norm::of);

  /** @param vector
   * @return 1-norm of given vector, i.e. |a_1| + ... + |a_n| also known as ManhattanDistance */
  public static Scalar of(Tensor vector) {
    return of(vector.stream().map(Scalar.class::cast));
  }

  /** @param stream of scalars
   * @return sum of absolute values of scalars in given stream
   * @throws Exception if stream is empty */
  public static Scalar of(Stream<Scalar> stream) {
    return stream.map(Abs.FUNCTION).reduce(Scalar::add).orElseThrow();
  }

  /** inspired by
   * <a href="https://reference.wolfram.com/language/ref/ManhattanDistance.html">ManhattanDistance</a>
   * 
   * @param v1 vector
   * @param v2 vector
   * @return 1-norm of vector difference || v1 - v2 || */
  public static Scalar between(Tensor v1, Tensor v2) {
    return of(v1.subtract(v2));
  }
}
