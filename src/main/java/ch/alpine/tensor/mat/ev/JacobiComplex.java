// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.ArcTan;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Real;

/** Reference:
 * https://en.wikipedia.org/wiki/Jacobi_method_for_complex_Hermitian_matrices */
/* package */ class JacobiComplex extends JacobiMethod {
  /** @param matrix hermitian */
  public JacobiComplex(Tensor matrix) {
    super(matrix);
  }

  @Override
  protected void init() {
    // remove any imaginary part on diagonal after check that hermitian numerical
    for (int p = 0; p < n; ++p)
      H[p][p] = Real.FUNCTION.apply(H[p][p]);
  }

  @Override // from JacobiMethod
  protected void run(int p, int q, Scalar apq) {
    Scalar hpp = diag(p);
    Scalar hqq = diag(q);
    Scalar hpq = H[p][q];
    Scalar phi1 = Arg.FUNCTION.apply(hpq);
    Scalar phi2 = ArcTan.of(hpp.subtract(hqq), apq.add(apq));
    Scalar theta1 = phi1.subtract(Pi.HALF).multiply(RationalScalar.HALF);
    Scalar theta2 = phi2.multiply(RationalScalar.HALF);
    GivensComplex givensComplex = new GivensComplex(theta1, theta2);
    givensComplex.transform(H, p, q);
    givensComplex.dot(V, p, q);
  }
}
