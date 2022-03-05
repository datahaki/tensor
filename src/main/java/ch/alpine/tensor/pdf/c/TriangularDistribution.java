// code by jph
package ch.alpine.tensor.pdf.c;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.pow.Sqrt;

/** The triangular distribution is a special case of a {@link TrapezoidalDistribution}
 * 
 * <p>inspired by
 * <a href="https://en.wikipedia.org/wiki/Triangular_distribution">Triangular_distribution</a> */
public enum TriangularDistribution {
  ;
  /** @param a
   * @param b
   * @param c
   * @return
   * @throws Exception unless a <= b <= c and a < c */
  public static Distribution of(Scalar a, Scalar b, Scalar c) {
    return TrapezoidalDistribution.of(a, b, b, c);
  }

  /** @param a
   * @param b
   * @param c
   * @return
   * @throws Exception unless a <= b <= c and a < c */
  public static Distribution of(Number a, Number b, Number c) {
    return of(RealScalar.of(a), RealScalar.of(b), RealScalar.of(c));
  }

  /** the support of this distribution was
   * width = 2 * sqrt(6) * sigma
   * width = 2 * 2.44949 * sigma
   * 
   * @param mean
   * @param sigma standard deviation, strictly positive
   * @return */
  public static Distribution with(Scalar mean, Scalar sigma) {
    Scalar b = mean;
    Scalar d = sigma.multiply(Sqrt.FUNCTION.apply(RealScalar.of(6)));
    return of(b.subtract(d), b, b.add(d));
  }

  /** @param mean
   * @param sigma standard deviation, strictly positive
   * @return */
  public static Distribution with(Number mean, Number sigma) {
    return with(RealScalar.of(mean), RealScalar.of(sigma));
  }
}
