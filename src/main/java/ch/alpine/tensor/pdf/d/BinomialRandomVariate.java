// code by cr and jph
package ch.alpine.tensor.pdf.d;

import java.io.Serializable;
import java.util.Random;
import java.util.stream.DoubleStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.MeanInterface;
import ch.alpine.tensor.pdf.RandomVariateInterface;
import ch.alpine.tensor.pdf.VarianceInterface;
import ch.alpine.tensor.pdf.c.NormalDistribution;

/** fallback option to robustly generate random variates from a
 * {@link BinomialDistribution} for any parameters n and p.
 * The complexity of a single random variate generation is O(n).
 * 
 * <p>For large n, and p away from 0, or 1, the option to approximate the
 * distribution as a {@link NormalDistribution} should be considered.
 * 
 * @see BinomialDistribution
 * @author Claudio Ruch */
/* package */ class BinomialRandomVariate implements Distribution, //
    MeanInterface, RandomVariateInterface, VarianceInterface, Serializable {
  private final int n;
  private final Scalar p;
  private final double p_success;

  public BinomialRandomVariate(int n, Scalar p) {
    this.n = n;
    this.p = p;
    p_success = p.number().doubleValue();
  }

  @Override // from RandomVariateInterface
  public Scalar randomVariate(Random random) {
    return RealScalar.of(DoubleStream.generate(random::nextDouble) //
        .limit(n) //
        .filter(value -> value < p_success) //
        .count());
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return RealScalar.of(n).multiply(p);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return mean().multiply(RealScalar.ONE.subtract(p));
  }
}
