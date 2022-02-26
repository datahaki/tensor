// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Imag;

record Givens2(int n, Scalar theta1, Scalar theta2, int p, int q) {
  public Tensor matrix() {
    Chop.NONE.requireZero(Imag.FUNCTION.apply(theta1));
    Chop.NONE.requireZero(Imag.FUNCTION.apply(theta2));
    Tensor tensor = IdentityMatrix.of(n);
    GivensComplex givensComplex = new GivensComplex(theta1, theta2);
    tensor.set(givensComplex.rpp, p, p);
    tensor.set(givensComplex.rpq, p, q);
    tensor.set(givensComplex.rqp, q, p);
    tensor.set(givensComplex.rqq, q, q);
    return tensor;
  }

  public Tensor inverse() {
    Chop.NONE.requireZero(Imag.FUNCTION.apply(theta1));
    Chop.NONE.requireZero(Imag.FUNCTION.apply(theta2));
    Tensor tensor = IdentityMatrix.of(n);
    GivensComplexInv givensComplex = new GivensComplexInv(theta1, theta2);
    tensor.set(givensComplex.rpp, p, p);
    tensor.set(givensComplex.rpq, p, q);
    tensor.set(givensComplex.rqp, q, p);
    tensor.set(givensComplex.rqq, q, q);
    return tensor;
  }
}
