// code by jph
// credit to spencer
package ch.alpine.tensor.pdf.d;

import java.io.Serializable;
import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.MeanInterface;
import ch.alpine.tensor.pdf.RandomVariateInterface;
import ch.alpine.tensor.pdf.VarianceInterface;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Clips;

/** <a href="https://en.wikipedia.org/wiki/Poisson_binomial_distribution">wikipedia</a>
 * 
 * @see BinomialDistribution
 * @see BinomialRandomVariate */
public class PoissonBinomialDistribution implements Distribution, //
    RandomVariateInterface, MeanInterface, VarianceInterface, Serializable {
  /** Hint:
   * if p_vector consists of identical entries, the {@link BinomialDistribution}
   * or {@link BinomialRandomVariate} should be used instead.
   * 
   * @param p_vector with scalar entries in the interval [0, 1], the empty vector
   * is also permitted
   * @return
   * @throws Exception if any entry in given p_vector is outside the unit interval */
  public static Distribution of(Tensor p_vector) {
    Tensor p_result = Tensors.reserve(p_vector.length());
    int lowerBound = 0;
    for (Tensor _p : p_vector) {
      Scalar p = Clips.unit().requireInside((Scalar) _p);
      if (RealScalar.ONE.equals(p))
        ++lowerBound;
      else //
      if (Scalars.nonZero(p))
        p_result.append(p);
    }
    return new PoissonBinomialDistribution(lowerBound, p_result);
  }

  // ---
  private final int lowerBound;
  private final Tensor p_vector;

  private PoissonBinomialDistribution(int lowerBound, Tensor p_vector) {
    this.lowerBound = lowerBound;
    this.p_vector = p_vector;
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return RealScalar.of(lowerBound).add(Total.of(p_vector));
  }

  @Override // from RandomVariateInterface
  public Scalar randomVariate(Random random) {
    return RealScalar.of(lowerBound + p_vector.stream() //
        .map(Scalar.class::cast) //
        .map(Scalar::number) //
        .mapToDouble(Number::doubleValue) //
        .filter(p -> random.nextDouble() < p) //
        .count());
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return p_vector.stream() //
        .map(Scalar.class::cast) //
        .map(p -> RealScalar.ONE.subtract(p).multiply(p)) //
        .reduce(Scalar::add) //
        .orElse(RealScalar.ZERO);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("PoissonBinomialDistribution", p_vector);
  }
}
