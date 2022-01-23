// code by jph
package ch.alpine.tensor.mat;

import java.util.function.BiFunction;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Integers;

/** utility to build DistanceMatrix */
public enum UpperEvaluation {
  ;
  /** builds a matrix that can be
   * {@link SymmetricMatrixQ}
   * {@link HermitianMatrixQ}
   * {@link UpperTriangularize}
   * ...
   * 
   * @param ps of length n
   * @param qs of length n
   * @param function mapping i-th element of sequence and j to matrix element (i, j)
   * @param mapping from matrix element (i, j) to matrix element (j, i)
   * @return matrix of size n x n */
  public static Tensor of(Tensor ps, Tensor qs, //
      BiFunction<Tensor, Tensor, Scalar> function, //
      ScalarUnaryOperator flip) {
    int n = Integers.requireEquals(ps.length(), qs.length());
    Scalar[][] matrix = new Scalar[n][n];
    Tensor[] q = qs.stream().toArray(Tensor[]::new);
    int i = 0;
    for (Tensor p : ps) {
      matrix[i][i] = function.apply(p, q[i]);
      for (int j = i + 1; j < n; ++j) {
        Scalar scalar = function.apply(p, q[j]);
        matrix[i][j] = scalar;
        matrix[j][i] = flip.apply(scalar);
      }
      ++i;
    }
    return Tensors.matrix(matrix);
  }
}
