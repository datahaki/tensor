// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Cos;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.Sin;

record Givens2(int n, Scalar theta1, Scalar theta2, int p, int q) {
  public Tensor matrix() {
    Chop.NONE.requireZero(Imag.FUNCTION.apply(theta1));
    Chop.NONE.requireZero(Imag.FUNCTION.apply(theta2));
    Tensor tensor = IdentityMatrix.of(n);
    Scalar cos = Cos.FUNCTION.apply(theta2);
    Scalar sin = Sin.FUNCTION.apply(theta2);
    Scalar rpp = ComplexScalar.unit(theta1.negate()).multiply(ComplexScalar.I).negate();
    Scalar rpq = ComplexScalar.unit(theta1).negate();
    Scalar rqp = ComplexScalar.unit(theta1.negate());
    Scalar rqq = ComplexScalar.unit(theta1).multiply(ComplexScalar.I);
    tensor.set(rpp.multiply(sin), p, p);
    tensor.set(rpq.multiply(cos), p, q);
    tensor.set(rqp.multiply(cos), q, p);
    tensor.set(rqq.multiply(sin), q, q);
    return tensor;
  }
}
