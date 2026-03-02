// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarBinaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.pow.Sqrt;

public enum ArithmeticGeometricMean implements ScalarBinaryOperator {
  INSTANCE;

  @Override
  public Scalar apply(Scalar a, Scalar b) {
    while (!Tolerance.CHOP.isClose(a, b)) {
      Scalar nextA = a.add(b).multiply(Rational.HALF);
      Scalar nextB = Sqrt.FUNCTION.apply(a.multiply(b));
      a = nextA;
      b = nextB;
    }
    return a;
  }
}
