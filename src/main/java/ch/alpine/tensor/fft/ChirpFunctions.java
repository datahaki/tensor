// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.ply.Polynomial;
import ch.alpine.tensor.sca.tri.Cos;

public enum ChirpFunctions {
  ;
  public static ScalarUnaryOperator linear(Scalar f0, Scalar k) {
    return Polynomial.of(Tensors.of(f0, k)).times(Pi.TWO).antiderivative() //
        .andThen(Cos.FUNCTION);
  }

  public static ScalarUnaryOperator quadratic(Scalar f0, Scalar k) {
    return Polynomial.of(Tensors.of(f0, f0.zero(), k)).times(Pi.TWO).antiderivative() //
        .andThen(Cos.FUNCTION);
  }

  public static ScalarUnaryOperator exponential(Scalar f0, Scalar alpha) {
    Scalar factor = Pi.TWO.multiply(f0).divide(alpha);
    ScalarUnaryOperator f = t -> Exp.FUNCTION.apply(alpha.multiply(t)).subtract(RealScalar.ONE).multiply(factor);
    return f.andThen(Cos.FUNCTION);
  }
}
