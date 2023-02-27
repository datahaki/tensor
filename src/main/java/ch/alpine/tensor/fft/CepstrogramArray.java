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
  public static final SpectrogramArray Power = new SpectrogramArray(vector -> {
    return Fourier.INVERSE.transform(Fourier.FORWARD.transform(vector) //
        .map(AbsSquared.FUNCTION) //
        .map(Log.FUNCTION)).map(AbsSquared.FUNCTION);
  });
  public static final SpectrogramArray Real = new SpectrogramArray(vector -> {
    return Fourier.INVERSE.transform(Fourier.FORWARD.transform(vector) //
        .map(Abs.FUNCTION) //
        .map(Log.FUNCTION)).map(Re.FUNCTION);
  });
  public static final SpectrogramArray Real1 = new SpectrogramArray(vector -> {
    return Fourier.INVERSE.transform(Fourier.FORWARD.transform(vector) //
        .map(Abs.FUNCTION) //
        .map(RealScalar.of(1E-12)::add) //
        .map(Log.FUNCTION)) //
        .map(Re.FUNCTION);
  });

}
