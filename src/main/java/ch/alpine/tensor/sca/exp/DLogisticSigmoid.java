// code by jph
package ch.alpine.tensor.sca.exp;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

public enum DLogisticSigmoid implements ScalarUnaryOperator {
  FUNCTION {
    @Override
    public Scalar apply(Scalar scalar) {
      // y = sigma(x) * (1 - sigma(x))
      Scalar exp = Exp.FUNCTION.apply(scalar); // Exp[x]
      Scalar den = RealScalar.ONE.add(exp); // 1+Exp[x]
      return exp.divide(den.multiply(den)); // Exp[x] / (1+Exp[x])^2
    }
  },
  /** quadratic polynomial with zeros at 0 and 1 */
  NESTED {
    @Override
    public Scalar apply(Scalar y) { // here y = sigma(x)
      return y.multiply(RealScalar.ONE.subtract(y)); // x * (1 - x)
    }
  }
}
