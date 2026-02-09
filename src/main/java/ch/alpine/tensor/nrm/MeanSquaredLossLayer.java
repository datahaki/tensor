// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.red.Mean;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/MeanSquaredLossLayer.html">MeanSquaredLossLayer</a> */
public enum MeanSquaredLossLayer {
  ;
  public static Scalar of(Tensor a, Tensor b) {
    // TODO TENSOR not sure
    return Mean.ofVector(Flatten.of(a.subtract(b)).maps(s -> s.multiply(s)));
  }
}
