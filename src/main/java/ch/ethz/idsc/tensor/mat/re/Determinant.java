// code by jph
package ch.ethz.idsc.tensor.mat.re;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class Determinant extends AbstractReduce {
  /** @param matrix square possibly non-invertible
   * @param pivot
   * @return determinant of given matrix */
  public static Scalar of(Tensor matrix, Pivot pivot) {
    return new Determinant(matrix, pivot).override_det();
  }

  /***************************************************/
  private Determinant(Tensor matrix, Pivot pivot) {
    super(matrix, pivot);
  }

  /** eliminates rows using given pivot and aborts if matrix is degenerate,
   * in which case the zero pivot element is returned.
   * 
   * @return determinant of given matrix */
  private Scalar override_det() {
    for (int c0 = 0; c0 < lhs.length; ++c0) {
      pivot(c0, c0);
      Scalar piv = lhs[ind[c0]].Get(c0);
      if (Scalars.isZero(piv))
        return piv;
      eliminate(c0, piv);
    }
    return det();
  }

  private void eliminate(int c0, Scalar piv) {
    for (int c1 = c0 + 1; c1 < lhs.length; ++c1) {
      int ic1 = ind[c1];
      Scalar fac = lhs[ic1].Get(c0).divide(piv).negate();
      lhs[ic1] = lhs[ic1].add(lhs[ind[c0]].multiply(fac));
    }
  }
}
