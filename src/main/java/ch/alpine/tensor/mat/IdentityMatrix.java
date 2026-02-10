// code by jph
package ch.alpine.tensor.mat;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.ex.MatrixPower;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.red.KroneckerDelta;
import ch.alpine.tensor.spa.SparseArray;

/** implementation is consistent with Mathematica.
 * 
 * <pre>
 * IdentityMatrix[2] == {{1, 0}, {0, 1}}
 * </pre>
 * 
 * For non-positive input:
 * <pre>
 * IdentityMatrix[0] => Exception
 * IdentityMatrix[-3] => Exception
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/IdentityMatrix.html">IdentityMatrix</a>
 * 
 * @see DiagonalMatrix
 * @see MatrixPower
 * @see Inverse */
public enum IdentityMatrix {
  ;
  /** @param n positive
   * @return identity matrix of dimensions n x n
   * @throws Exception if n is negative or zero */
  public static Tensor of(int n) {
    Integers.requirePositive(n);
    return Tensors.matrix(KroneckerDelta::of, n, n);
  }

  /** @param n
   * @return */
  public static Stream<Tensor> stream(int n) {
    return IntStream.range(0, n).mapToObj(k -> UnitVector.of(n, k));
  }

  /** @param n
   * @return identity matrix as {@link SparseArray} with dimensions n x n
   * @throws Exception if n is negative or zero */
  public static Tensor sparse(int n) {
    Integers.requirePositive(n);
    Tensor tensor = Array.sparse(n, n);
    IntStream.range(0, n).forEach(i -> tensor.set(RealScalar.ONE, i, i));
    return tensor;
  }

  /** function provides the neutral multiplicative element for a matrix
   * with entries of type {@link GaussScalar}, etc.
   * 
   * @param matrix square
   * @return
   * @throws Exception if given matrix is not a square matrix */
  public static Tensor of(Tensor matrix) {
    int n = Integers.requireEquals(matrix.length(), Unprotect.dimension1(matrix));
    return Tensors.matrix((i, j) -> i.equals(j) //
        ? matrix.Get(i, j).one()
        : matrix.Get(i, j).one().zero(), n, n);
  }
  private static final ScalarUnaryOperator INCR = s -> s.add(s.one());
  private static final ScalarUnaryOperator DECR = s -> s.subtract(s.one());

  /** Careful: the function alters the input matrix !
   * 
   * @param matrix
   * @return given matrix with diagonal elements incremented by {@link Scalar#one()}
   * @throws Exception if matrix is {@link Tensor#unmodifiable()} */
  public static Tensor inplaceAdd(Tensor matrix) {
    SquareMatrixQ.INSTANCE.requireMember(matrix);
    IntStream.range(0, matrix.length()).forEach(i -> matrix.set(INCR, i, i));
    return matrix;
  }

  
  /** Careful: the function alters the input matrix !
   * 
   * @param matrix
   * @return given matrix with diagonal elements decremented by {@link Scalar#one()}
   * @throws Exception if matrix is {@link Tensor#unmodifiable()} */
  public static Tensor inplaceSub(Tensor matrix) {
    SquareMatrixQ.INSTANCE.requireMember(matrix);
    IntStream.range(0, matrix.length()).forEach(i -> matrix.set(DECR, i, i));
    return matrix;
  }
}
