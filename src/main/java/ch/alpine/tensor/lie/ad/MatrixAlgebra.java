// code by jph
package ch.alpine.tensor.lie.ad;

import java.io.Serializable;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.lie.MatrixBracket;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.spa.SparseArray;

public class MatrixAlgebra implements Serializable {
  private final Tensor basis;
  /** m is a matrix with basis elements flattened to rows
   * m has at least as many columns as rows */
  private final Tensor matrix;
  private final Tensor ad;

  /** @param basis consisting of n matrices that generate the Lie algebra
   * @throws Exception if basis contains redundant elements */
  public MatrixAlgebra(Tensor basis) {
    this.basis = ExactTensorQ.require(basis);
    matrix = Transpose.of(Tensor.of(basis.stream().map(Flatten::of)));
    Integers.requireEquals(basis.length(), MatrixRank.of(matrix));
    int n = basis.length();
    ad = SparseArray.of(RealScalar.ZERO, n, n, n);
    for (int i = 0; i < n; ++i)
      for (int j = i + 1; j < n; ++j) {
        Tensor x = LinearSolve.any(matrix, Flatten.of(MatrixBracket.of(basis.get(i), basis.get(j))));
        ad.set(x, Tensor.ALL, j, i);
        ad.set(x.negate(), Tensor.ALL, i, j);
      }
  }

  /** @return sparse array of rank 3 with dimensions n x n x n
   * @see JacobiIdentity */
  public Tensor ad() {
    return ad;
  }

  /** @param matrix
   * @return vector with vector . basis == matrix
   * @throws Exception if given matrix is not an element in the matrix Lie algebra */
  public Tensor toVector(Tensor matrix) {
    Tensor x = LinearSolve.any(this.matrix, Flatten.of(matrix));
    Tolerance.CHOP.requireClose(x.dot(basis), matrix);
    return x;
  }

  /** @param vector
   * @return */
  public Tensor toMatrix(Tensor vector) {
    return vector.dot(basis);
  }
}
