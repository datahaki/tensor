// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Cos;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.Sin;

class GivensComplexInv {
  final Scalar rpp;
  final Scalar rpq;
  final Scalar rqp;
  final Scalar rqq;

  public GivensComplexInv(Scalar theta1, Scalar theta2) {
    Chop.NONE.requireZero(Imag.FUNCTION.apply(theta1));
    Chop.NONE.requireZero(Imag.FUNCTION.apply(theta2));
    Scalar cos = Cos.FUNCTION.apply(theta2);
    Scalar sin = Sin.FUNCTION.apply(theta2);
    rpp = ComplexScalar.unit(theta1).multiply(ComplexScalar.I).multiply(sin);
    rpq = ComplexScalar.unit(theta1).multiply(cos);
    rqp = ComplexScalar.unit(theta1.negate()).negate().multiply(cos);
    rqq = ComplexScalar.unit(theta1.negate()).multiply(ComplexScalar.I).negate().multiply(sin);
  }
}
