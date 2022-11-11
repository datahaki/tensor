// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Re;
import ch.alpine.tensor.sca.exp.Log;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/CepstrumArray.html">CepstrumArray</a> */
public enum CepstrumArray implements TensorUnaryOperator {
  POWER {
    @Override
    public Tensor apply(Tensor vector) {
      return InverseFourier.of(Fourier.of(vector).map(AbsSquared.FUNCTION).map(Log.FUNCTION)).map(AbsSquared.FUNCTION);
    }
  },
  REAL {
    @Override
    public Tensor apply(Tensor vector) {
      return InverseFourier.of(Fourier.of(vector).map(Abs.FUNCTION).map(Log.FUNCTION)).map(Re.FUNCTION);
    }
  }
}
