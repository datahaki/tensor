// code by jph
package ch.alpine.tensor.mat;

import java.util.stream.IntStream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;

/** TODO implementation does not give a speedup yet ! */
public enum TransposeMatrixDot {
  ;
  public static Tensor of(Tensor matrix, Tensor tensor) {
    matrix.Get(0, 0);
    int m = Unprotect.dimension1Hint(matrix);
    // return matrix.stream() //
    // .map(row -> TensorProduct.of(row, tensor)) //
    // .reduce(Tensor::add) //
    // .orElseThrow();
    return Tensor.of(IntStream.range(0, m) //
        .mapToObj(i -> matrix.get(Tensor.ALL, i).dot(tensor)));
  }

  public static Tensor self(Tensor matrix) {
    return of(matrix, matrix);
  }
}
