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
public class DoubleDouble {
  private static final double SPLIT = 134217729.0; // 2^27 + 1
  public static final DoubleDouble ZERO = new DoubleDouble(0.0, 0.0);
  public static final DoubleDouble ONE = new DoubleDouble(1.0, 0.0);
  public static final DoubleDouble TWO = new DoubleDouble(2.0, 0.0);
  public static final DoubleDouble HALF = new DoubleDouble(0.5, 0.0);
  public static final DoubleDouble PI = new DoubleDouble(3.141592653589793116, 1.224646799147353207e-16);
  public static final DoubleDouble TWO_PI = PI.mul(fromLong(2));
  public static final DoubleDouble HALF_PI = PI.mul(DoubleDouble.of(0.5));
  public static final DoubleDouble E = new DoubleDouble(2.718281828459045091, 1.445646891729250158e-16);
  public static final DoubleDouble LN2 = new DoubleDouble(0.693147180559945286, 2.319046813846299558e-17);
  /* 2/pi split into double-double pieces.
   * These are derived from a >200-bit expansion and rounded
   * so that multiplication is exact in double-double arithmetic. */
  private static final DoubleDouble TWO_OVER_PI = new DoubleDouble(0.63661977236758138243, -3.9357353350364971764e-17);
  /* π/2 split the same way */
  private static final DoubleDouble PI_OVER_TWO = new DoubleDouble(1.57079632679489655800, 6.1232339957367660359e-17);

  public static DoubleDouble of(double x) {
    return new DoubleDouble(x, 0.0);
  }

  public static DoubleDouble fromLong(long x) {
    return new DoubleDouble((double) x, 0.0);
  }

  /* ---------------- Constructors ---------------- */
  private final double hi;
  private final double lo;

  @PackageTestAccess
  DoubleDouble(double hi, double lo) {
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
  private static DoubleDouble twoSum(double a, double b) {
    double s = a + b;
    double bb = s - a;
    double err = (a - (s - bb)) + (b - bb);
    return new DoubleDouble(s, err);
  }

  /** Computes a * b exactly as (prod, err). */
  private static DoubleDouble twoProd(double a, double b) {
    double p = a * b;
    double ca = SPLIT * a;
    double ah = ca - (ca - a);
    double al = a - ah;
    double cb = SPLIT * b;
    double bh = cb - (cb - b);
    double bl = b - bh;
    double err = ((ah * bh - p) + ah * bl + al * bh) + al * bl;
    return new DoubleDouble(p, err);
  }

  /* ---------------- Arithmetic ---------------- */
  public int signum() {
    return Double.compare(hi, 0.0);
  }

  public DoubleDouble abs() {
    return signum() < 0 ? negate() : this;
  }

  public DoubleDouble negate() {
    return new DoubleDouble(-hi, -lo);
  }

  public DoubleDouble add(DoubleDouble y) {
    DoubleDouble s = twoSum(this.hi, y.hi);
    double e = this.lo + y.lo + s.lo;
    return quickNormalize(s.hi, e);
  }

  public DoubleDouble sub(DoubleDouble y) {
    DoubleDouble s = twoSum(this.hi, -y.hi);
    double e = this.lo - y.lo + s.lo;
    return quickNormalize(s.hi, e);
  }

  public DoubleDouble mul(DoubleDouble y) {
    DoubleDouble p = twoProd(this.hi, y.hi);
    double e = this.hi * y.lo + this.lo * y.hi + p.lo;
    return quickNormalize(p.hi, e);
  }

  public DoubleDouble div(DoubleDouble y) {
    // Approximate quotient
    double q1 = this.hi / y.hi;
    DoubleDouble q1dd = DoubleDouble.of(q1);
    DoubleDouble r = this.sub(y.mul(q1dd));
    // Refinement step (Newton correction)
    double q2 = r.hi / y.hi;
    return q1dd.add(DoubleDouble.of(q2));
  }

  public DoubleDouble sqrt() {
    if (hi <= 0.0) {
      if (hi == 0.0)
        return this;
      throw new ArithmeticException("sqrt of negative");
    }
    // Start with hardware sqrt
    double x = Math.sqrt(hi);
    DoubleDouble xdd = DoubleDouble.of(x);
    // One Newton iteration:
    // x_{n+1} = 0.5 * (x + a/x)
    DoubleDouble ax = this.div(xdd);
    DoubleDouble sum = xdd.add(ax);
    return sum.mul(DoubleDouble.of(0.5));
  }

  public DoubleDouble exp() {
    if (hi == 0.0)
      return DoubleDouble.of(1.0);
    // k = nearest integer to x / ln2
    double kd = Math.rint(this.div(LN2).hi);
    long k = (long) kd;
    DoubleDouble r = this.sub(LN2.mul(fromLong(k)));
    // exp(r) via Taylor (|r| small)
    DoubleDouble term = DoubleDouble.of(1.0);
    DoubleDouble sum = DoubleDouble.of(1.0);
    for (int i = 1; i <= 30; i++) {
      term = term.mul(r).div(fromLong(i));
      sum = sum.add(term);
      if (Math.abs(term.hi) < 1e-34)
        break;
    }
    // scale by 2^k
    return sum.scalb((int) k);
  }

  public DoubleDouble log() {
    if (this.signum() <= 0)
      throw new ArithmeticException("log domain error");
    // initial guess from hardware log
    DoubleDouble y = DoubleDouble.of(Math.log(this.hi));
    for (int i = 0; i < 5; i++) {
      DoubleDouble ey = y.exp();
      DoubleDouble diff = this.sub(ey);
      y = y.add(diff.div(ey));
    }
    return y;
  }

  public DoubleDouble pow(DoubleDouble y) {
    if (this.signum() <= 0)
      throw new ArithmeticException("pow base must be > 0");
    return this.log().mul(y).exp();
  }
  /* ---------------- Payne–Hanek Constants ---------------- */

  private static final class ReducerResult {
    final DoubleDouble r; // reduced argument
    final int quadrant; // n mod 4

    ReducerResult(DoubleDouble r, int quadrant) {
      this.r = r;
      this.quadrant = quadrant;
    }
  }

  /** Payne–Hanek argument reduction.
   *
   * Reduces x into r such that |r| ≤ π/4 and also returns
   * the quadrant (multiple of π/2). */
  private static ReducerResult payneHanekReduce(DoubleDouble x) {
    // n ≈ x * 2/pi (computed in double-double precision)
    DoubleDouble q = x.mul(TWO_OVER_PI);
    // nearest integer to q
    long n = Math.round(q.hi);
    DoubleDouble nDD = DoubleDouble.fromLong(n);
    // r = x − n*(π/2)
    DoubleDouble r = x.sub(PI_OVER_TWO.mul(nDD));
    return new ReducerResult(r, (int) (n & 3));
  }
  /* ---------------- Cody–Waite Constants ---------------- */

  /* π/2 split into 3 pieces for exact subtraction */
  private static final double PIO2_HI = 1.5707963267948966; // leading 53 bits
  private static final double PIO2_MID = 6.123233995736766e-17; // next bits
  private static final double PIO2_LO = 2.0222662487959506e-33; // tail

  /** Fast Cody–Waite argument reduction.
   * Valid for |x| < about 2^19 * π (~1e6). Beyond that we fall back. */
  private static ReducerResult codyWaiteReduce(DoubleDouble x) {
    // Estimate quadrant using hardware precision
    double xn = Math.rint(x.hi * INV_PIO2);
    long n = (long) xn;
    // Carefully subtract n*(π/2) using the split expansion
    double r = x.hi;
    r -= xn * PIO2_HI;
    r -= xn * PIO2_MID;
    r -= xn * PIO2_LO;
    // Bring along the low component
    DoubleDouble rr = new DoubleDouble(r, x.lo);
    return new ReducerResult(rr, (int) (n & 3));
  }

  private static final double CW_LIMIT = 1.0e6;

  /** Hybrid reducer (like real libm implementations). */
  private static ReducerResult reduceHybrid(DoubleDouble x) {
    if (Math.abs(x.hi) < CW_LIMIT) {
      return codyWaiteReduce(x); // fast path
    } else {
      return payneHanekReduce(x); // large argument path
    }
  }

  /* 2/π for fast multiply */
  private static final double INV_PIO2 = 0.6366197723675814;

  public static final class SinCos {
    public final DoubleDouble sin;
    public final DoubleDouble cos;

    private SinCos(DoubleDouble sin, DoubleDouble cos) {
      this.sin = sin;
      this.cos = cos;
    }
  }

  /** Core kernel valid for |x| ≤ π/4.
   * Computes sin(x) and cos(x) together. */
  private static SinCos sinCosPoly(DoubleDouble x) {
    DoubleDouble x2 = x.mul(x).negate();
    // ---- sine series ----
    DoubleDouble sinTerm = x;
    DoubleDouble sinSum = x;
    // ---- cosine series ----
    DoubleDouble cosTerm = DoubleDouble.of(1.0);
    DoubleDouble cosSum = cosTerm;
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
    DoubleDouble xr = red.r;
    // Compute both on reduced interval
    SinCos sc = sinCosPoly(xr);
    DoubleDouble s = sc.sin;
    DoubleDouble c = sc.cos;
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

  public DoubleDouble sin() {
    return sinCos().sin;
  }

  public DoubleDouble cos() {
    return sinCos().cos;
  }

  public DoubleDouble atan() {
    boolean negate = false;
    DoubleDouble x = this;
    if (x.signum() < 0) {
      negate = true;
      x = x.negate();
    }
    DoubleDouble result;
    if (x.hi > 1.0) {
      // atan(x) = pi/2 − atan(1/x)
      result = HALF_PI.sub(DoubleDouble.of(1.0).div(x).atan());
    } else {
      // Taylor series for |x| ≤ 1
      DoubleDouble term = x;
      DoubleDouble sum = x;
      DoubleDouble x2 = x.mul(x).negate();
      for (int i = 3; i <= 59; i += 2) {
        term = term.mul(x2).div(fromLong(i - 2));
        DoubleDouble add = term.div(fromLong(i));
        sum = sum.add(add);
        if (Math.abs(add.hi) < 1e-34)
          break;
      }
      result = sum;
    }
    return negate ? result.negate() : result;
  }

  public static DoubleDouble atan2(DoubleDouble y, DoubleDouble x) {
    if (x.hi == 0.0) {
      if (y.hi > 0.0)
        return HALF_PI;
      if (y.hi < 0.0)
        return HALF_PI.negate();
      throw new ArithmeticException("atan2(0,0)");
    }
    DoubleDouble atan = y.div(x).atan();
    if (x.hi > 0.0) {
      return atan;
    } else {
      return (y.hi >= 0.0) ? atan.add(PI) : atan.sub(PI);
    }
  }

  public DoubleDouble sinh() {
    if (hi == 0.0)
      return this;
    DoubleDouble ex = this.exp();
    DoubleDouble emx = DoubleDouble.of(1.0).div(ex);
    return ex.sub(emx).mul(DoubleDouble.of(0.5));
  }

  public DoubleDouble cosh() {
    if (hi == 0.0)
      return DoubleDouble.of(1.0);
    DoubleDouble ex = this.exp();
    DoubleDouble emx = DoubleDouble.of(1.0).div(ex);
    return ex.add(emx).mul(DoubleDouble.of(0.5));
  }

  public DoubleDouble asin() {
    DoubleDouble one = DoubleDouble.of(1.0);
    DoubleDouble x2 = this.mul(this);
    DoubleDouble inside = one.sub(x2);
    if (inside.signum() < 0) {
      throw new ArithmeticException("asin domain error");
    }
    DoubleDouble denom = inside.sqrt();
    return this.div(denom).atan();
  }

  public DoubleDouble acos() {
    return HALF_PI.sub(this.asin());
  }

  public static DoubleDouble hypot(DoubleDouble x, DoubleDouble y) {
    DoubleDouble ax = x.abs();
    DoubleDouble ay = y.abs();
    // Ensure ax >= ay
    if (ax.hi < ay.hi) {
      DoubleDouble tmp = ax;
      ax = ay;
      ay = tmp;
    }
    if (ax.hi == 0.0)
      return DoubleDouble.of(0.0);
    // Compute ay/ax safely
    DoubleDouble r = ay.div(ax);
    // ax * sqrt(1 + r^2)
    return ax.mul(DoubleDouble.of(1.0).add(r.mul(r)).sqrt());
  }

  public DoubleDouble erf() {
    DoubleDouble x = this;
    DoubleDouble ax = x.abs();
    // For very small x, erf(x) ≈ 2x/√π
    if (ax.hi < 1e-8) {
      return x.mul(TWO_DIV_SQRT_PI());
    }
    // Use power series:
    // erf(x)=2/√π * Σ (-1)^n x^(2n+1)/(n!(2n+1))
    DoubleDouble xsq = x.mul(x);
    DoubleDouble term = x;
    DoubleDouble sum = x;
    for (int n = 1; n < 50; n++) {
      term = term.mul(xsq).negate().div(fromLong(n));
      DoubleDouble add = term.div(fromLong(2 * n + 1));
      sum = sum.add(add);
      if (Math.abs(add.hi) < 1e-34)
        break;
    }
    return sum.mul(TWO_DIV_SQRT_PI());
  }

  private static DoubleDouble TWO_DIV_SQRT_PI() {
    // 2/sqrt(pi)
    return DoubleDouble.of(2.0).div(PI.sqrt());
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

  public DoubleDouble gamma() {
    DoubleDouble z = this;
    if (z.hi < 0.5) {
      // Reflection formula:
      // Γ(z)=π/(sin(πz)Γ(1−z))
      DoubleDouble piZ = PI.mul(z);
      return PI.div(piZ.sin().mul(DoubleDouble.of(1.0).sub(z).gamma()));
    }
    z = z.sub(DoubleDouble.of(1.0));
    DoubleDouble x = DoubleDouble.of(0.99999999999980993);
    for (int i = 0; i < LANCZOS.length; i++) {
      x = x.add(DoubleDouble.of(LANCZOS[i]).div(z.add(fromLong(i + 1))));
    }
    DoubleDouble g = DoubleDouble.of(LANCZOS.length - 0.5);
    DoubleDouble t = z.add(g);
    // sqrt(2π)
    DoubleDouble sqrtTwoPi = PI.mul(DoubleDouble.of(2.0)).sqrt();
    return sqrtTwoPi.mul(t.pow(z.add(DoubleDouble.of(0.5)))).mul(t.negate().exp()).mul(x);
  }

  public DoubleDouble logGamma() {
    DoubleDouble z = this;
    // Reflection formula for z < 0.5:
    // logΓ(z) = log(π) − log(sin(πz)) − logΓ(1−z)
    if (z.hi < 0.5) {
      DoubleDouble piZ = PI.mul(z);
      return PI.log().sub(piZ.sin().log()).sub(DoubleDouble.of(1.0).sub(z).logGamma());
    }
    // Lanczos evaluation
    z = z.sub(DoubleDouble.of(1.0));
    DoubleDouble x = DoubleDouble.of(0.99999999999980993);
    for (int i = 0; i < LANCZOS.length; i++) {
      x = x.add(DoubleDouble.of(LANCZOS[i]).div(z.add(fromLong(i + 1))));
    }
    DoubleDouble g = DoubleDouble.of(LANCZOS.length - 0.5);
    DoubleDouble t = z.add(g);
    // log(sqrt(2π)) computed in DD precision
    DoubleDouble logSqrtTwoPi = PI.mul(DoubleDouble.of(2.0)).sqrt().log();
    return logSqrtTwoPi.add(z.add(DoubleDouble.of(0.5)).mul(t.log())).sub(t).add(x.log());
  }
  /* ---------------- Normalization ---------------- */

  /** Fast renormalization ensuring |lo| <= 0.5 ulp(hi) */
  private static DoubleDouble quickNormalize(double hi, double lo) {
    double s = hi + lo;
    double e = lo - (s - hi);
    return new DoubleDouble(s, e);
  }

  /* ---------------- Utilities ---------------- */
  public DoubleDouble scalb(int n) {
    if (hi == 0.0)
      return this;
    double newHi = Math.scalb(hi, n);
    double newLo = Math.scalb(lo, n);
    // Renormalize to maintain non-overlapping representation
    double s = newHi + newLo;
    double e = newLo - (s - newHi);
    return new DoubleDouble(s, e);
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

  public String toDecimalString(int digits) {
    MathContext mc = new MathContext(digits, RoundingMode.HALF_EVEN);
    return toBigDecimal().round(mc).toPlainString();
  }

  @Override
  public String toString() {
    return toShortestString();
  }

  public String toShortestString() {
    if (Double.isNaN(hi))
      return "NaN";
    if (Double.isInfinite(hi))
      return (hi > 0) ? "Infinity" : "-Infinity";
    if (hi == 0.0 && lo == 0.0)
      return "0";
    // 1️⃣ Exact decimal value
    BigDecimal exact = toBigDecimal();
    // 2️⃣ Start with enough digits to uniquely identify a 106-bit value
    // 34 digits is safely above the ~32 needed.
    MathContext mc = new MathContext(34, RoundingMode.HALF_EVEN);
    String full = exact.round(mc).toString();
    // Normalize form (remove trailing zeros etc.)
    full = new BigDecimal(full).stripTrailingZeros().toString();
    // 3️⃣ Try removing digits while preserving round-trip identity
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
      candidate = stripTrailingDotZeros(candidate);
      // 4️⃣ Round-trip test
      DoubleDouble reparsed = Parse.parse(candidate);
      if (this.equalsExact(reparsed)) {
        best = candidate;
      } else {
        break; // went too far — last one was shortest valid
      }
    }
    return best;
  }

  public boolean equalsExact(DoubleDouble o) {
    return Double.doubleToLongBits(this.hi) == Double.doubleToLongBits(o.hi) && Double.doubleToLongBits(this.lo) == Double.doubleToLongBits(o.lo);
  }

  private static String stripTrailingDotZeros(String s) {
    if (!s.contains("."))
      return s;
    while (s.endsWith("0"))
      s = s.substring(0, s.length() - 1);
    if (s.endsWith("."))
      s = s.substring(0, s.length() - 1);
    return s;
  }

  public String toString(int precision) {
    if (Double.isNaN(hi))
      return "NaN";
    if (Double.isInfinite(hi))
      return (hi > 0) ? "Infinity" : "-Infinity";
    // Round the exact value to the requested significant digits
    MathContext mc = new MathContext(precision, RoundingMode.HALF_EVEN);
    BigDecimal bd = toBigDecimal().round(mc);
    // Remove trailing zeros to avoid ugly output like 1.2300000
    bd = bd.stripTrailingZeros();
    return bd.toString(); // uses scientific notation when appropriate
  }

  public String toHighPrecisionString() {
    // crude but useful diagnostic form
    return String.format("hi=%.17g lo=%.17g (≈ %.31g)", hi, lo, hi + lo);
  }

  public static void main(String[] args) {
    DoubleDouble a = DoubleDouble.of(1e16);
    DoubleDouble b = DoubleDouble.of(1.0);
    // This fails in double:
    double broken = (1e16 + 1.0) - 1e16;
    // This works:
    DoubleDouble precise = a.add(b).sub(a);
    System.out.println("double result      = " + broken);
    System.out.println("double-double      = " + precise.toHighPrecisionString());
    // π via sqrt example
    DoubleDouble two = DoubleDouble.of(2.0);
    DoubleDouble sqrt2 = two.sqrt();
    System.out.println("sqrt(2) ≈ " + sqrt2.toString());
    DoubleDouble x = DoubleDouble.of(1e16).add(DoubleDouble.of(1));
    DoubleDouble y = x.sub(DoubleDouble.of(1e16));
    System.out.println("DoubleDouble : " + y.toDecimalString(40));
    BigDecimal bd = y.toBigDecimal();
    System.out.println("BigDecimal   : " + bd.toPlainString());
  }
}
