// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.function.Function;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.pdf.d.CategoricalDistribution;
import ch.alpine.tensor.red.Mean;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MixtureDistribution.html">MixtureDistribution</a> */
public class MixtureDistribution implements Distribution, PDF, CDF, MeanInterface, //
    RandomVariateInterface, Serializable {
  /** @param weights vector with non-negative entries
   * @param distributions
   * @return */
  public static Distribution of(Tensor weights, Distribution... distributions) {
    return of(weights, List.of(distributions));
  }

  /** @param weights vector with non-negative entries
   * @param list non-empty
   * @return
   * @throws Exception if weights vectors not have the same length as list */
  public static Distribution of(Tensor weights, List<Distribution> list) {
    Integers.requireEquals(weights.length(), list.size());
    if (list.isEmpty())
      throw new IllegalArgumentException();
    return new MixtureDistribution(weights, list);
  }

  // ---
  private final CategoricalDistribution categoricalDistribution;
  private final List<Distribution> list;

  private MixtureDistribution(Tensor unscaledPDF, List<Distribution> list) {
    categoricalDistribution = CategoricalDistribution.fromUnscaledPDF(unscaledPDF);
    this.list = list;
  }

  private Scalar dot(Function<Distribution, Scalar> function) {
    return IntStream.range(0, list.size()) //
        .mapToObj(i -> function.apply(list.get(i)).multiply(categoricalDistribution.p_equals(BigInteger.valueOf(i)))) //
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
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    int index = RandomVariate.of(categoricalDistribution, randomGenerator).number().intValue();
    return RandomVariate.of(list.get(index), randomGenerator);
  }
}
