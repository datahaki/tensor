// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.ev.JacobiComplex.GivensComplex;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Im;

class Givens2 {
  private final int n;
  private final Scalar theta1;
  private final Scalar theta2;
  private final int p;
  private final int q;

  Givens2(int n, Scalar theta1, Scalar theta2, int p, int q) {
    this.n = n;
    this.theta1 = theta1;
    this.theta2 = theta2;
    this.p = p;
    this.q = q;
  }

  public Tensor matrix() {
    Chop.NONE.requireZero(Im.FUNCTION.apply(theta1));
    Chop.NONE.requireZero(Im.FUNCTION.apply(theta2));
    Tensor tensor = IdentityMatrix.of(n);
    GivensComplex givensComplex = new JacobiComplex(tensor).new GivensComplex(theta1, theta2);
    tensor.set(givensComplex.rpp, p, p);
    tensor.set(givensComplex.rpq, p, q);
    tensor.set(givensComplex.rqp, q, p);
    tensor.set(givensComplex.rqq, q, q);
    return tensor;
  }

  public Tensor inverse() {
    Chop.NONE.requireZero(Im.FUNCTION.apply(theta1));
    Chop.NONE.requireZero(Im.FUNCTION.apply(theta2));
    Tensor tensor = IdentityMatrix.of(n);
    GivensComplexInv givensComplex = new GivensComplexInv(theta1, theta2);
    tensor.set(givensComplex.rpp, p, p);
    tensor.set(givensComplex.rpq, p, q);
    tensor.set(givensComplex.rqp, q, p);
    tensor.set(givensComplex.rqq, q, q);
    return tensor;
  }
}
