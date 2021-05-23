// code by jph
package ch.alpine.tensor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/* package */ enum ScalarParser {
  ;
  private static final char OPENING_BRACKET = '(';
  private static final char CLOSING_BRACKET = ')';
  private static final char ADD = '+';
  private static final char SUBTRACT = '-';
  private static final char MULTIPLY = '*';
  private static final char DIVIDE = '/';
  private static final char DECIMAL_PRIME = '`';
  // ---
  private static final Predicate<String> PREDICATE_INTEGER = //
      Pattern.compile("\\d+").asMatchPredicate(); // optional sign is obsolete
  private static final Predicate<String> PREDICATE_DOUBLE = //
      Pattern.compile(StaticHelper.fpRegex).asMatchPredicate();
  /** suffix that is appended to imaginary part of {@link ComplexScalar} in function toString() */
  private static final String I_SYMBOL = "I";

  /** @param _string
   * @return
   * @throws Exception if given string cannot be parsed to a scalar of instance
   * {@link RealScalar} or {@link ComplexScalar} */
  public static Scalar of(final String _string) {
    final String string = _string.strip();
    final char[] chars = string.toCharArray();
    final List<Integer> plusMinus = new ArrayList<>();
    Integer times = null;
    Integer divide = null;
    {
      int index = 0;
      int level = 0;
      for (char c : chars) {
        if (c == OPENING_BRACKET)
          ++level;
        if (level == 0) {
          if (c == ADD || c == SUBTRACT) {
            if (index == 0 || chars[index - 1] != 'E')
              plusMinus.add(index);
          } else //
          if (c == MULTIPLY)
            times = index;
          else //
          if (c == DIVIDE)
            divide = index;
        }
        if (c == CLOSING_BRACKET)
          --level;
        ++index;
      }
    }
    if (!plusMinus.isEmpty()) {
      int first = plusMinus.get(0);
      Scalar sum = first == 0 //
          ? RealScalar.ZERO
          : of(string.substring(0, first));
      for (int index = 0; index < plusMinus.size(); ++index) {
        int curr = plusMinus.get(index);
        char c = chars[curr];
        int next = index + 1 < plusMinus.size() //
            ? plusMinus.get(index + 1)
            : string.length();
        Scalar rhs = of(string.substring(curr + 1, next));
        sum = c == ADD //
            ? sum.add(rhs)
            : sum.subtract(rhs);
      }
      return sum;
    }
    if (Objects.nonNull(times))
      return of(string.substring(0, times)).multiply(of(string.substring(times + 1)));
    if (Objects.nonNull(divide))
      return of(string.substring(0, divide)).divide(of(string.substring(divide + 1)));
    if (string.startsWith("(") && string.endsWith(")"))
      return of(string.substring(1, string.length() - 1));
    if (string.equals(I_SYMBOL))
      return ComplexScalar.I;
    if (PREDICATE_INTEGER.test(string))
      return RationalScalar.integer(new BigInteger(string));
    if (PREDICATE_DOUBLE.test(string))
      return DoubleScalar.of(Double.parseDouble(string));
    int prime = string.indexOf(DECIMAL_PRIME); // check decimal
    if (0 < prime) {
      String ante = string.substring(0, prime);
      String post = string.substring(prime + 1);
      if (post.isEmpty())
        return of(ante);
      int precision = Math.toIntExact(Math.round(Double.parseDouble(post)));
      MathContext mathContext = new MathContext(precision, RoundingMode.HALF_EVEN);
      BigDecimal bigDecimal = new BigDecimal(ante, mathContext);
      return DecimalScalar.of(bigDecimal, precision);
    }
    throw new IllegalArgumentException(_string);
  }

  /** helper function that formats imaginary part to a String
   * 
   * @param im
   * @return */
  public static String imagToString(Scalar im) {
    if (im instanceof RationalScalar) {
      RationalScalar rationalScalar = (RationalScalar) im;
      BigInteger num = rationalScalar.numerator();
      BigInteger den = rationalScalar.denominator();
      if (num.equals(BigInteger.ONE))
        return fractionOfI(den);
      if (num.equals(BigInteger.ONE.negate()))
        return "-" + fractionOfI(den);
    }
    String imag = im.toString();
    if (imag.equals("1"))
      return I_SYMBOL;
    if (imag.equals("-1"))
      return '-' + I_SYMBOL;
    return imag + '*' + I_SYMBOL;
  }

  private static String fractionOfI(BigInteger den) {
    return I_SYMBOL + (den.equals(BigInteger.ONE) ? "" : "/" + den);
  }
}
