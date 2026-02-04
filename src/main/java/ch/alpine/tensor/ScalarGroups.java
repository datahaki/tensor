// code by jph
package ch.alpine.tensor;

import ch.alpine.tensor.api.GroupInterface;
import ch.alpine.tensor.lie.rot.Quaternion;
import ch.alpine.tensor.num.BinaryPower;

/** implementation computes integer powers of {@link ComplexScalar},
 * {@link Quaternion}, etc. */
/* package */ enum ScalarGroups implements GroupInterface<Scalar> {
  ADD {
    @Override // from GroupInterface
    public Scalar neutral(Scalar scalar) {
      return scalar.zero();
    }

    @Override // from GroupInterface
    public Scalar invert(Scalar scalar) {
      return scalar.negate();
    }

    @Override // from GroupInterface
    public Scalar combine(Scalar scalar1, Scalar scalar2) {
      return scalar1.add(scalar2);
    }

    @Override // from Object
    public String toString() {
      return "ScalarAddition";
    }
  },
  MUL {
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
  };

  private final BinaryPower<Scalar> binaryPower = new BinaryPower<>(this);

  public BinaryPower<Scalar> binaryPower() {
    return binaryPower;
  }
}
