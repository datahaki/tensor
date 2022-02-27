// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.NormalizeTotal;
import ch.alpine.tensor.pdf.d.CategoricalDistribution;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Total;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MixtureDistribution.html">MixtureDistribution</a> */
public class MixtureDistribution implements Distribution, PDF, CDF, MeanInterface, //
    RandomVariateInterface, Serializable {
  /** @param weights
   * @param distributions
   * @return */
  public static Distribution of(Tensor weights, Distribution... distributions) {
    return new MixtureDistribution( //
        NormalizeTotal.FUNCTION.apply(weights), //
        Arrays.asList(distributions));
  }

  // ---
  private final Tensor weights;
  private final List<Distribution> list;
  private final CategoricalDistribution categoricalDistribution;

  private MixtureDistribution(Tensor unscaledPDF, List<Distribution> list) {
    categoricalDistribution = CategoricalDistribution.fromUnscaledPDF(unscaledPDF);
    Tolerance.CHOP.requireClose(Total.of(unscaledPDF), RealScalar.ONE);
    this.weights = unscaledPDF;
    this.list = list;
  }

  private Scalar dot(Function<Distribution, Scalar> f) {
    return (Scalar) weights.dot(Tensor.of(list.stream().map(f)));
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    return dot(distribution -> PDF.of(distribution).at(x));
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return dot(distribution -> CDF.of(distribution).p_lessThan(x));
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    return dot(distribution -> CDF.of(distribution).p_lessEquals(x));
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return dot(Mean::of);
  }

  @Override // from RandomVariateInterface
  public Scalar randomVariate(Random random) {
    int index = RandomVariate.of(categoricalDistribution, random).number().intValue();
    return RandomVariate.of(list.get(index), random);
  }
}
