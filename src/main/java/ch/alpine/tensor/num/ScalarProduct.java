// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.lie.Quaternion;

/** implementation computes integer powers of {@link ComplexScalar},
 * {@link Quaternion}, etc. */
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
    return "ScalarProduct";
  }
}
