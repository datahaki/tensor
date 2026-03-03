// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Re;
import ch.alpine.tensor.sca.exp.Log;

public enum SpectrogramArrays {
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

  private final TensorUnaryOperator process;

  SpectrogramArrays(TensorUnaryOperator process) {
    this.process = process;
  }

  public SpectrogramArray operator() {
    return SpectrogramArray.of(process);
  }
}
