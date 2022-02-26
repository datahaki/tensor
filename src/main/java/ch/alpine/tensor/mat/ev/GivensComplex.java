// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.Cos;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.Sin;

/* package */ class GivensComplex {
  final Scalar rpp;
  final Scalar rpq;
  final Scalar rqp;
  final Scalar rqq;
  final Scalar cpp;
  final Scalar cpq;
  final Scalar cqp;
  final Scalar cqq;

  public GivensComplex(Scalar theta1, Scalar theta2) {
    Chop.NONE.requireZero(Imag.FUNCTION.apply(theta1));
    Chop.NONE.requireZero(Imag.FUNCTION.apply(theta2));
    Scalar cos = Cos.FUNCTION.apply(theta2);
    Scalar sin = Sin.FUNCTION.apply(theta2);
    rpp = ComplexScalar.unit(theta1.negate()).multiply(ComplexScalar.I).negate().multiply(sin);
    rpq = ComplexScalar.unit(theta1).negate().multiply(cos);
    rqp = ComplexScalar.unit(theta1.negate()).multiply(cos);
    rqq = ComplexScalar.unit(theta1).multiply(ComplexScalar.I).multiply(sin);
    cpp = Conjugate.of(rpp);
    cpq = Conjugate.of(rpq);
    cqp = Conjugate.of(rqp);
    cqq = Conjugate.of(rqq);
  }
}
