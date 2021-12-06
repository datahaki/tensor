// code by jph
package ch.alpine.tensor.mat.re;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.ext.Integers;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/RowReduce.html">RowReduce</a>
 * 
 * @see LinearSolve */
public class RowReduce extends AbstractReduce {
  /** @param matrix
   * @return reduced row echelon form (also called row canonical form) of matrix
   * @throws Exception if input is not a non-empty rectangular matrix */
  public static Tensor of(Tensor matrix) {
    return of(matrix, Pivots.selection(matrix));
  }

  /** @param matrix
   * @return reduced row echelon form (also called row canonical form) of matrix */
  public static Tensor of(Tensor matrix, Pivot pivot) {
    return new RowReduce(matrix, pivot).solve();
  }

  // ---
  private RowReduce(Tensor matrix, Pivot pivot) {
    super(matrix, pivot);
  }

  private Tensor solve() {
    int m = Integers.requirePositiveOrZero(Stream.of(lhs) //
        .mapToInt(Tensor::length) //
        .max().getAsInt());
    for (int c0 = 0, j = 0; c0 < lhs.length && j < m; ++j) {
      pivot(c0, j);
      Scalar piv = lhs[ind(c0)].Get(j);
      if (Scalars.nonZero(piv)) {
        for (int c1 = 0; c1 < lhs.length; ++c1)
          if (c1 != c0) {
            Scalar fac = lhs[ind(c1)].Get(j).divide(piv).negate();
            Tensor mul = lhs[ind(c0)].multiply(fac);
            // lhs[ind(c1)] = lhs[ind(c1)].add(mul.map(Unprotect::zeroProject));
            lhs[ind(c1)] = lhs[ind(c1)].map(Unprotect::zeroProject).add(mul.map(Unprotect::zeroProject));
          }
        lhs[ind(c0)] = lhs[ind(c0)].divide(piv);
        ++c0;
      }
    }
    return Tensor.of(IntStream.range(0, lhs.length).map(this::ind).mapToObj(i -> lhs[i]));
  }
}
