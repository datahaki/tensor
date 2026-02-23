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

public enum SpectrogramArrays implements SpectrogramArray {
  /** reproduces
   * <a href="https://reference.wolfram.com/language/ref/SpectrogramArray.html">SpectrogramArray</a> */
  FOURIER(Fourier.FORWARD::transform),
  /** inspired by
   * <a href="https://reference.wolfram.com/language/ref/CepstrogramArray.html">CepstrogramArray</a> */
  POWER(vector -> //
  Fourier.INVERSE.transform(Fourier.FORWARD.transform(vector)//
      .maps(AbsSquared.FUNCTION)//
      .maps(Log.FUNCTION)).maps(AbsSquared.FUNCTION)),
  /** inspired by
   * <a href="https://reference.wolfram.com/language/ref/CepstrogramArray.html">CepstrogramArray</a> */
  REAL(vector -> //
  Fourier.INVERSE.transform(Fourier.FORWARD.transform(vector)//
      .maps(Abs.FUNCTION)//
      .maps(Log.FUNCTION)).maps(Re.FUNCTION)),
  /** inspired by
   * <a href="https://reference.wolfram.com/language/ref/CepstrogramArray.html">CepstrogramArray</a> */
  REAL1(vector -> //
  Fourier.INVERSE.transform(Fourier.FORWARD.transform(vector)//
      .maps(Abs.FUNCTION)//
      .maps(RealScalar.of(1E-12)::add)//
      .maps(Log.FUNCTION))//
      .maps(Re.FUNCTION));

  private final SpectrogramArray spectrogramArray;

  SpectrogramArrays(TensorUnaryOperator process) {
    spectrogramArray = SpectrogramArray.of(process);
  }

  public SpectrogramArray operator() {
    return spectrogramArray;
  }

  @Override
  public Tensor apply(Tensor vector) {
    return spectrogramArray.apply(vector);
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
}
