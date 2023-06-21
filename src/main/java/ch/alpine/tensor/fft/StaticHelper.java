// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.Re;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.pow.Sqrt;

/* package */ enum StaticHelper {
  ;
  private static final ScalarUnaryOperator LOG2 = Log.base(2);

  /** @param vector_length
   * @return power of 2 */
  public static int default_windowLength(int vector_length) {
    int num = Round.intValueExact(LOG2.apply(Sqrt.FUNCTION.apply(RealScalar.of(vector_length))));
    return 1 << (num + 1);
  }

  // helper function
  public static int windowLength(Scalar windowDuration, Scalar samplingFrequency) {
    return Round.intValueExact(windowDuration.multiply(samplingFrequency));
  }

  /** @param length
   * @param window
   * @return symmetric vector of given length of weights that sum up to length */
  public static Tensor weights(int length, ScalarUnaryOperator window) {
    Tensor samples = 1 == length //
        ? Tensors.vector(0)
        : samples(length);
    Tensor weights = samples.map(window);
    return weights.multiply(RealScalar.of(length).divide(Total.ofVector(weights)));
  }

  /** @param length
   * @return vector of given length */
  public static Tensor samples(int length) {
    return Subdivide.increasing( //
        Clips.absolute(RationalScalar.HALF.add(RationalScalar.of(-1, 2 * length))), //
        length - 1);
  }

  // helper function
  static int default_offset(int windowLength) {
    return Round.intValueExact(RationalScalar.of(windowLength, 3));
  }

  /** function truncates to real output depending on all-real input
   * 
   * @param vector input
   * @param result output
   * @return result output with imaginary part projected to zero, if input is all-real */
  public static Tensor re_re(Tensor vector, Tensor result) {
    // TODO TENSOR this is redundant to some other place
    return Chop.NONE.allZero(vector.map(Im.FUNCTION)) //
        ? result.map(Re.FUNCTION)
        : result;
  }
}
