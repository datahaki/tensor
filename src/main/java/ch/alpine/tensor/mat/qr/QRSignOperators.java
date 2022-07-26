// code by jph
package ch.alpine.tensor.mat.qr;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.Sign;

public enum QRSignOperators implements QRSignOperator {
  /** householder reflections with highest numerical stability */
  STABILITY {
    @Override // from QRSignOperator
    public Scalar sign(Scalar xk) {
      return Scalars.isZero(xk) //
          ? ONE_NEGATE
          : Sign.FUNCTION.apply(xk);
    }

    @Override // from QRSignOperator
    public boolean isDetExact() {
      return true;
    }
  },
  /** householder reflections that aim to preserve orientation:
   * Sign[Det[matrix]] == Sign[Det[Q]]
   * 
   * <p>Attention: Function fails on general matrices!
   * Use function to correct small deviations in an "almost"-orthogonal matrix
   * 
   * <p>Careful: when using "preserveOrientation" {@link QRDecomposition#det()}
   * gives the determinant of the matrix only up to sign! */
  ORIENTATION {
    @Override // from QRSignOperator
    public Scalar sign(Scalar xk) {
      return Scalars.isZero(Imag.FUNCTION.apply(xk)) //
          ? RealScalar.ONE
          : STABILITY.sign(xk).negate();
    }

    @Override // from QRSignOperator
    public boolean isDetExact() {
      return false;
    }
  };

  private static final Scalar ONE_NEGATE = RealScalar.ONE.negate();
}
