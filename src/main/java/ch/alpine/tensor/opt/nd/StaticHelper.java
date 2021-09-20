// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.stream.IntStream;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.alg.VectorQ;

/* package */ enum StaticHelper {
  ;
  public static void require(Tensor lbounds, Tensor ubounds) {
    VectorQ.require(lbounds);
    VectorQ.require(ubounds);
    if (lbounds.length() != ubounds.length())
      throw TensorRuntimeException.of(lbounds, ubounds);
    if (!IntStream.range(0, lbounds.length()).allMatch(index -> Scalars.lessEquals(lbounds.Get(index), ubounds.Get(index))))
      throw TensorRuntimeException.of(lbounds, ubounds);
  }
}
