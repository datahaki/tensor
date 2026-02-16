// code by jph
package ch.alpine.tensor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.ArgInterface;
import ch.alpine.tensor.api.ComplexEmbedding;
import ch.alpine.tensor.api.ConjugateInterface;
import ch.alpine.tensor.api.RoundingInterface;
import ch.alpine.tensor.api.SignInterface;
import ch.alpine.tensor.sca.exp.LogInterface;
import ch.alpine.tensor.sca.pow.PowerInterface;
import ch.alpine.tensor.sca.tri.ArcTanInterface;

/** RealScalar encodes a real number
 * 
 * <p>encodings provided by the tensor library are
 * <ul>
 * <li>integer fraction {@link Rational}
 * <li>decimal with double precision {@link DoubleScalar}
 * <li>decimal with extra precision {@link DecimalScalar}
 * </ul> */
public interface RealScalar extends Scalar, //
    AbsInterface, ArcTanInterface, ArgInterface, Comparable<Scalar>, ComplexEmbedding, //
    ConjugateInterface, LogInterface, PowerInterface, RoundingInterface, //
    SignInterface {
  /** real scalar 0 as a {@link Rational} */
  Scalar ZERO = Rational.integer(0);
  /** real scalar 1 as a {@link Rational} */
  Scalar ONE = Rational.integer(1);
  /** real scalar 2 as a {@link Rational} */
  Scalar TWO = Rational.integer(2);

  /** @param value
   * @return real scalar of given integer value */
  static Scalar of(long value) {
    return Rational.integer(value);
  }

  /** @param value
   * @return real scalar of given decimal value */
  static Scalar of(double value) {
    return DoubleScalar.of(value);
  }

  /** @param value
   * @return real scalar of given integer value */
  static Scalar of(Integer value) {
    return Rational.integer(value);
  }

  /** @param value
   * @return real scalar of given integer value */
  static Scalar of(Long value) {
    return Rational.integer(value);
  }

  /** @param bigInteger
   * @return real scalar of given integer value */
  static Scalar of(BigInteger bigInteger) {
    return Rational.integer(bigInteger);
  }

  /** @param bigDecimal
   * @return real scalar of given decimal value */
  static Scalar of(BigDecimal bigDecimal) {
    return DecimalScalar.of(bigDecimal);
  }

  /** depending on the derived class of the given {@link Number},
   * the value is encoded as {@link Rational},
   * {@link DoubleScalar}, or {@link DecimalScalar}.
   * 
   * @param number non-null
   * @return scalar with the best possible accuracy to encode given number
   * @throws Exception if number is null, or instance of an unsupported type */
  static Scalar of(Number number) {
    if (number instanceof Integer || //
        number instanceof Long || //
        number instanceof Short || //
        number instanceof Byte || //
        number instanceof AtomicInteger || //
        number instanceof AtomicLong)
      return Rational.integer(number.longValue());
    if (number instanceof Double || //
        number instanceof Float)
      return DoubleScalar.of(number.doubleValue());
    if (number instanceof BigInteger)
      return Rational.integer((BigInteger) number);
    if (number instanceof BigDecimal)
      return DecimalScalar.of((BigDecimal) number);
    Objects.requireNonNull(number);
    throw new IllegalArgumentException(number.toString());
  }
}
