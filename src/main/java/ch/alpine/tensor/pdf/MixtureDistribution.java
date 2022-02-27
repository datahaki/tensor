// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.d.CategoricalDistribution;
import ch.alpine.tensor.red.Mean;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MixtureDistribution.html">MixtureDistribution</a> */
public class MixtureDistribution implements Distribution, PDF, CDF, MeanInterface, //
    RandomVariateInterface, Serializable {
  /** @param unscaledPDF
   * @param distributions
   * @return */
  @SafeVarargs
  public static Distribution of(Tensor unscaledPDF, Distribution... distributions) {
    return new MixtureDistribution( //
        unscaledPDF, //
        Arrays.asList(distributions));
  }

  // ---
  private final CategoricalDistribution categoricalDistribution;
  private final List<Distribution> list;

  private MixtureDistribution(Tensor unscaledPDF, List<Distribution> list) {
    categoricalDistribution = CategoricalDistribution.fromUnscaledPDF(unscaledPDF);
    this.list = list;
  }

  private Scalar dot(Function<Distribution, Scalar> function) {
    AtomicInteger atomicInteger = new AtomicInteger();
    return list.stream() //
        .map(function) //
        .map(scalar -> scalar.multiply(categoricalDistribution.p_equals(atomicInteger.getAndIncrement()))) //
        .reduce(Scalar::add) //
        .orElseThrow();
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
