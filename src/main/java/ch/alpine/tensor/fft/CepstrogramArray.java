// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Re;
import ch.alpine.tensor.sca.exp.Log;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/CepstrogramArray.html">CepstrogramArray</a> */
public enum CepstrogramArray implements SpectrogramArray {
  POWER(vector -> //
  Fourier.INVERSE.transform(Fourier.FORWARD.transform(vector)//
      .maps(AbsSquared.FUNCTION)//
      .maps(Log.FUNCTION)).maps(AbsSquared.FUNCTION)),
  REAL(vector -> //
  Fourier.INVERSE.transform(Fourier.FORWARD.transform(vector)//
      .maps(Abs.FUNCTION)//
      .maps(Log.FUNCTION)).maps(Re.FUNCTION)),
  REAL1(vector -> //
  Fourier.INVERSE.transform(Fourier.FORWARD.transform(vector)//
      .maps(Abs.FUNCTION)//
      .maps(RealScalar.of(1E-12)::add)//
      .maps(Log.FUNCTION))//
      .maps(Re.FUNCTION));

  private final SpectrogramArray spectrogramArray;

  CepstrogramArray(TensorUnaryOperator process) {
    spectrogramArray = SpectrogramArray.of(process);
  }

  @Override
  public Tensor half_abs(Tensor vector) {
    return spectrogramArray.half_abs(vector);
  }

  @Override
  public TensorUnaryOperator of(Scalar windowDuration, Scalar samplingFrequency, int offset, ScalarUnaryOperator window) {
    return spectrogramArray.of(windowDuration, samplingFrequency, offset, window);
  }

  @Override
  public TensorUnaryOperator of(Scalar windowDuration, Scalar samplingFrequency, ScalarUnaryOperator window) {
    return spectrogramArray.of(windowDuration, samplingFrequency, window);
  }

  @Override
  public Tensor apply(Tensor vector) {
    return spectrogramArray.apply(vector);
  }
}
