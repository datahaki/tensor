// code by jph
package ch.alpine.tensor.alg;

import java.util.stream.IntStream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.Inverse;
import ch.alpine.tensor.mat.LinearSolve;

/** transform a (r, s)-tensor to a new basis */
public enum BasisTransform {
  ;
  /** Hint: if r is non-zero then v has to be a square matrix with full rank
   * 
   * @param tensor of type (r, s)
   * @param r non-negative
   * @param v matrix
   * @return
   * @throws Exception if r is negative */
  public static Tensor of(Tensor tensor, int r, Tensor v) {
    int rank = TensorRank.of(tensor);
    Tensor inverse = 0 < Integers.requirePositiveOrZero(r) //
        ? Transpose.of(Inverse.of(v))
        : null;
    int[] sigma = IntStream.range(rank - 1, 2 * rank - 1) //
        .map(index -> index % rank) //
        .toArray(); // [r, 0, 1, ..., r - 1]
    for (int index = 0; index < rank; ++index)
      tensor = Transpose.of(tensor, sigma).dot(index < r ? inverse : v);
    return tensor;
  }

  /** In the special case, when the form has rank 2 ("bilinear form"), then
   * <pre>
   * BasisTransform.ofForm(form, v) == Transpose.of(v).dot(form).dot(v)
   * </pre>
   * 
   * @param form is a (0, s)-tensor with all dimensions equal
   * @param v matrix not necessarily square
   * @return tensor of form with respect to basis v
   * @throws Exception if form is not a regular array, or v is not a matrix */
  public static Tensor ofForm(Tensor form, Tensor v) {
    return of(form, 0, v);
  }

  /** @param matrix is (1, 1)-tensor
   * @param v square matrix with full rank
   * @return Inverse[v] . matrix . v */
  public static Tensor ofMatrix(Tensor matrix, Tensor v) {
    return LinearSolve.of(v, matrix.dot(v));
  }
}
