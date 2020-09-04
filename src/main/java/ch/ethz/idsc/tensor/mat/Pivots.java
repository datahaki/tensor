// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Abs;

public enum Pivots implements Pivot {
  /** selects entry with largest absolute value
   * 
   * in order to compute the inverse of matrices with mixed unit, for instance:
   * {{1[m^2], 6[m*rad]}, {6[m*rad], 16[rad^2]}}
   * the pivot is computer over the absolute numeric value of the columns */
  ARGMAX_ABS() {
    @Override // from Pivot
    public int get(int row, int col, int[] ind, Tensor[] lhs) {
      Scalar max = value(Abs.FUNCTION.apply(lhs[ind[row]].Get(col)));
      int arg = row;
      for (int i = row + 1; i < ind.length; ++i) {
        Scalar cmp = Abs.FUNCTION.apply(value(lhs[ind[i]].Get(col)));
        if (Scalars.lessThan(max, cmp)) {
          max = cmp;
          arg = i;
        }
      }
      return arg;
    }
  }, //
  /** picks the first non-zero element in the column as pivot
   * the return value is c0 in the case when the element at (ind[c0], j)
   * is non-zero, but also if none of the candidates is non-zero */
  FIRST_NON_ZERO() {
    @Override // from Pivot
    public int get(int row, int col, int[] ind, Tensor[] lhs) {
      return IntStream.range(row, ind.length) //
          .filter(c1 -> Scalars.nonZero(lhs[ind[c1]].Get(col))) //
          .findFirst().orElse(row);
    }
  };

  private static Scalar value(Scalar scalar) {
    return scalar instanceof Quantity //
        ? ((Quantity) scalar).value()
        : scalar;
  }
}
