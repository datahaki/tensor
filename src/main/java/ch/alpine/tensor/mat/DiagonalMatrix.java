// code by jph
package ch.alpine.tensor.mat;

import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.spa.SparseArray;

/** consistent with Mathematica, in particular DiagonalMatrix[{}] results in error.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/DiagonalMatrix.html">DiagonalMatrix</a> */
public enum DiagonalMatrix {
  ;
  /** @param vector with scalars to appear on the diagonal
   * @return
   * @throws Exception if given vector is empty or a {@link Scalar} */
  public static Tensor with(Tensor vector) {
    int length = Integers.requirePositive(vector.length());
    return Tensors.matrix((i, j) -> i.equals(j) ? vector.Get(i) : vector.Get(i).zero(), length, length);
    // List<Scalar> list = vector.stream() //
    // .map(Scalar.class::cast) //
    // .map(Scalar::zero) //
    // .distinct() //
    // .limit(2).toList();
    // if (list.size() == 1) {
    // int n = vector.length();
    // Tensor tensor = SparseArray.of(list.get(0), n, n);
    // IntStream.range(0, n).forEach(i -> tensor.set(vector.get(i), i, i));
    // return tensor;
    // }
    // throw new Throw(vector);
  }

  /** @param n
   * @param value
   * @return matrix of dimensions n x n with value as diagonal entries
   * @throws Exception if n is negative or zero */
  public static Tensor of(int n, Scalar value) {
    Integers.requirePositive(n);
    Scalar zero = value.zero();
    Tensor tensor = SparseArray.of(zero, n, n);
    IntStream.range(0, n).forEach(i -> tensor.set(value, i, i));
    return tensor;
  }

  /** @param scalars
   * @return square matrix with scalars along diagonal
   * @throws Exception if list of scalars is empty */
  public static Tensor of(Scalar... scalars) {
    return with(Tensors.of(scalars));
  }

  /** @param numbers
   * @return square matrix with numbers as {@link RealScalar}s along diagonal
   * @throws Exception if list of numbers is empty */
  public static Tensor of(Number... numbers) {
    return with(Tensors.vector(numbers));
  }
}
