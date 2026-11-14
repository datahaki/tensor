// code by jph
package ch.alpine.tensor.mat;

import java.util.stream.IntStream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;

/** CAREFUL: implementation does not give a speedup yet,
 * but timing is identical to Transpose.of(A).dot(B) */
public enum TransposeMatrixDot {
  ;
  public static Tensor of(Tensor matrix, Tensor tensor) {
    matrix.Get(0, 0);
    // slower
    // Int myI = new Int();
    // return matrix.stream() //
    // .map(row -> TensorProduct.of(row, tensor.get(myI.getAndIncrement()))) //
    // .reduce(Tensor::add) //
    // .orElseThrow();
    int m = Unprotect.dimension1Hint(matrix);
    return Tensor.of(IntStream.range(0, m) //
        .mapToObj(i -> matrix.get(Tensor.ALL, i).dot(tensor)));
  }

  public static Tensor self(Tensor matrix) {
    return of(matrix, matrix);
  }
}
