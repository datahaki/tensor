package ch.alpine.tensor.sca.exp;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.Tanh;

public enum Gelu implements ScalarUnaryOperator {
  FUNCTION;

  static final Scalar f = DoubleScalar.of(0.044715);
  static final Scalar sca = Sqrt.FUNCTION.apply(RealScalar.TWO.divide(Pi.VALUE));

  @Override
  public Scalar apply(Scalar x) {
    Scalar x3 = x.multiply(x).multiply(x);
    Scalar u = f.multiply(x3).add(x).multiply(sca);
    return Tanh.FUNCTION.apply(u).add(RealScalar.ONE).multiply(Rational.HALF).multiply(x);
  }

  public static Scalar dx(Scalar x) {
    Scalar x3 = x.multiply(x).multiply(x);
    Scalar u = f.multiply(x3).add(x).multiply(sca);
    Scalar tanhu = Tanh.FUNCTION.apply(u);
    Scalar t1 = RealScalar.ONE.add(tanhu).multiply(Rational.HALF);
    Scalar t2 = RealScalar.ONE.subtract(tanhu.multiply(tanhu)).multiply(Rational.HALF).multiply(x);
    Scalar fac = sca.multiply(RealScalar.ONE.add(RealScalar.of(3).multiply(f.multiply(x.multiply(x)))));
    return t1.add(t2.multiply(fac));
  }
}
