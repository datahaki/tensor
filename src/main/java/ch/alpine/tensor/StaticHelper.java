// code by jph
// https://docs.oracle.com/javase/7/docs/api/java/lang/Double.html#valueOf(java.lang.String)
package ch.alpine.tensor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Optional;

import ch.alpine.tensor.api.ComplexEmbedding;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.tri.ArcTan;

/* package */ enum StaticHelper {
  ;
  /** code from java.lang.Double */
  private static final String Digits = "(\\p{Digit}+)";
  private static final String HexDigits = "(\\p{XDigit}+)";
  // an exponent is 'e' or 'E' followed by an optionally
  // signed decimal integer.
  private static final String Exp = "[eE][+-]?" + Digits;
  // optional leading and trailing whitespace and sign is obsolete
  public static final String fpRegex = ("(" + //
      "NaN|" + // "NaN" string
      "Infinity|" + // "Infinity" string
      // A decimal floating-point string representing a finite positive
      // number without a leading sign has at most five basic pieces:
      // Digits . Digits ExponentPart FloatTypeSuffix
      //
      // Since this method allows integer-only strings as input
      // in addition to strings of floating-point literals, the
      // two sub-patterns below are simplifications of the grammar
      // productions from section 3.10.2 of
      // The Java Language Specification.
      // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
      "(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|" +
      // . Digits ExponentPart_opt FloatTypeSuffix_opt
      "(\\.(" + Digits + ")(" + Exp + ")?)|" +
      // Hexadecimal strings
      "((" +
      // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
      "(0[xX]" + HexDigits + "(\\.)?)|" +
      // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
      "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" + ")[pP][+-]?" + Digits + "))" + "[fFdD]?))" //
  );

  // ---
  /** @param value
   * @return exact root of value
   * @throws IllegalArgumentException if value is not a square number */
  public static Optional<BigInteger> sqrt(BigInteger value) {
    return Optional.of(sqrtApproximation(value)) //
        .filter(root -> root.multiply(root).equals(value));
  }

  public static BigInteger sqrtApproximation(BigInteger n) {
    if (n.signum() < 0)
      throw new ArithmeticException();
    if (n.equals(BigInteger.ZERO) || n.equals(BigInteger.ONE))
      return n;
    BigInteger x = n.shiftRight(n.bitLength() / 2); // initial guess
    while (true) {
      BigInteger y = x.add(n.divide(x)).shiftRight(1);
      if (y.equals(x) || y.equals(x.subtract(BigInteger.ONE)))
        return y;
      x = y;
    }
  }

  // ---
  /** @param x complex scalar
   * @param y complex scalar
   * @return Mathematica::ArcTan[x, y] */
  public static Scalar arcTan(Scalar x, Scalar y) {
    if (Scalars.isZero(x)) { // prevent division by zero
      ComplexEmbedding complexEmbedding = (ComplexEmbedding) y;
      return Sign.FUNCTION.apply(complexEmbedding.real()).multiply(Pi.HALF);
    }
    return ArcTan.FUNCTION.apply(y.divide(x));
  }

  public static final Scalar[] SIGN = { //
      RealScalar.ONE.negate(), // -1
      RealScalar.ZERO, // 0
      RealScalar.ONE }; // +1
  // ---

  /** @param bigDecimal
   * @return
   * @throws Exception if value is Infinity */
  public static BigInteger floor(BigDecimal bigDecimal) {
    BigInteger bigInteger = bigDecimal.toBigInteger();
    if (0 < new BigDecimal(bigInteger).compareTo(bigDecimal)) {
      bigDecimal = bigDecimal.subtract(BigDecimal.ONE);
      bigInteger = bigDecimal.toBigInteger();
    }
    return bigInteger;
  }

  /** @param bigDecimal
   * @return
   * @throws Exception if value is Infinity */
  public static BigInteger ceiling(BigDecimal bigDecimal) {
    BigInteger bigInteger = bigDecimal.toBigInteger();
    if (new BigDecimal(bigInteger).compareTo(bigDecimal) < 0) {
      bigDecimal = bigDecimal.add(BigDecimal.ONE);
      bigInteger = bigDecimal.toBigInteger();
    }
    return bigInteger;
  }

  /** @param bigDecimal
   * @return */
  public static BigInteger round(BigDecimal bigDecimal) {
    return bigDecimal.setScale(0, RoundingMode.HALF_UP).toBigIntegerExact();
  }
}
