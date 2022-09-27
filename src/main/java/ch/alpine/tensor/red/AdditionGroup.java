// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.num.GroupInterface;

/* package */ enum AdditionGroup implements GroupInterface<Scalar> {
  INSTANCE;

  @Override
  public Scalar neutral(Scalar element) {
    return element.zero();
  }

  @Override
  public Scalar invert(Scalar element) {
    return element.negate();
  }

  @Override
  public Scalar combine(Scalar factor1, Scalar factor2) {
    return factor1.add(factor2);
  }
}
