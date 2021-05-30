// code by jph
package ch.alpine.tensor.mat.re;

import java.util.List;
import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.red.Diagonal;

/** implementation is consistent with Mathematica
 * 
 * <p>The determinant of an empty matrix Det[{{}}] throws an exception
 * just as <code>Mathematica::Det[{{}}]</code> results in Exception
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Det.html">Det</a> */
public enum Det {
  ;
  /** @param matrix
   * @return determinant of matrix */
  public static Scalar of(Tensor matrix) {
    return of(matrix, Pivots.selection(matrix));
  }

  /** @param matrix
   * @param pivot
   * @return determinant of matrix */
  public static Scalar of(Tensor matrix, Pivot pivot) {
    Dimensions dimensions = new Dimensions(matrix);
    List<Integer> list = dimensions.list();
    int n = list.get(0);
    int m = list.get(1);
    if (m == 0 || //
        !dimensions.isArray() || //
        dimensions.maxDepth() != 2)
      throw TensorRuntimeException.of(matrix);
    if (n == m) // square
      return Determinant.of(matrix, pivot);
    Objects.requireNonNull(pivot);
    return Diagonal.of(matrix).stream() //
        .map(Scalar.class::cast) //
        .map(Scalar::zero) //
        .reduce(Scalar::add).orElseThrow();
  }
}
