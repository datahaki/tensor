// code by jph
package ch.alpine.tensor.sca.exp;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

public enum DLogisticSigmoid implements ScalarUnaryOperator {
  FUNCTION {
    @Override
    public Scalar apply(Scalar scalar) {
      Scalar exp = Exp.FUNCTION.apply(scalar); // Exp[x]
      Scalar den = RealScalar.ONE.add(exp); // 1+Exp[x]
      return exp.divide(den.multiply(den)); // Exp[x] / (1+Exp[x])^2
    }
  },
  NESTED {
    @Override
    public Scalar apply(Scalar x) {
      return x.multiply(RealScalar.ONE.subtract(x)); // x * (1 - x)
    }
  }
}
