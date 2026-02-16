// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Re;
import ch.alpine.tensor.sca.exp.Log;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/CepstrogramArray.html">CepstrogramArray</a> */
public enum CepstrogramArray {
  ;
  public static final SpectrogramArray POWER = SpectrogramArray.of(vector -> //
  Fourier.INVERSE.transform(Fourier.FORWARD.transform(vector)//
      .maps(AbsSquared.FUNCTION)//
      .maps(Log.FUNCTION)).maps(AbsSquared.FUNCTION));
  public static final SpectrogramArray REAL = SpectrogramArray.of(vector -> //
  Fourier.INVERSE.transform(Fourier.FORWARD.transform(vector)//
      .maps(Abs.FUNCTION)//
      .maps(Log.FUNCTION)).maps(Re.FUNCTION));
  public static final SpectrogramArray REAL1 = SpectrogramArray.of(vector -> //
  Fourier.INVERSE.transform(Fourier.FORWARD.transform(vector)//
      .maps(Abs.FUNCTION)//
      .maps(RealScalar.of(1E-12)::add)//
      .maps(Log.FUNCTION))//
      .maps(Re.FUNCTION));
}
