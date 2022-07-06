// code by jph
package ch.alpine.tensor.io;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.tensor.AbstractScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;

/** StringScalar represents a string.
 * 
 * <p>No mathematical operations add, multiply, ... are supported.
 * 
 * <p>For instance, when importing a csv file, the first line may contain
 * column header names which are imported as {@link StringScalar}s.
 * 
 * <p>In the tensor library
 * <pre>
 * String string = "{Hello, World}";
 * string.equals(Tensors.fromString(string).toString()); // evaluates to true
 * </pre>
 * 
 * <p>That means StringScalar is <em>not</em> compatible with Mathematica !
 * In Mathematica string expressions begin and terminate with quotes.
 * For example, Mathematica::{"Hello", "World"}.
 * Whether this convention will adopted in the tensor library at some point
 * in the future is open for discussion. */
public class StringScalar extends AbstractScalar implements //
    Comparable<Scalar>, Serializable {
  /** instance of string scalar with empty string "" as value */
  public static final Scalar EMPTY = of("");

  /** @param string
   * @return new instance of {@link StringScalar} representing string
   * @throws Exception if argument is null */
  public static Scalar of(String string) {
    return new StringScalar(Objects.requireNonNull(string));
  }

  // ---
  private final String string;

  private StringScalar(String string) {
    this.string = string;
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    throw Throw.of(this);
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    throw Throw.of(this, scalar);
  }

  @Override // from Scalar
  public Scalar negate() {
    throw Throw.of(this);
  }

  @Override // from Scalar
  public Scalar zero() {
    throw Throw.of(this);
  }

  @Override // from Scalar
  public Scalar one() {
    throw Throw.of(this);
  }

  @Override // from Scalar
  public Number number() {
    throw Throw.of(this);
  }

  // ---
  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    throw Throw.of(this, scalar);
  }

  @Override // from Comparable<Scalar>
  public int compareTo(Scalar scalar) {
    if (scalar instanceof StringScalar stringScalar)
      return string.compareTo(stringScalar.string);
    throw Throw.of(this, scalar);
  }

  // ---
  @Override // from AbstractScalar
  public int hashCode() {
    return string.hashCode();
  }

  @Override // from AbstractScalar
  public boolean equals(Object object) {
    return object instanceof StringScalar stringScalar //
        && string.equals(stringScalar.string);
  }

  @Override // from AbstractScalar
  public String toString() {
    return string;
  }
}
