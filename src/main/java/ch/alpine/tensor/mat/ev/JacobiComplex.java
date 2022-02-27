// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.ArcTan;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Real;

/** Reference:
 * https://en.wikipedia.org/wiki/Jacobi_method_for_complex_Hermitian_matrices */
/* package */ class JacobiComplex extends JacobiMethod {
  /** @param matrix hermitian
   * @param chop */
  public JacobiComplex(Tensor matrix, Chop chop) {
    super(matrix);
    HermitianMatrixQ.require(matrix, chop);
    // remove any imaginary part on diagonal after check that hermitian numerical
    for (int p = 0; p < n; ++p)
      H[p][p] = Real.FUNCTION.apply(H[p][p]);
    // Scalar factor = DoubleScalar.of(0.2 / (n * n));
    // int phase1 = PHASE1[Math.min(n, PHASE1.length - 1)];
    for (int iteration = 0; iteration < MAX_ITERATIONS; ++iteration) {
      Scalar sum = sumAbs_offDiagonal();
      if (Chop._14.allZero(sum))
        return;
      // Scalar tresh = phase1 <= iteration //
      // ? sum.zero()
      // : sum.multiply(factor);
      for (int p = 0; p < n; ++p)
        for (int q = 0; q < n; ++q)
          if (p != q) {
            // Scalar off = H[p][q];
            // Scalar abs = Abs.FUNCTION.apply(off);
            // Scalar g = HUNDRED.multiply(abs);
            // if (phase1 < iteration && //
            // Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(diag(p)))) && //
            // Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(diag(q))))) {
            // H[p][q] = off.zero();
            // H[q][p] = off.zero();
            // } else //
            // if (Scalars.lessThan(tresh, abs)) {
            Scalar hpp = H[p][p];
            Scalar hpq = H[p][q];
            Scalar hqq = H[q][q];
            if (Scalars.nonZero(hpq)) {
              Scalar apq = Abs.FUNCTION.apply(hpq);
              Scalar phi1 = Arg.FUNCTION.apply(hpq);
              Scalar phi2 = ArcTan.of(hpp.subtract(hqq), apq.add(apq));
              Scalar theta1 = phi1.subtract(Pi.HALF).multiply(RationalScalar.HALF);
              Scalar theta2 = phi2.multiply(RationalScalar.HALF);
              GivensComplex givensComplex = new GivensComplex(theta1, theta2);
              givensComplex.transform(H, p, q);
              givensComplex.dot(V, p, q);
            }
          }
    }
    throw TensorRuntimeException.of(matrix);
  }
}
