// code by jph
package ch.ethz.idsc.tensor.num;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

/** instantiated by {@link Scalars#binaryPower(Scalar)} */
public enum ScalarProduct implements GroupInterface<Scalar> {
  INSTANCE;

  @Override // from GroupInterface
  public Scalar identity(Scalar scalar) {
    return scalar.one();
  }

  @Override // from GroupInterface
  public Scalar invert(Scalar scalar) {
    return scalar.reciprocal();
  }

  @Override // from GroupInterface
  public Scalar combine(Scalar s1, Scalar s2) {
    return s1.multiply(s2);
  }

  @Override
  public String toString() {
    return String.format("%s", getClass().getSimpleName());
  }
}
