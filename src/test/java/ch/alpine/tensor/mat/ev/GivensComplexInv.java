// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.Complex;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;

class GivensComplexInv {
  final Scalar rpp;
  final Scalar rpq;
  final Scalar rqp;
  final Scalar rqq;

  public GivensComplexInv(Scalar theta1, Scalar theta2) {
    Chop.NONE.requireZero(Im.FUNCTION.apply(theta1));
    Chop.NONE.requireZero(Im.FUNCTION.apply(theta2));
    Scalar cos = Cos.FUNCTION.apply(theta2);
    Scalar sin = Sin.FUNCTION.apply(theta2);
    rpp = Complex.unit(theta1).multiply(Complex.I).multiply(sin);
    rpq = Complex.unit(theta1).multiply(cos);
    rqp = Complex.unit(theta1.negate()).negate().multiply(cos);
    rqq = Complex.unit(theta1.negate()).multiply(Complex.I).negate().multiply(sin);
  }
}
