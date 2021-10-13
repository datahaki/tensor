// code by jph
package ch.alpine.tensor.mat.gr;

import java.util.concurrent.atomic.AtomicInteger;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;

/* package */ enum StaticHelper {
  ;
  public static Tensor residualMaker(Tensor matrix) {
    AtomicInteger atomicInteger = new AtomicInteger();
    // I-X^+.X is projector on ker X
    return Tensor.of(matrix.stream() //
        .map(Tensor::negate) // copy
        .map(row -> {
          int index = atomicInteger.getAndIncrement();
          row.set(scalar -> scalar.add(((Scalar) scalar).one()), index);
          return row; // by ref
        }));
  }

  /** @param scalar
   * @return clips given scalar to unit interval [0, 1]
   * @throws Exception if given scalar is significantly outside of unit interval */
  public static Scalar requireUnit(Scalar scalar) {
    Scalar result = Clips.unit().apply(scalar);
    Chop._06.requireClose(result, scalar);
    return result;
  }
}
