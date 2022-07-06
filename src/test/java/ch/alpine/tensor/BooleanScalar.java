// code by jph
package ch.alpine.tensor;

import java.io.Serializable;

import ch.alpine.tensor.sca.pow.SqrtInterface;

/** arithmetic of BooleanScalar is as for an element of the finite field F with cardinality |F|=2
 * multiplication is logical AND
 * addition is logical XOR */
/* package */ final class BooleanScalar extends AbstractScalar implements //
    Comparable<Scalar>, SqrtInterface, Serializable {
  /** instance with value true, toString() == "true" */
  public static final Scalar TRUE = new BooleanScalar(true);
  /** instance with value false, toString() == "false" */
  public static final Scalar FALSE = new BooleanScalar(false);

  /** @param value
   * @return */
  public static Scalar of(boolean value) {
    return value //
        ? TRUE
        : FALSE;
  }

  // ---
  private final boolean value;

  /** constructor initializes the static members
   * 
   * @param value */
  private BooleanScalar(boolean value) {
    this.value = value;
  }

  @Override // from Scalar
  public Scalar negate() {
    return this;
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    if (!value)
      throw Throw.of(this);
    return this;
  }

  @Override // from Scalar
  public Scalar zero() {
    return FALSE;
  }

  @Override // from Scalar
  public Scalar one() {
    return TRUE;
  }

  @Override // from Scalar
  public Number number() {
    // since Boolean is not an instance of Number, we return type Integer
    return value //
        ? 1
        : 0;
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof BooleanScalar) {
      BooleanScalar booleanScalar = (BooleanScalar) scalar;
      return of(Boolean.logicalAnd(value, booleanScalar.value));
    }
    throw Throw.of(this, scalar);
  }

  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof BooleanScalar) {
      BooleanScalar booleanScalar = (BooleanScalar) scalar;
      return of(Boolean.logicalXor(value, booleanScalar.value));
    }
    throw Throw.of(this, scalar);
  }

  @Override // from Comparable
  public int compareTo(Scalar scalar) {
    if (scalar instanceof BooleanScalar) {
      BooleanScalar booleanScalar = (BooleanScalar) scalar;
      return Boolean.compare(value, booleanScalar.value);
    }
    throw Throw.of(this, scalar);
  }

  @Override // from SqrtInterface
  public Scalar sqrt() {
    return this;
  }

  @Override // from Object
  public int hashCode() {
    return Boolean.hashCode(value);
  }

  @Override // from Object
  public boolean equals(Object object) {
    if (object instanceof BooleanScalar) {
      BooleanScalar booleanScalar = (BooleanScalar) object;
      return value == booleanScalar.value;
    }
    return false;
  }

  @Override // from Object
  public String toString() {
    return Boolean.toString(value);
  }
}
