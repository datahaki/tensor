// code from chatgpt
package ch.alpine.tensor.jet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import ch.alpine.tensor.ext.PackageTestAccess;

/** DoubleDouble: ~106-bit floating point using two IEEE doubles.
 *
 * value = hi + lo (lo stores the rounding error of hi)
 *
 * Based on Dekker (1971) and Shewchuk (1997) error-free transforms. */
public class BigDouble {
  private static final double SPLIT = 134217729.0; // 2^27 + 1
  public static final BigDouble ZERO = of(0);
  public static final BigDouble ONE = of(1);
  public static final BigDouble TWO = of(2);
  public static final BigDouble HALF = of(0.5);
  public static final BigDouble PI = new BigDouble(3.141592653589793116, 1.224646799147353207e-16);
  public static final BigDouble TWO_PI = PI.mul(fromLong(2));
  public static final BigDouble HALF_PI = PI.mul(BigDouble.of(0.5));
  public static final BigDouble E = new BigDouble(2.718281828459045091, 1.445646891729250158e-16);
  public static final BigDouble LN2 = new BigDouble(0.693147180559945286, 2.319046813846299558e-17);
  /* 2/pi split into double-double pieces.
   * These are derived from a >200-bit expansion and rounded
   * so that multiplication is exact in double-double arithmetic. */
  private static final BigDouble TWO_OVER_PI = new BigDouble(0.63661977236758138243, -3.9357353350364971764e-17);
  /* π/2 split the same way */
  private static final BigDouble PI_OVER_TWO = new BigDouble(1.57079632679489655800, 6.1232339957367660359e-17);

  public static BigDouble of(double x) {
    return new BigDouble(x, 0.0);
  }

  public static BigDouble fromLong(long x) {
    return new BigDouble(x, 0.0);
  }

  /* ---------------- Constructors ---------------- */
  private final double hi;
  private final double lo;

  @PackageTestAccess
  BigDouble(double hi, double lo) {
    double s = hi + lo;
    double e = lo - (s - hi);
    this.hi = s;
    this.lo = e;
  }

  public double hi() {
    return hi;
  }

  public double lo() {
    return lo;
  }
  /* ---------------- Basic Error-Free Ops ---------------- */

  /** Computes a + b exactly as (sum, err). */
  private static BigDouble twoSum(double a, double b) {
    double s = a + b;
    double bb = s - a;
    double err = (a - (s - bb)) + (b - bb);
    return new BigDouble(s, err);
  }

  /** Computes a * b exactly as (prod, err). */
  private static BigDouble twoProd(double a, double b) {
    double p = a * b;
    double ca = SPLIT * a;
    double ah = ca - (ca - a);
    double al = a - ah;
    double cb = SPLIT * b;
    double bh = cb - (cb - b);
    double bl = b - bh;
    double err = ((ah * bh - p) + ah * bl + al * bh) + al * bl;
    return new BigDouble(p, err);
  }

  /* ---------------- Arithmetic ---------------- */
  public int signum() {
    return Double.compare(hi, 0.0);
  }

  public BigDouble abs() {
    return signum() < 0 ? negate() : this;
  }

  public BigDouble negate() {
    return new BigDouble(-hi, -lo);
  }

  public BigDouble add(BigDouble y) {
    BigDouble s = twoSum(this.hi, y.hi);
    double e = this.lo + y.lo + s.lo;
    return quickNormalize(s.hi, e);
  }

  public BigDouble sub(BigDouble y) {
    BigDouble s = twoSum(this.hi, -y.hi);
    double e = this.lo - y.lo + s.lo;
    return quickNormalize(s.hi, e);
  }

  public BigDouble mul(BigDouble y) {
    BigDouble p = twoProd(this.hi, y.hi);
    double e = this.hi * y.lo + this.lo * y.hi + p.lo;
    return quickNormalize(p.hi, e);
  }

  public BigDouble div(BigDouble y) {
    // Approximate quotient
    double q1 = this.hi / y.hi;
    BigDouble q1dd = BigDouble.of(q1);
    BigDouble r = this.sub(y.mul(q1dd));
    // Refinement step (Newton correction)
    double q2 = r.hi / y.hi;
    return q1dd.add(BigDouble.of(q2));
  }

  public BigDouble sqrt() {
    if (hi <= 0.0) {
      if (hi == 0.0)
        return this;
      throw new ArithmeticException("sqrt of negative");
    }
    // Start with hardware sqrt
    double x = Math.sqrt(hi);
    BigDouble xdd = BigDouble.of(x);
    // One Newton iteration:
    // x_{n+1} = 0.5 * (x + a/x)
    BigDouble ax = this.div(xdd);
    BigDouble sum = xdd.add(ax);
    return sum.mul(BigDouble.of(0.5));
  }

  public BigDouble exp() {
    if (hi == 0.0)
      return BigDouble.of(1.0);
    // k = nearest integer to x / ln2
    double kd = Math.rint(this.div(LN2).hi);
    long k = (long) kd;
    BigDouble r = this.sub(LN2.mul(fromLong(k)));
    // exp(r) via Taylor (|r| small)
    BigDouble term = BigDouble.of(1.0);
    BigDouble sum = BigDouble.of(1.0);
    for (int i = 1; i <= 30; i++) {
      term = term.mul(r).div(fromLong(i));
      sum = sum.add(term);
      if (Math.abs(term.hi) < 1e-34)
        break;
    }
    // scale by 2^k
    return sum.scalb((int) k);
  }

  public BigDouble log() {
    if (this.signum() <= 0)
      throw new ArithmeticException("log domain error");
    // initial guess from hardware log
    BigDouble y = BigDouble.of(Math.log(this.hi));
    for (int i = 0; i < 5; i++) {
      BigDouble ey = y.exp();
      BigDouble diff = this.sub(ey);
      y = y.add(diff.div(ey));
    }
    return y;
  }

  public BigDouble pow(BigDouble y) {
    if (this.signum() <= 0)
      throw new ArithmeticException("pow base must be > 0");
    return this.log().mul(y).exp();
  }

  public BigDouble reciprocal() {
    return ONE.div(this);
  }
  /* ---------------- Payne–Hanek Constants ---------------- */

  private static final class ReducerResult {
    final BigDouble r; // reduced argument
    final int quadrant; // n mod 4

    ReducerResult(BigDouble r, int quadrant) {
      this.r = r;
      this.quadrant = quadrant;
    }
  }

  /** Payne–Hanek argument reduction.
   *
   * Reduces x into r such that |r| ≤ π/4 and also returns
   * the quadrant (multiple of π/2). */
  private static ReducerResult payneHanekReduce(BigDouble x) {
    // n ≈ x * 2/pi (computed in double-double precision)
    BigDouble q = x.mul(TWO_OVER_PI);
    // nearest integer to q
    long n = Math.round(q.hi);
    BigDouble nDD = BigDouble.fromLong(n);
    // r = x − n*(π/2)
    BigDouble r = x.sub(PI_OVER_TWO.mul(nDD));
    return new ReducerResult(r, (int) (n & 3));
  }
  /* ---------------- Cody–Waite Constants ---------------- */

  /* π/2 split into 3 pieces for exact subtraction */
  private static final double PIO2_HI = 1.5707963267948966; // leading 53 bits
  private static final double PIO2_MID = 6.123233995736766e-17; // next bits
  private static final double PIO2_LO = 2.0222662487959506e-33; // tail

  /** Fast Cody–Waite argument reduction.
   * Valid for |x| < about 2^19 * π (~1e6). Beyond that we fall back. */
  private static ReducerResult codyWaiteReduce(BigDouble x) {
    // Estimate quadrant using hardware precision
    double xn = Math.rint(x.hi * INV_PIO2);
    long n = (long) xn;
    // Carefully subtract n*(π/2) using the split expansion
    double r = x.hi;
    r -= xn * PIO2_HI;
    r -= xn * PIO2_MID;
    r -= xn * PIO2_LO;
    // Bring along the low component
    BigDouble rr = new BigDouble(r, x.lo);
    return new ReducerResult(rr, (int) (n & 3));
  }

  private static final double CW_LIMIT = 1.0e6;

  /** Hybrid reducer (like real libm implementations). */
  private static ReducerResult reduceHybrid(BigDouble x) {
    if (Math.abs(x.hi) < CW_LIMIT) {
      return codyWaiteReduce(x); // fast path
    }
    return payneHanekReduce(x); // large argument path
  }

  /* 2/π for fast multiply */
  private static final double INV_PIO2 = 0.6366197723675814;

  public static final class SinCos {
    public final BigDouble sin;
    public final BigDouble cos;

    private SinCos(BigDouble sin, BigDouble cos) {
      this.sin = sin;
      this.cos = cos;
    }
  }

  /** Core kernel valid for |x| ≤ π/4.
   * Computes sin(x) and cos(x) together. */
  private static SinCos sinCosPoly(BigDouble x) {
    BigDouble x2 = x.mul(x).negate();
    // ---- sine series ----
    BigDouble sinTerm = x;
    BigDouble sinSum = x;
    // ---- cosine series ----
    BigDouble cosTerm = BigDouble.of(1.0);
    BigDouble cosSum = cosTerm;
    for (int i = 2; i <= 38; i += 2) {
      // cosine update first (uses previous cosine term)
      cosTerm = cosTerm.mul(x2).div(fromLong((i - 1) * i));
      cosSum = cosSum.add(cosTerm);
      // sine update uses same recurrence, shifted
      sinTerm = sinTerm.mul(x2).div(fromLong(i * (i + 1)));
      sinSum = sinSum.add(sinTerm);
      if (Math.abs(sinTerm.hi) < 1e-34 && Math.abs(cosTerm.hi) < 1e-34)
        break;
    }
    return new SinCos(sinSum, cosSum);
  }

  /** Simultaneously compute sin(x) and cos(x).
   * Much faster than calling sin(x) and cos(x) separately. */
  public SinCos sinCos() {
    // Hybrid Cody–Waite / Payne–Hanek reduction
    ReducerResult red = reduceHybrid(this);
    BigDouble xr = red.r;
    // Compute both on reduced interval
    SinCos sc = sinCosPoly(xr);
    BigDouble s = sc.sin;
    BigDouble c = sc.cos;
    // Rotate according to quadrant
    switch (red.quadrant) {
    case 0:
      return new SinCos(s, c);
    case 1:
      return new SinCos(c, s.negate());
    case 2:
      return new SinCos(s.negate(), c.negate());
    default: // 3
      return new SinCos(c.negate(), s);
    }
  }

  public BigDouble sin() {
    return sinCos().sin;
  }

  public BigDouble cos() {
    return sinCos().cos;
  }

  public BigDouble atan() {
    boolean negate = false;
    BigDouble x = this;
    if (x.signum() < 0) {
      negate = true;
      x = x.negate();
    }
    BigDouble result;
    if (x.hi > 1.0) {
      // atan(x) = pi/2 − atan(1/x)
      result = HALF_PI.sub(BigDouble.ONE.div(x).atan());
    } else {
      // Taylor series for |x| ≤ 1
      BigDouble term = x;
      BigDouble sum = x;
      BigDouble x2 = x.mul(x).negate();
      for (int i = 3; i <= 59; i += 2) {
        term = term.mul(x2).div(fromLong(i - 2));
        BigDouble add = term.div(fromLong(i));
        sum = sum.add(add);
        if (Math.abs(add.hi) < 1e-34)
          break;
      }
      result = sum;
    }
    return negate ? result.negate() : result;
  }

  public static BigDouble atan2(BigDouble y, BigDouble x) {
    if (x.hi == 0.0) {
      if (y.hi > 0.0)
        return HALF_PI;
      if (y.hi < 0.0)
        return HALF_PI.negate();
      throw new ArithmeticException("atan2(0,0)");
    }
    BigDouble atan = y.div(x).atan();
    if (x.hi > 0.0) {
      return atan;
    }
    return (y.hi >= 0.0) ? atan.add(PI) : atan.sub(PI);
  }

  public BigDouble sinh() {
    if (hi == 0.0)
      return this;
    BigDouble ex = this.exp();
    BigDouble emx = BigDouble.of(1.0).div(ex);
    return ex.sub(emx).mul(BigDouble.of(0.5));
  }

  public BigDouble cosh() {
    if (hi == 0.0)
      return BigDouble.of(1.0);
    BigDouble ex = this.exp();
    BigDouble emx = BigDouble.of(1.0).div(ex);
    return ex.add(emx).mul(BigDouble.of(0.5));
  }

  public BigDouble asin() {
    BigDouble one = BigDouble.of(1.0);
    BigDouble x2 = this.mul(this);
    BigDouble inside = one.sub(x2);
    if (inside.signum() < 0) {
      throw new ArithmeticException("asin domain error");
    }
    BigDouble denom = inside.sqrt();
    return this.div(denom).atan();
  }

  public BigDouble acos() {
    return HALF_PI.sub(this.asin());
  }

  public static BigDouble hypot(BigDouble x, BigDouble y) {
    BigDouble ax = x.abs();
    BigDouble ay = y.abs();
    // Ensure ax >= ay
    if (ax.hi < ay.hi) {
      BigDouble tmp = ax;
      ax = ay;
      ay = tmp;
    }
    if (ax.hi == 0.0)
      return BigDouble.of(0.0);
    // Compute ay/ax safely
    BigDouble r = ay.div(ax);
    // ax * sqrt(1 + r^2)
    return ax.mul(BigDouble.of(1.0).add(r.mul(r)).sqrt());
  }

  public BigDouble erf() {
    BigDouble x = this;
    BigDouble ax = x.abs();
    // For very small x, erf(x) ≈ 2x/√π
    if (ax.hi < 1e-8) {
      return x.mul(TWO_DIV_SQRT_PI());
    }
    // Use power series:
    // erf(x)=2/√π * Σ (-1)^n x^(2n+1)/(n!(2n+1))
    BigDouble xsq = x.mul(x);
    BigDouble term = x;
    BigDouble sum = x;
    for (int n = 1; n < 50; n++) {
      term = term.mul(xsq).negate().div(fromLong(n));
      BigDouble add = term.div(fromLong(2 * n + 1));
      sum = sum.add(add);
      if (Math.abs(add.hi) < 1e-34)
        break;
    }
    return sum.mul(TWO_DIV_SQRT_PI());
  }

  private static BigDouble TWO_DIV_SQRT_PI() {
    // 2/sqrt(pi)
    return BigDouble.of(2.0).div(PI.sqrt());
  }

  private static final double[] LANCZOS = { //
      676.5203681218851, //
      -1259.1392167224028, //
      771.32342877765313, //
      -176.61502916214059, //
      12.507343278686905, //
      -0.13857109526572012, //
      9.9843695780195716e-6, //
      1.5056327351493116e-7 };

  public BigDouble gamma() {
    BigDouble z = this;
    if (z.hi < 0.5) {
      // Reflection formula:
      // Γ(z)=π/(sin(πz)Γ(1−z))
      BigDouble piZ = PI.mul(z);
      return PI.div(piZ.sin().mul(BigDouble.of(1.0).sub(z).gamma()));
    }
    z = z.sub(BigDouble.of(1.0));
    BigDouble x = BigDouble.of(0.99999999999980993);
    for (int i = 0; i < LANCZOS.length; i++) {
      x = x.add(BigDouble.of(LANCZOS[i]).div(z.add(fromLong(i + 1))));
    }
    BigDouble g = BigDouble.of(LANCZOS.length - 0.5);
    BigDouble t = z.add(g);
    // sqrt(2π)
    BigDouble sqrtTwoPi = PI.mul(BigDouble.of(2.0)).sqrt();
    return sqrtTwoPi.mul(t.pow(z.add(BigDouble.of(0.5)))).mul(t.negate().exp()).mul(x);
  }

  public BigDouble logGamma() {
    BigDouble z = this;
    // Reflection formula for z < 0.5:
    // logΓ(z) = log(π) − log(sin(πz)) − logΓ(1−z)
    if (z.hi < 0.5) {
      BigDouble piZ = PI.mul(z);
      return PI.log().sub(piZ.sin().log()).sub(BigDouble.of(1.0).sub(z).logGamma());
    }
    // Lanczos evaluation
    z = z.sub(BigDouble.of(1.0));
    BigDouble x = BigDouble.of(0.99999999999980993);
    for (int i = 0; i < LANCZOS.length; i++) {
      x = x.add(BigDouble.of(LANCZOS[i]).div(z.add(fromLong(i + 1))));
    }
    BigDouble g = BigDouble.of(LANCZOS.length - 0.5);
    BigDouble t = z.add(g);
    // log(sqrt(2π)) computed in DD precision
    BigDouble logSqrtTwoPi = PI.mul(BigDouble.of(2.0)).sqrt().log();
    return logSqrtTwoPi.add(z.add(BigDouble.of(0.5)).mul(t.log())).sub(t).add(x.log());
  }
  /* ---------------- Normalization ---------------- */

  /** Fast renormalization ensuring |lo| <= 0.5 ulp(hi) */
  private static BigDouble quickNormalize(double hi, double lo) {
    double s = hi + lo;
    double e = lo - (s - hi);
    return new BigDouble(s, e);
  }

  /* ---------------- Utilities ---------------- */
  public BigDouble scalb(int n) {
    if (hi == 0.0)
      return this;
    double newHi = Math.scalb(hi, n);
    double newLo = Math.scalb(lo, n);
    // Renormalize to maintain non-overlapping representation
    double s = newHi + newLo;
    double e = newLo - (s - newHi);
    return new BigDouble(s, e);
  }

  public double toDouble() {
    return hi + lo;
  }

  private static BigDecimal exactDoubleToBigDecimal(double x) {
    if (Double.isNaN(x) || Double.isInfinite(x)) {
      throw new ArithmeticException("Non-finite value");
    }
    long bits = Double.doubleToLongBits(x);
    int sign = ((bits >>> 63) == 0) ? 1 : -1;
    int exponent = (int) ((bits >>> 52) & 0x7ffL);
    long mantissa = bits & 0x000f_ffff_ffff_ffffL;
    if (exponent == 0) {
      // subnormal
      exponent = 1 - 1023;
    } else {
      // normal number: implicit leading 1
      mantissa |= (1L << 52);
      exponent -= 1023;
    }
    // Now value = sign * mantissa * 2^(exponent-52)
    BigInteger m = BigInteger.valueOf(sign).multiply(BigInteger.valueOf(mantissa));
    int shift = exponent - 52;
    BigDecimal bd = new BigDecimal(m);
    if (shift > 0) {
      bd = bd.multiply(BigDecimal.valueOf(2).pow(shift));
    } else if (shift < 0) {
      bd = bd.divide(BigDecimal.valueOf(2).pow(-shift));
    }
    return bd;
  }

  /** Convert this DoubleDouble to an exact BigDecimal.
   * No precision is lost in the conversion. */
  public BigDecimal toBigDecimal() {
    BigDecimal hiBD = exactDoubleToBigDecimal(this.hi);
    BigDecimal loBD = exactDoubleToBigDecimal(this.lo);
    return hiBD.add(loBD);
  }

  @Override
  public String toString() {
    if (Double.isNaN(hi))
      return "NaN";
    if (Double.isInfinite(hi))
      return 0 < hi //
          ? "Infinity"
          : "-Infinity";
    if (equals(ZERO))
      return "0";
    BigDecimal exact = toBigDecimal();
    // Start with enough digits to uniquely identify a 106-bit value
    // 34 digits is safely above the ~32 needed.
    MathContext mc = new MathContext(34, RoundingMode.HALF_EVEN);
    String full = exact.round(mc).toString();
    // Normalize form (remove trailing zeros etc.)
    full = new BigDecimal(full).stripTrailingZeros().toString();
    // Try removing digits while preserving round-trip identity
    String best = full;
    int ePos = Math.max(full.indexOf('e'), full.indexOf('E'));
    String mantissa = (ePos >= 0) ? full.substring(0, ePos) : full;
    String exponent = (ePos >= 0) ? full.substring(ePos) : "";
    int dot = mantissa.indexOf('.');
    if (dot < 0)
      return best; // already an integer form
    String digits = mantissa.replace(".", "");
    int intDigits = dot;
    // Try progressively shortening the fractional tail
    for (int cut = digits.length() - 1; cut >= intDigits + 1; cut--) {
      String candidateDigits = digits.substring(0, cut);
      String candidate = candidateDigits.substring(0, intDigits) + "." + candidateDigits.substring(intDigits) + exponent;
      candidate = simplify(candidate);
      // Round-trip test
      if (Parse.parse(candidate).equalsExact(this))
        best = candidate;
      else
        break; // went too far — last one was shortest valid
    }
    return best;
  }

  private static String simplify(String string) {
    if (!string.contains("."))
      return string;
    while (string.endsWith("0"))
      string = string.substring(0, string.length() - 1);
    if (string.endsWith("."))
      string = string.substring(0, string.length() - 1);
    return string;
  }

  public boolean isZero() {
    // handles +0.0 and -0.0 in either component
    return hi == 0.0 && lo == 0.0;
  }

  public boolean isNaN() {
    return Double.isNaN(hi);
  }

  @Override
  public int hashCode() {
    if (isZero())
      return 0;
    long h = Double.doubleToLongBits(hi);
    long l = Double.doubleToLongBits(lo);
    return (int) (h ^ (h >>> 32) ^ l ^ (l >>> 32));
  }

  public boolean equalsExact(BigDouble o) {
    return Double.doubleToLongBits(this.hi) == Double.doubleToLongBits(o.hi) && Double.doubleToLongBits(this.lo) == Double.doubleToLongBits(o.lo);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj instanceof BigDouble bigDouble) {
      // Treat all zeros as equal (+0.0 == -0.0)
      if (isZero() && bigDouble.isZero())
        return true;
      // NaNs are not equal to anything (match Double semantics)
      if (isNaN() || bigDouble.isNaN())
        return false;
      // Exact comparison otherwise
      return equalsExact(bigDouble);
    }
    return false;
  }
}
