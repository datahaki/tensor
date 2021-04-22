// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ enum MatrixLog2 {
  ;
  private static final Scalar FOUR = RealScalar.of(4);
  private static final Scalar TWO = RealScalar.TWO;

  /** @param matrix of size 2 x 2
   * @return */
  public static Tensor of(Tensor matrix) {
    MatrixQ.requireSize(matrix, 2, 2);
    Scalar a = matrix.Get(0, 0);
    Scalar b = matrix.Get(0, 1);
    Scalar c = matrix.Get(1, 0);
    Scalar d = matrix.Get(1, 1);
    if (Scalars.isZero(b) && Scalars.isZero(c)) // diagonal matrix
      return DiagonalMatrix.of(Log.FUNCTION.apply(a), Log.FUNCTION.apply(d));
    Scalar ad = a.subtract(d);
    Scalar A = Sqrt.FUNCTION.apply(ad.multiply(ad).add(Times.of(b, c, FOUR)));
    Scalar s = a.add(A).add(d);
    Scalar p = a.subtract(A).add(d);
    Scalar q = A.add(d).subtract(a);
    Scalar t = a.add(A).subtract(d);
    Scalar log_p2 = Log.FUNCTION.apply(p.divide(TWO));
    Scalar log_s2 = Log.FUNCTION.apply(s.divide(TWO));
    Scalar r11 = log_p2.multiply(q).add(log_s2.multiply(t)).divide(TWO);
    Scalar r22 = log_p2.multiply(t).add(log_s2.multiply(q)).divide(TWO);
    Scalar dsp = Log.FUNCTION.apply(s).subtract(Log.FUNCTION.apply(p));
    Scalar r12 = dsp.multiply(b);
    Scalar r21 = dsp.multiply(c);
    return Tensors.matrix(new Scalar[][] { { r11, r12 }, { r21, r22 } }).divide(A);
  }
}
