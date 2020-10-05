// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Sign;

public enum QRSignOperators implements QRSignOperator {
  /** householder reflections with highest numerical stability */
  STABILITY {
    @Override // from QRSignOperator
    public Scalar sign(Scalar xk) {
      return Scalars.isZero(xk) //
          ? RealScalar.ONE
          : Sign.FUNCTION.apply(xk).negate();
    }

    @Override // from QRSignOperator
    public boolean isDetExact() {
      return true;
    }
  }, //
  /** householder reflections that aim to preserve orientation:
   * Sign[Det[matrix]] == Sign[Det[Q]]
   * 
   * <p>Attention: Function fails on general matrices!
   * Use function to correct small deviations in an "almost"-orthogonal matrix
   * 
   * <p>Careful: when using "preserveOrientation" {@link #det()} of the returned
   * instance of {@link QRDecomposition} gives the determinant of the matrix
   * only up to sign! */
  ORIENTATION {
    @Override // from QRSignOperator
    public Scalar sign(Scalar xk) {
      return Scalars.isZero(Imag.FUNCTION.apply(xk)) //
          ? ONE_NEGATE
          : STABILITY.sign(xk);
    }

    @Override // from QRSignOperator
    public boolean isDetExact() {
      return false;
    }
  };

  private static final Scalar ONE_NEGATE = RealScalar.ONE.negate();
}
