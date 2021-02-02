// code by jph
package ch.ethz.idsc.tensor.num;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

/** instantiated by {@link Scalars#binaryPower(Scalar)} */
public enum ScalarProduct implements GroupInterface<Scalar> {
  INSTANCE;

  @Override // from GroupInterface
  public Scalar neutral(Scalar scalar) {
    return scalar.one();
  }

  @Override // from GroupInterface
  public Scalar invert(Scalar scalar) {
    return scalar.reciprocal();
  }

  @Override // from GroupInterface
  public Scalar combine(Scalar scalar1, Scalar scalar2) {
    return scalar1.multiply(scalar2);
  }

  @Override // from Object
  public String toString() {
    return String.format("%s", getClass().getSimpleName());
  }
}
