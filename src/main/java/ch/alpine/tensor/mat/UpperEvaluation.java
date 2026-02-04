// code by jph
package ch.alpine.tensor.mat;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
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
   * @param flip mapping from matrix element (i, j) to matrix element (j, i)
   * @return matrix of size n x n */
  public static <T extends Tensor> Tensor of(Tensor ps, Tensor qs, //
      BiFunction<Tensor, Tensor, T> function, //
      UnaryOperator<T> flip) {
    int n = Integers.requireEquals(ps.length(), qs.length());
    Tensor matrix = Array.zeros(n, n);
    Tensor[] q = qs.stream().toArray(Tensor[]::new);
    int i = 0;
    for (Tensor p : ps) {
      matrix.set(function.apply(p, q[i]), i, i);
      for (int j = i + 1; j < n; ++j) {
        T scalar = function.apply(p, q[j]);
        matrix.set(scalar, i, j);
        matrix.set(flip.apply(scalar), j, i);
      }
      ++i;
    }
    return matrix;
  }
}
