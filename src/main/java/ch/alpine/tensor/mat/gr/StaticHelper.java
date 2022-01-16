// code by jph
package ch.alpine.tensor.mat.gr;

import java.util.concurrent.atomic.AtomicInteger;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

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
}
