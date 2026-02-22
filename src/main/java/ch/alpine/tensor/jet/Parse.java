package ch.alpine.tensor.jet;

import java.math.BigInteger;
import java.util.Locale;

enum Parse {
  ;
  public static DoubleDouble parse(String s) {
    ParsedDecimal pd = parseDecimal(s);
    // exact integer value
    DoubleDouble value = fromBigInteger(pd.digits);
    int k = pd.exp10;
    if (k > 0) {
      // multiply by 10^k = 2^k * 5^k
      value = value.mul(pow5(k));
      value = value.scalb(k);
    } else if (k < 0) {
      k = -k;
      // divide by 10^k
      value = value.div(pow5(k));
      value = value.scalb(-k);
    }
    return value;
  }

  private static DoubleDouble pow5(int n) {
    DoubleDouble result = DoubleDouble.ONE;
    DoubleDouble base = new DoubleDouble(5.0, 0.0);
    while (n != 0) {
      if ((n & 1) != 0)
        result = result.mul(base);
      base = base.mul(base);
      n >>>= 1;
    }
    return result;
  }

  private static final DoubleDouble[] POW5 = new DoubleDouble[32];
  static {
    POW5[0] = DoubleDouble.ONE;
    DoubleDouble five = new DoubleDouble(5.0, 0.0);
    for (int i = 1; i < POW5.length; i++)
      POW5[i] = POW5[i - 1].mul(five);
  }

  private static DoubleDouble fromBigInteger(BigInteger x) {
    if (x.signum() == 0)
      return DoubleDouble.ZERO;
    int bitLen = x.bitLength();
    int shift = Math.max(0, bitLen - 53); // keep top 53 bits
    BigInteger hiInt = x.shiftRight(shift);
    double hi = hiInt.doubleValue();
    // remainder = exact error
    BigInteger rem = x.subtract(hiInt.shiftLeft(shift));
    double lo = rem.doubleValue();
    DoubleDouble result = new DoubleDouble(hi, 0.0);
    result = result.add(new DoubleDouble(lo, 0.0));
    result = result.scalb(shift); // multiply by 2^shift
    return result;
  }

  private static final class ParsedDecimal {
    final BigInteger digits;
    final int exp10;

    ParsedDecimal(BigInteger d, int e) {
      this.digits = d;
      this.exp10 = e;
    }
  }

  private static ParsedDecimal parseDecimal(String s) {
    s = s.trim().toLowerCase(Locale.ROOT);
    int ePos = s.indexOf('e');
    int exp10 = 0;
    if (ePos >= 0) {
      exp10 = Integer.parseInt(s.substring(ePos + 1));
      s = s.substring(0, ePos);
    }
    int dot = s.indexOf('.');
    if (dot >= 0) {
      exp10 -= (s.length() - dot - 1);
      s = s.substring(0, dot) + s.substring(dot + 1);
    }
    if (s.isEmpty() || s.equals("+") || s.equals("-"))
      throw new NumberFormatException("Invalid decimal");
    return new ParsedDecimal(new BigInteger(s), exp10);
  }
}
