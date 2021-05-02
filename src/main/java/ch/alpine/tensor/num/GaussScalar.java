// code by jph
package ch.alpine.tensor.num;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

import ch.alpine.tensor.AbstractScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.ConjugateInterface;
import ch.alpine.tensor.api.ExactScalarQInterface;
import ch.alpine.tensor.api.PowerInterface;
import ch.alpine.tensor.api.RoundingInterface;
import ch.alpine.tensor.api.SignInterface;
import ch.alpine.tensor.api.SqrtInterface;
import ch.alpine.tensor.qty.Quantity;

/** scalars from finite field with prime number of elements and values
 * 0, 1, 2, ..., prime - 1
 * 
 * an instance stores two non-negative integers: the value and the prime
 * which can be accessed via {@link #number()} and {@link #prime()}. */
public class GaussScalar extends AbstractScalar implements //
    AbsInterface, ConjugateInterface, ExactScalarQInterface, PowerInterface, //
    RoundingInterface, SignInterface, SqrtInterface, //
    Comparable<Scalar>, Serializable {
  /** @param value
   * @param prime number
   * @return value in finite field with prime number of elements
   * @throws Exception if given prime is not a prime number */
  public static GaussScalar of(BigInteger value, BigInteger prime) {
    return in(value, PrimeQ.require(prime));
  }

  /** @param value
   * @param prime number
   * @return value in finite field with prime number of elements
   * @throws Exception if given prime is not a prime number */
  public static GaussScalar of(long value, long prime) {
    return of(BigInteger.valueOf(value), BigInteger.valueOf(prime));
  }

  // helper function
  private static GaussScalar in(BigInteger value, BigInteger prime) {
    return new GaussScalar(value.mod(prime), prime);
  }

  /***************************************************/
  private final BigInteger value;
  private final BigInteger prime;

  /** @param value non-negative
   * @param prime */
  private GaussScalar(BigInteger value, BigInteger prime) {
    this.value = value;
    this.prime = prime;
  }

  @Override // from Scalar
  public GaussScalar negate() {
    return in(value.negate(), prime);
  }

  @Override // from Scalar
  public GaussScalar reciprocal() {
    return new GaussScalar(value.modInverse(prime), prime);
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof GaussScalar)
      return in(value.multiply(requireCommonPrime((GaussScalar) scalar)), prime);
    if (scalar instanceof Quantity)
      return scalar.multiply(this);
    throw TensorRuntimeException.of(this, scalar);
  }

  @Override // from AbstractScalar
  public GaussScalar divide(Scalar scalar) {
    return (GaussScalar) super.divide(scalar);
  }

  @Override // from AbstractScalar
  public GaussScalar under(Scalar scalar) {
    return (GaussScalar) super.under(scalar);
  }

  @Override // from Scalar
  public BigInteger number() {
    return value;
  }

  /** @return prime order of finite field */
  public BigInteger prime() {
    return prime;
  }

  @Override // from Scalar
  public Scalar zero() {
    return new GaussScalar(BigInteger.ZERO, prime);
  }

  @Override // from Scalar
  public Scalar one() {
    return new GaussScalar(BigInteger.ONE, prime);
  }

  /***************************************************/
  @Override // from AbstractScalar
  protected GaussScalar plus(Scalar scalar) {
    if (scalar instanceof GaussScalar)
      return in(value.add(requireCommonPrime((GaussScalar) scalar)), prime);
    throw TensorRuntimeException.of(this, scalar);
  }

  /***************************************************/
  @Override
  public Scalar abs() {
    return this;
  }

  @Override
  public Scalar absSquared() {
    return multiply(this);
  }

  @Override // from Comparable<Scalar>
  public int compareTo(Scalar scalar) {
    if (scalar instanceof GaussScalar)
      return value.compareTo(requireCommonPrime((GaussScalar) scalar));
    throw TensorRuntimeException.of(this, scalar);
  }

  private BigInteger requireCommonPrime(GaussScalar gaussScalar) {
    if (prime.equals(gaussScalar.prime))
      return gaussScalar.value;
    throw TensorRuntimeException.of(this, gaussScalar);
  }

  @Override // from ConjugateInterface
  public Scalar conjugate() {
    return this;
  }

  @Override // from ExactScalarQInterface
  public boolean isExactScalar() {
    return true;
  }

  @Override // from PowerInterface
  public GaussScalar power(Scalar exponent) {
    // exponents of the form 1/2, 1/3, etc. could also be valid
    return new GaussScalar(value.modPow(Scalars.bigIntegerValueExact(exponent), prime), prime);
  }

  @Override // from RoundingInterface
  public Scalar ceiling() {
    return this;
  }

  @Override // from RoundingInterface
  public Scalar floor() {
    return this;
  }

  @Override // from RoundingInterface
  public Scalar round() {
    return this;
  }

  @Override // from SignInterface
  public Scalar sign() {
    return new GaussScalar(BigInteger.valueOf(value.signum()), prime);
  }

  @Override // from SqrtInterface
  public GaussScalar sqrt() {
    GaussScalar gaussScalar = StaticHelper.SQRT.get(this);
    if (Objects.nonNull(gaussScalar))
      return gaussScalar;
    for (BigInteger index = BigInteger.ZERO; index.compareTo(prime) < 0; index = index.add(BigInteger.ONE))
      if (equals(in(index.multiply(index), prime))) {
        gaussScalar = in(index, prime);
        StaticHelper.SQRT.put(this, gaussScalar);
        return gaussScalar;
      }
    // examples of gauss scalars without sqrt: 2 mod 5, 3 mod 5, 6 mod 11, etc.
    throw TensorRuntimeException.of(this); // sqrt of this does not exist
  }

  /***************************************************/
  @Override // from AbstractScalar
  public int hashCode() {
    return value.hashCode() + 31 * prime.hashCode();
  }

  @Override // from AbstractScalar
  public boolean equals(Object object) {
    if (object instanceof GaussScalar) {
      GaussScalar gaussScalar = (GaussScalar) object;
      return value.equals(gaussScalar.value) //
          && prime.equals(gaussScalar.prime);
    }
    return false;
  }

  @Override // from AbstractScalar
  public String toString() {
    return "(" + value + " mod " + prime + ")";
    // return "{\"value\": " + value + ", \"prime\": " + prime + "}";
  }
}
