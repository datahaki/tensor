// code by jph
package ch.ethz.idsc.tensor.num;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

/** instantiated by {@link Scalars#binaryPower(Scalar)} */
public class ScalarProduct implements GroupInterface<Scalar>, Serializable {
  private static final long serialVersionUID = 2474105585311096988L;
  // ---
  private final Scalar one;

  /** @param one assumed to be non-null */
  public ScalarProduct(Scalar one) {
    this.one = Objects.requireNonNull(one);
  }

  @Override // from BinaryPower
  public Scalar identity() {
    return one;
  }

  @Override // from BinaryPower
  public Scalar invert(Scalar scalar) {
    return scalar.reciprocal();
  }

  @Override // from BinaryPower
  public Scalar combine(Scalar s1, Scalar s2) {
    return s1.multiply(s2);
  }
}
