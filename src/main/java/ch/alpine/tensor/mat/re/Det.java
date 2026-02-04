// code by jph
package ch.alpine.tensor.mat.re;

import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.ext.Int;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.lie.Permutations;
import ch.alpine.tensor.lie.Signature;
import ch.alpine.tensor.mat.SquareMatrixQ;

/** implementation is consistent with Mathematica
 * 
 * <p>The determinant of an empty matrix Det[{{}}] throws an exception
 * just as <code>Mathematica::Det[{{}}]</code> results in Exception
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Det.html">Det</a> */
public class Det extends AbstractReduce {
  /** @param matrix square
   * @return determinant of matrix
   * @throws Exception if matrix is not square */
  public static Scalar of(Tensor matrix) {
    return of(matrix, Pivots.selection(matrix));
  }

  /** @param matrix square
   * @param pivot
   * @return determinant of matrix
   * @throws Exception if matrix is not square */
  public static Scalar of(Tensor matrix, Pivot pivot) {
    return new Det(SquareMatrixQ.INSTANCE.requireMember(matrix), pivot).override_det();
  }

  /** Careful: do not call function for large matrix
   * Hint: function exists for testing of small matrices
   * 
   * @param matrix
   * @return */
  public static Scalar withoutDivision(Tensor matrix) {
    int n = SquareMatrixQ.INSTANCE.requireMember(matrix).length();
    return Permutations.stream(Range.of(0, n)).map(sigma -> {
      Int i = new Int();
      int[] index = Primitives.toIntArray(sigma);
      return matrix.stream() //
          .map(row -> row.Get(index[i.getAndIncrement()])) //
          .reduce(Scalar::multiply) //
          .map(Signature.of(sigma)::multiply) //
          .orElseThrow();
    }).reduce(Scalar::add).orElse(RealScalar.ZERO);
  }

  // ---
  /** @param matrix square possibly non-invertible
   * @param pivot
   * @return determinant of given matrix */
  private Det(Tensor matrix, Pivot pivot) {
    super(matrix, pivot);
  }

  /** eliminates rows using given pivot and aborts if matrix is degenerate,
   * in which case the zero pivot element is returned.
   * 
   * @return determinant of given matrix */
  private Scalar override_det() {
    for (int c0 = 0; c0 < lhs.length; ++c0) {
      pivot(c0, c0);
      Scalar piv = lhs[ind(c0)].Get(c0);
      if (Scalars.isZero(piv))
        return IntStream.range(0, lhs.length) //
            .mapToObj(i -> lhs[i].Get(i)) //
            .map(Scalar::zero) //
            .reduce(Scalar::multiply) //
            .orElseThrow();
      eliminate(c0, piv);
    }
    return det();
  }

  private void eliminate(int c0, Scalar piv) {
    for (int c1 = c0 + 1; c1 < lhs.length; ++c1) {
      int ic1 = ind(c1);
      Scalar fac = lhs[ic1].Get(c0).divide(piv).negate();
      lhs[ic1] = lhs[ic1].add(lhs[ind(c0)].multiply(fac));
    }
  }
}
