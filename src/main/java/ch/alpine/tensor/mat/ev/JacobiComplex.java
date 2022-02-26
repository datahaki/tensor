// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.ArcTan;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Real;
import ch.alpine.tensor.sca.Sign;

/** https://en.wikipedia.org/wiki/Jacobi_method_for_complex_Hermitian_matrices */
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
            Scalar abs = Abs.FUNCTION.apply(hpq);
            if (Sign.isPositive(abs)) {
              Scalar phi1 = Arg.FUNCTION.apply(hpq);
              Scalar phi2 = ArcTan.of(hpp.subtract(hqq), abs.add(abs));
              Scalar theta1 = phi1.subtract(Pi.HALF).multiply(RationalScalar.HALF);
              Scalar theta2 = phi2.multiply(RationalScalar.HALF);
              GivensComplex givensComplex = new GivensComplex(theta1, theta2);
              {
                for (int i = 0; i < n; ++i) {
                  Scalar hpi = H[p][i];
                  Scalar hqi = H[q][i];
                  H[p][i] = hpi.multiply(givensComplex.rpp).add(hqi.multiply(givensComplex.rpq));
                  H[q][i] = hpi.multiply(givensComplex.rqp).add(hqi.multiply(givensComplex.rqq));
                }
                for (int i = 0; i < n; ++i) {
                  Scalar hip = H[i][p];
                  Scalar hiq = H[i][q];
                  H[i][p] = hip.multiply(givensComplex.cpp).add(hiq.multiply(givensComplex.cpq));
                  H[i][q] = hip.multiply(givensComplex.cqp).add(hiq.multiply(givensComplex.cqq));
                }
              }
              H[p][p] = Real.FUNCTION.apply(H[p][p]);
              H[q][q] = Real.FUNCTION.apply(H[q][q]);
              { // update V
                Tensor vp = V.get(p);
                Tensor vq = V.get(q);
                V.set(vp.multiply(givensComplex.rpp).add(vq.multiply(givensComplex.rpq)), p);
                V.set(vp.multiply(givensComplex.rqp).add(vq.multiply(givensComplex.rqq)), q);
              }
            }
          }
      if (Chop._14.isZero(sumAbs_offDiagonal()))
        return;
    }
    throw TensorRuntimeException.of(matrix);
  }
}
