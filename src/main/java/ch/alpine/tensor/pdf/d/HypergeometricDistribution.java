// code by jph
package ch.alpine.tensor.pdf.d;

import java.math.BigInteger;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.num.Binomial;
import ch.alpine.tensor.pdf.Distribution;

/** Quote from Mathematica:
 * "A hypergeometric distribution gives the distribution of the number of successes
 * in N draws from a population of size m_n containing n successes."
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/HypergeometricDistribution.html">HypergeometricDistribution</a> */
public class HypergeometricDistribution extends EvaluatedDiscreteDistribution {
  /** see the Mathematica documentation of HypergeometricDistribution
   * 
   * @param N number of draws
   * @param n number of successes
   * @param m_n population size
   * @return */
  public static Distribution of(int N, int n, int m_n) {
    Integers.requirePositive(N); // 0 < N
    Integers.requireLessEquals(N, m_n); // N <= m_n
    Integers.requirePositiveOrZero(n); // 0 <= n
    Integers.requireLessEquals(n, m_n); // n <= m_n
    return new HypergeometricDistribution(N, n, m_n);
  }

  // ---
  private final int N;
  private final int n;
  private final int m_n;
  private final Binomial binomial_n;
  private final Binomial binomial_m;
  private final Binomial binomial_m_n;

  private HypergeometricDistribution(int N, int n, int m_n) {
    this.N = N;
    this.n = n;
    this.m_n = m_n;
    int m = m_n - n;
    binomial_n = Binomial.of(n);
    binomial_m = Binomial.of(m);
    binomial_m_n = Binomial.of(m_n);
    build(Math.min(N, n));
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return RealScalar.of(N).multiply(RationalScalar.of(n, m_n));
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    // ((mpn - n) n (mpn - N) N) / ((-1 + mpn) mpn^2)
    Scalar rd1 = RationalScalar.of(m_n - n, m_n);
    Scalar rd2 = RationalScalar.of(m_n - N, m_n);
    // ( n N) / (-1 + mpn)
    Scalar rd3 = RationalScalar.of(N, m_n - 1);
    // ( n )
    Scalar rd4 = RationalScalar.of(n, 1);
    return rd1.multiply(rd2).multiply(rd3).multiply(rd4);
  }

  @Override // from DiscreteDistribution
  public BigInteger lowerBound() {
    return BigInteger.ZERO;
  }

  @Override // from AbstractDiscreteDistribution
  protected Scalar protected_p_equals(int x) {
    return x <= N && x <= n //
        ? binomial_n.over(x).multiply(binomial_m.over(N - x)).divide(binomial_m_n.over(N))
        : RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("HypergeometricDistribution", N, n, m_n);
  }
}
