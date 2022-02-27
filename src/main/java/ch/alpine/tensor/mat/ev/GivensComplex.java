// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.Cos;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.Real;
import ch.alpine.tensor.sca.Sin;

/** encodes the product of two complex rotation matrices
 * 
 * Reference:
 * https://en.wikipedia.org/wiki/Jacobi_method_for_complex_Hermitian_matrices */
/* package */ class GivensComplex {
  final Scalar rpp;
  final Scalar rpq;
  final Scalar rqp;
  final Scalar rqq;
  final Scalar cpp;
  final Scalar cpq;
  final Scalar cqp;
  final Scalar cqq;

  /** @param theta1
   * @param theta2 */
  public GivensComplex(Scalar theta1, Scalar theta2) {
    Chop.NONE.requireZero(Imag.FUNCTION.apply(theta1));
    Chop.NONE.requireZero(Imag.FUNCTION.apply(theta2));
    Scalar cos = Cos.FUNCTION.apply(theta2);
    Scalar sin = Sin.FUNCTION.apply(theta2);
    rpp = ComplexScalar.unit(theta1.negate()).multiply(ComplexScalar.I).negate().multiply(sin);
    rpq = ComplexScalar.unit(theta1).negate().multiply(cos);
    rqp = ComplexScalar.unit(theta1.negate()).multiply(cos);
    rqq = ComplexScalar.unit(theta1).multiply(ComplexScalar.I).multiply(sin);
    cpp = Conjugate.of(rpp);
    cpq = Conjugate.of(rpq);
    cqp = Conjugate.of(rqp);
    cqq = Conjugate.of(rqq);
  }

  public void transform(Scalar[][] H, int p, int q) {
    int n = H.length;
    for (int i = 0; i < n; ++i) {
      Scalar hpi = H[p][i];
      Scalar hqi = H[q][i];
      H[p][i] = hpi.multiply(rpp).add(hqi.multiply(rpq));
      H[q][i] = hpi.multiply(rqp).add(hqi.multiply(rqq));
    }
    for (int i = 0; i < n; ++i) {
      Scalar hip = H[i][p];
      Scalar hiq = H[i][q];
      H[i][p] = hip.multiply(cpp).add(hiq.multiply(cpq));
      H[i][q] = hip.multiply(cqp).add(hiq.multiply(cqq));
    }
    // Tolerance.CHOP.requireZero(H[p][q]); // dev check
    // Tolerance.CHOP.requireZero(H[q][p]); // dev check
    H[p][q] = H[p][q].zero();
    H[q][p] = H[q][p].zero();
    // Tolerance.CHOP.requireZero(Imag.FUNCTION.apply(H[p][p])); // dev check
    // Tolerance.CHOP.requireZero(Imag.FUNCTION.apply(H[q][q])); // dev check
    H[p][p] = Real.FUNCTION.apply(H[p][p]);
    H[q][q] = Real.FUNCTION.apply(H[q][q]);
  }

  public void dot(Tensor[] V, int p, int q) {
    Tensor vp = V[p];
    Tensor vq = V[q];
    V[p] = vp.multiply(rpp).add(vq.multiply(rpq));
    V[q] = vp.multiply(rqp).add(vq.multiply(rqq));
  }
}
