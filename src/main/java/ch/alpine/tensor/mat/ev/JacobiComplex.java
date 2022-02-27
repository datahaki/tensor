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

/** Reference:
 * https://en.wikipedia.org/wiki/Jacobi_method_for_complex_Hermitian_matrices */
/* package */ class JacobiComplex extends JacobiMethod {
  /** @param matrix hermitian
   * @param chop */
  public JacobiComplex(Tensor matrix, Chop chop) {
    super(matrix);
    HermitianMatrixQ.require(matrix, chop);
    for (int iteration = 0; iteration < MAX_ITERATIONS; ++iteration) {
      for (int p = 0; p < n; ++p)
        for (int q = 0; q < n; ++q)
          if (p != q) {
            Scalar hpp = H[p][p];
            Scalar hpq = H[p][q];
            Scalar hqq = H[q][q];
            if (Scalars.nonZero(hpq)) {
              Scalar abs = Abs.FUNCTION.apply(hpq);
              Scalar phi1 = Arg.FUNCTION.apply(hpq);
              Scalar phi2 = ArcTan.of(hpp.subtract(hqq), abs.add(abs));
              Scalar theta1 = phi1.subtract(Pi.HALF).multiply(RationalScalar.HALF);
              Scalar theta2 = phi2.multiply(RationalScalar.HALF);
              GivensComplex givensComplex = new GivensComplex(theta1, theta2);
              givensComplex.transform(H, p, q);
              givensComplex.dot(V, p, q);
            }
          }
      // TODO not the best stop criteria
      if (Chop._14.isZero(sumAbs_offDiagonal()))
        return;
    }
    throw TensorRuntimeException.of(matrix);
  }
}
