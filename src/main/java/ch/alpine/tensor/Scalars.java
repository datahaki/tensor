// code by jph
package ch.alpine.tensor;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

import ch.alpine.tensor.chq.IntegerQ;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.num.BinaryPower;
import ch.alpine.tensor.num.Divisible;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.sca.Sign;

/** collection of useful static functions related to {@link Scalar} */
public enum Scalars {
  ;
  /** parses given string to an instance of {@link Scalar}
   * 
   * <p>Examples:
   * <pre>
   * "7/9" -> RationalScalar.of(7, 9)
   * "3.14" -> DoubleScalar.of(3.14)
   * "1E-20" -> DoubleScalar.of(1E-20)
   * "(3+2)*I/(-1+4)+8-I" -> ComplexScalar.of(8, 2/3) == "8+2/3*I"
   * "9.81[m*s^-2]" -> Quantity.of(9.81, "m*s^-2")
   * "2020-12-20T04:30" -> DateTime
   * "2020-12-20T04:30:03.125239876" -> DateTime...
   * </pre>
   * 
   * If the parsing logic encounters an inconsistency, the return type
   * is a {@link StringScalar} that holds the input string.
   * 
   * <p>Scalar types that are not supported include {@link GaussScalar}.
   * 
   * @param string
   * @return scalar */
  public static Scalar fromString(String string) {
    try {
      return QuantityParser.of(string);
    } catch (Exception exception) {
      // ---
    }
    if (16 <= string.length()) {
      int sep = string.indexOf('T', 10);
      if (string.indexOf('-', 4) < sep && sep < string.indexOf(':', 13))
        try {
          return DateTime.parse(string);
        } catch (Exception exception) {
          // ---
        }
    }
    return StringScalar.of(string);
  }

  /** Examples:
   * <pre>
   * compare(2, 4) < 0
   * compare(5, 1) > 0
   * compare(8, 8) == 0
   * </pre>
   * 
   * <p>when used for sorting, the function results in increasing ordering, i.e.
   * from small to large.
   * 
   * @param s1
   * @param s2
   * @return canonic/native comparison of input scalars
   * @throws Exception if given scalars are not comparable
   * @see Double#compare(double, double)
   * @see Integer#compare(int, int) */
  public static int compare(Scalar s1, Scalar s2) {
    @SuppressWarnings("unchecked")
    Comparable<Scalar> comparable = (Comparable<Scalar>) s1;
    return comparable.compareTo(s2);
  }

  /** @param s1
   * @param s2
   * @return true if s1 < s2 */
  public static boolean lessThan(Scalar s1, Scalar s2) {
    return compare(s1, s2) < 0;
  }

  /** @param s1
   * @param s2
   * @return true if s1 <= s2 */
  public static boolean lessEquals(Scalar s1, Scalar s2) {
    return compare(s1, s2) <= 0;
  }

  /** @param scalar
   * @return true if given scalar equals scalar.zero() */
  public static boolean isZero(Scalar scalar) {
    return scalar.equals(scalar.zero());
  }

  /** @param scalar
   * @return true if given scalar does not equal scalar.zero() */
  public static boolean nonZero(Scalar scalar) {
    return !scalar.equals(scalar.zero());
  }

  /** @param scalar
   * @return scalar
   * @throws Exception if scalar is not equal to scalar.zero()
   * @see Sign#requirePositive(Scalar) */
  public static Scalar requireZero(Scalar scalar) {
    if (isZero(scalar))
      return scalar;
    throw new Throw(scalar);
  }

  /** bi-predicate that tests if m divides n, i.e. "m|n"
   * 
   * @param m in exact precision
   * @param n in exact precision
   * @return true if m divides n, else false
   * @see Divisible */
  public static boolean divides(Scalar m, Scalar n) {
    return Divisible.of(n, m);
  }

  /** @return */
  public static BinaryPower<Scalar> add() {
    return ScalarGroups.ADD.binaryPower();
  }

  /** @return */
  public static BinaryPower<Scalar> mul() {
    return ScalarGroups.MUL.binaryPower();
  }

  // ---
  /** exact conversion to primitive type {@code int}
   * 
   * <p>function succeeds if given scalar is
   * <ul>
   * <li>instance of {@link Rational}, with
   * <li>numerator sufficiently small to encode as {@code int}, and
   * <li>denominator == 1
   * </ul>
   * 
   * @param scalar
   * @return int value that equals given scalar
   * @throws Exception if exact conversion is not possible
   * @see IntegerQ */
  public static int intValueExact(Scalar scalar) {
    return bigIntegerValueExact(scalar).intValueExact();
  }

  /** exact conversion to primitive type {@code int}
   * 
   * @param scalar non null
   * @return
   * @throws Exception if given scalar is null */
  public static OptionalInt optionalInt(Scalar scalar) {
    try {
      return OptionalInt.of(intValueExact(scalar));
    } catch (Exception exception) {
      // ---
    }
    Objects.requireNonNull(scalar);
    return OptionalInt.empty();
  }

  // ---
  /** exact conversion to primitive type {@code long}
   * 
   * <p>function succeeds if given scalar is
   * <ul>
   * <li>instance of {@link Rational}, with
   * <li>numerator sufficiently small to encode as {@code long}, and
   * <li>denominator == 1
   * </ul>
   * 
   * @param scalar
   * @return long value that equals given scalar
   * @throws Exception if exact conversion is not possible
   * @see IntegerQ */
  public static long longValueExact(Scalar scalar) {
    return bigIntegerValueExact(scalar).longValueExact();
  }

  // ---
  /** exact conversion to type {@code BigInteger}
   * 
   * <p>function succeeds if given scalar is instance of
   * {@link Rational} with denominator == 1.
   * 
   * @param scalar
   * @return BigInteger that equals given scalar
   * @throws Exception if exact conversion is not possible
   * @see IntegerQ */
  public static BigInteger bigIntegerValueExact(Scalar scalar) {
    if (scalar instanceof Rational rational && rational.isInteger())
      return rational.numerator();
    throw new Throw(scalar);
  }

  /** exact conversion to type {@code BigInteger}
   * 
   * @param scalar non null
   * @return BigInteger that equals given scalar
   * or empty if given scalar does not represent an integer
   * @throws Exception if given scalar is null */
  public static Optional<BigInteger> optionalBigInteger(Scalar scalar) {
    if (scalar instanceof Rational rational && rational.isInteger())
      return Optional.of(rational.numerator());
    Objects.requireNonNull(scalar);
    return Optional.empty();
  }
}
