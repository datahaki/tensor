// code by jph
package ch.alpine.tensor.mat;

import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.red.EqualsReduce;
import ch.alpine.tensor.spa.SparseArray;

/** consistent with Mathematica, in particular DiagonalMatrix[{}] results in error.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/DiagonalMatrix.html">DiagonalMatrix</a> */
public enum DiagonalMatrix {
  ;
  /** @param vector with scalars to appear on the diagonal
   * @return sparse diagonal matrix with vector as diagonal
   * @throws Exception if given vector is empty or a {@link Scalar} */
  public static Tensor sparse(Tensor vector) {
    Scalar zero = EqualsReduce.zero(vector);
    int n = vector.length();
    Tensor tensor = SparseArray.of(zero, n, n);
    IntStream.range(0, n).forEach(i -> tensor.set(vector.Get(i), i, i));
    return tensor;
  }

  /** @param vector with scalars to appear on the diagonal
   * @return diagonal matrix with vector as diagonal
   * @throws Exception if given vector is empty or a {@link Scalar} */
  public static Tensor full(Tensor vector) {
    int n = Integers.requirePositive(vector.length());
    return Tensors.matrix((i, j) -> i.equals(j) ? vector.Get(i) : vector.Get(i).zero(), n, n);
  }

  /** @param n
   * @param scalar
   * @return matrix of dimensions n x n with value as diagonal entries
   * @throws Exception if n is negative or zero */
  public static Tensor of(int n, Scalar scalar) {
    Integers.requirePositive(n);
    Tensor tensor = SparseArray.of(scalar.zero(), n, n);
    IntStream.range(0, n).forEach(i -> tensor.set(scalar, i, i));
    return tensor;
  }

  /** @param scalars
   * @return square matrix with scalars along diagonal
   * @throws Exception if list of scalars is empty */
  public static Tensor of(Scalar... scalars) {
    return full(Tensors.of(scalars));
  }

  /** @param numbers
   * @return square matrix with numbers as {@link RealScalar}s along diagonal
   * @throws Exception if list of numbers is empty */
  public static Tensor of(Number... numbers) {
    return sparse(Tensors.vector(numbers));
  }
}
