// code by jph
package ch.alpine.tensor.sca.exp;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Sign;

/** The logistic function 1 / (1 + Exp[-z]) is a solution to the differential equation y' == y * (1 - y)
 * 
 * <p>The taylor series is
 * LogisticSigmoid[x] == 1/2 + x/4 - x^3/48 + x^5/480 + ...
 * 
 * <p>The derivative is
 * Exp[x] / ((1 + Exp[x])^2)
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/LogisticSigmoid.html">LogisticSigmoid</a> */
public enum LogisticSigmoid implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (Sign.isPositiveOrZero(scalar)) {
      Scalar z = Exp.FUNCTION.apply(scalar.negate());
      return RealScalar.ONE.add(z).reciprocal();
    } else {
      Scalar z = Exp.FUNCTION.apply(scalar);
      return RealScalar.ONE.add(z).under(z);
    }
  }
}
