// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.Re;
import ch.alpine.tensor.sca.tri.ArcTan;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;

/** Reference:
 * https://en.wikipedia.org/wiki/Jacobi_method_for_complex_Hermitian_matrices */
/* package */ class JacobiComplex extends JacobiMethod {
  /** @param matrix hermitian */
  public static Eigensystem eigensystem(Tensor matrix) {
    JacobiMethod jacobiMethod = new JacobiComplex(matrix);
    jacobiMethod.solve();
    return jacobiMethod;
  }

  // ---
  @PackageTestAccess
  JacobiComplex(Tensor matrix) {
    super(matrix);
    // remove any imaginary part on diagonal after check that hermitian numerical
    for (int p = 0; p < n; ++p)
      H[p][p] = Re.FUNCTION.apply(H[p][p]);
  }

  @Override // from JacobiMethod
  protected void eliminate(int p, int q) {
    Scalar hpp = diag(p);
    Scalar hqq = diag(q);
    Scalar hpq = H[p][q];
    Scalar apq = Abs.FUNCTION.apply(hpq);
    Scalar phi1 = Arg.FUNCTION.apply(hpq);
    Scalar phi2 = ArcTan.of(hpp.subtract(hqq), apq.add(apq));
    Scalar theta1 = phi1.subtract(Pi.HALF).multiply(RationalScalar.HALF);
    Scalar theta2 = phi2.multiply(RationalScalar.HALF);
    GivensRotation givensRotation = new GivensComplex(theta1, theta2);
    givensRotation.transform(p, q);
    givensRotation.dot(p, q);
  }

  /** encodes the product of two complex rotation matrices
   * 
   * Reference:
   * https://en.wikipedia.org/wiki/Jacobi_method_for_complex_Hermitian_matrices */
  /* package */ class GivensComplex implements GivensRotation {
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
    GivensComplex(Scalar theta1, Scalar theta2) {
      Chop.NONE.requireZero(Im.FUNCTION.apply(theta1));
      Chop.NONE.requireZero(Im.FUNCTION.apply(theta2));
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

    @Override // from GivensRotation
    public void transform(int p, int q) {
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
      H[p][p] = Re.FUNCTION.apply(H[p][p]);
      H[q][q] = Re.FUNCTION.apply(H[q][q]);
    }

    @Override // from GivensRotation
    public void dot(int p, int q) {
      Tensor vp = V[p];
      Tensor vq = V[q];
      V[p] = vp.multiply(rpp).add(vq.multiply(rpq));
      V[q] = vp.multiply(rqp).add(vq.multiply(rqq));
    }
  }
}
