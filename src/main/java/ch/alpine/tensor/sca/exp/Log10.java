// code by jph
package ch.alpine.tensor.sca.exp;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** logarithm to base 10
 * 
 * <p>The implementation invokes java.lang.Math::log10
 * and is therefore more accurate than {@link Log#base(Scalar)}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Log10.html">Log10</a>
 * 
 * @see Log */
public enum Log10 implements ScalarUnaryOperator {
  FUNCTION;

  private static final double PI_LOG10 = 1.36437635384184134748578362543;
  private static final Scalar LOG10 = Log.FUNCTION.apply(RealScalar.of(10));

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof RealScalar) {
      double value = scalar.number().doubleValue();
      if (0 <= value)
        return DoubleScalar.of(Math.log10(value));
      return ComplexScalar.of(Math.log10(-value), PI_LOG10);
    }
    if (scalar instanceof ComplexScalar)
      return Log.FUNCTION.apply(scalar).divide(LOG10);
    throw TensorRuntimeException.of(scalar);
  }

  /** @param tensor
   * @return tensor with all scalars replaced with their logarithm */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
