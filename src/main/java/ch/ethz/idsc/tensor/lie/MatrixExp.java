// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.MatrixPower;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** Reference:
 * "The Scaling and Squaring Method for the Matrix Exponential Revisited"
 * by Nick Higham, 2004
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MatrixExp.html">MatrixExp</a> */
public enum MatrixExp {
  ;
  private static final ScalarUnaryOperator LOG2 = Log.base(2);

  /** @param matrix square
   * @return exponential of given matrix exp(m) = I + m + m^2/2 + m^3/6 + ...
   * @throws Exception if given matrix is not a square matrix */
  public static Tensor of(Tensor matrix) {
    // LONGTERM the infinity norm is recommended
    Scalar max = RealScalar.of(matrix.flatten(-1) //
        .map(Scalar.class::cast) //
        .map(Scalar::abs) //
        .map(Scalar::number) //
        .mapToDouble(Number::doubleValue) //
        .reduce(Math::max).getAsDouble() + 1);
    long exponent = 1 << Ceiling.FUNCTION.apply(LOG2.apply(max)).number().longValue();
    return MatrixPower.of(MatrixExpSeries.of(matrix.multiply(RationalScalar.of(1, exponent))), exponent);
  }
}
