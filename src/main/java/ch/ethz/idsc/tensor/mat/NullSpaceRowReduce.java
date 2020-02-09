// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Transpose;

/* package */ enum NullSpaceRowReduce {
  ;
  /** matrix . Transpose[vectors] == 0
   * 
   * @param matrix
   * @param identity
   * @return list of vectors that span the right nullspace of given matrix */
  public static Tensor of(Tensor matrix, Tensor identity) {
    final int n = matrix.length();
    final int m = identity.length();
    Tensor lhs = RowReduce.of(Join.of(1, Transpose.of(matrix), identity));
    int j = 0;
    int c0 = 0;
    while (c0 < n)
      if (Scalars.nonZero(lhs.Get(j, c0++))) // <- careful: c0 is modified
        ++j;
    return Tensor.of(lhs.extract(j, m).stream().map(row -> row.extract(n, n + m)));
  }
}
