// code by jph
package ch.alpine.tensor.ext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

/** implementation is standalone */
public enum BigDecimalMath {
  ;
  /** computation of square-root using Newton iteration
   * 
   * @param square
   * @param mathContext
   * @return root of given square */
  // implementation inspired by Luciano Culacciatti
  // http://www.codeproject.com/Tips/257031/Implementing-SqrtRoot-in-BigDecimal
  public static BigDecimal sqrt(BigDecimal square, MathContext mathContext) {
    int signum = square.signum();
    if (signum == 0)
      return BigDecimal.ZERO;
    if (signum < 0)
      throw new IllegalArgumentException("Sqrt[" + square + "]");
    BigDecimal xn0 = BigDecimal.ZERO;
    BigDecimal xn1 = BigDecimal.ONE;
    while (xn0.compareTo(xn1) != 0) {
      xn0 = xn1;
      BigDecimal fx = xn0.multiply(xn0).subtract(square);
      BigDecimal fpx = xn0.add(xn0); // equals to 2 * xn0
      xn1 = fx.divide(fpx, mathContext);
      xn1 = xn0.subtract(xn1, mathContext);
    }
    return xn1;
  }

  // ---
  /** @param x
   * @param mathContext
   * @return exponential of x */
  public static BigDecimal exp(BigDecimal x, MathContext mathContext) {
    BigDecimal xn0 = BigDecimal.ZERO;
    BigDecimal xn1 = BigDecimal.ONE;
    BigDecimal add = x;
    for (int index = 0; xn0.compareTo(xn1) != 0;) {
      xn0 = xn1;
      add = add.multiply(x).divide(BigDecimal.valueOf(++index), mathContext);
      xn1 = xn1.add(add, mathContext);
    }
    return xn1;
  }

  /** @param x
   * @param mathContext
   * @return sine of x */
  public static BigDecimal sin(BigDecimal x, MathContext mathContext) {
    BigDecimal xn0 = BigDecimal.ZERO;
    BigDecimal xn1 = x;
    BigDecimal add = x;
    BigDecimal x2 = x.multiply(x, mathContext);
    int count = 0;
    for (int index = 1; xn0.compareTo(xn1) != 0;) {
      xn0 = xn1;
      add = add.multiply(x2).divide(BigDecimal.valueOf(++index * ++index), mathContext);
      xn1 = Integers.isEven(++count) //
          ? xn1.add(add, mathContext)
          : xn1.subtract(add, mathContext);
    }
    return xn1;
  }

  /** @param x
   * @param mathContext
   * @return hyperbolic sine of x */
  public static BigDecimal sinh(BigDecimal x, MathContext mathContext) {
    BigDecimal xn0 = BigDecimal.ZERO;
    BigDecimal xn1 = x;
    BigDecimal add = x;
    BigDecimal x2 = x.multiply(x, mathContext);
    for (int index = 1; xn0.compareTo(xn1) != 0;) {
      xn0 = xn1;
      add = add.multiply(x2).divide(BigDecimal.valueOf(++index * ++index), mathContext);
      xn1 = xn1.add(add, mathContext);
    }
    return xn1;
  }

  /** @param x
   * @param mathContext
   * @return cosine of x */
  public static BigDecimal cos(BigDecimal x, MathContext mathContext) {
    BigDecimal xn0 = BigDecimal.ZERO;
    BigDecimal xn1 = BigDecimal.ONE;
    BigDecimal add = BigDecimal.ONE;
    BigDecimal x2 = x.multiply(x, mathContext);
    int count = 0;
    for (int index = 0; xn0.compareTo(xn1) != 0;) {
      xn0 = xn1;
      add = add.multiply(x2).divide(BigDecimal.valueOf(++index * ++index), mathContext);
      xn1 = Integers.isEven(++count) //
          ? xn1.add(add, mathContext)
          : xn1.subtract(add, mathContext);
    }
    return xn1;
  }

  /** @param x
   * @param mathContext
   * @return hyperbolic cosine of x */
  public static BigDecimal cosh(BigDecimal x, MathContext mathContext) {
    BigDecimal xn0 = BigDecimal.ZERO;
    BigDecimal xn1 = BigDecimal.ONE;
    BigDecimal add = BigDecimal.ONE;
    BigDecimal x2 = x.multiply(x, mathContext);
    for (int index = 0; xn0.compareTo(xn1) != 0;) {
      xn0 = xn1;
      add = add.multiply(x2).divide(BigDecimal.valueOf(++index * ++index), mathContext);
      xn1 = xn1.add(add, mathContext);
    }
    return xn1;
  }

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

  public static BigInteger round(BigDecimal bigDecimal) {
    return bigDecimal.setScale(0, RoundingMode.HALF_UP).toBigIntegerExact();
  }
}
