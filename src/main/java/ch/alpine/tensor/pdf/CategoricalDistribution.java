// code by jph
package ch.alpine.tensor.pdf;

import java.util.concurrent.atomic.AtomicInteger;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Accumulate;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.Sign;

/** Tensor-lib:CategoricalDistribution corresponds to the special case of
 * Mathematica:CategoricalDistribution with categories 0, 1, 2, 3, ...
 * This is no loss of generality, since the integer i can simply be mapped to the i-th category.
 * 
 * <p>The constructor of the tensor library CategoricalDistribution takes as input
 * an unscaled pdf vector with scalar entries that are interpreted over the samples
 * <pre>
 * 0, 1, 2, 3, ..., [length of unscaled pdf] - 1
 * </pre>
 * 
 * <p>"unscaled" pdf means that the values in the input vector are not absolute probabilities,
 * but only proportional to the probabilities P[X == i] for i = 0, 1, 2, ... of the EmpiricalDistribution.
 * 
 * <p>An instance of CategoricalDistribution supports the computation of variance via
 * {@link Expectation#variance(Distribution)}.
 * 
 * <p>Mathematica::HistogramDistribution has a <em>continuous</em> CDF.
 * In contrast, the CDF of Tensor::CategoricalDistribution has discontinuities.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/CategoricalDistribution.html">CategoricalDistribution</a> */
public class CategoricalDistribution extends EvaluatedDiscreteDistribution implements CDF {
  /** Remark:
   * An entry in the vector unscaledPDF may be an instance of {@link Quantity}.
   * This is warranted because the i-th entry represents the relative count of
   * elements of the i-th category. For instance, the count may be 5[Apples].
   * 
   * @param unscaledPDF vector of non-negative weights over the numbers
   * [0, 1, 2, ..., unscaledPDF.length() - 1]
   * @return
   * @throws Exception if any entry in given unscaledPDF is negative */
  public static CategoricalDistribution fromUnscaledPDF(Tensor unscaledPDF) {
    return new CategoricalDistribution(unscaledPDF);
  }

  // ---
  private final Tensor pdf;
  private final Tensor cdf;

  private CategoricalDistribution(Tensor unscaledPDF) {
    unscaledPDF.stream() //
        .map(Scalar.class::cast) //
        .forEach(Sign::requirePositiveOrZero);
    Tensor accumulate = Accumulate.of(unscaledPDF);
    Scalar scale = Last.of(accumulate);
    pdf = unscaledPDF.divide(scale);
    cdf = accumulate.divide(scale);
    inverse_cdf_build(cdf.length() - 1);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    AtomicInteger atomicInteger = new AtomicInteger();
    return pdf.stream() //
        .map(tensor -> tensor.multiply(RealScalar.of(atomicInteger.getAndIncrement()))) //
        .reduce(Tensor::add) //
        .map(Scalar.class::cast) //
        .orElseThrow();
  }

  @Override // from DiscreteDistribution
  public int lowerBound() {
    return 0;
  }

  @Override // from AbstractDiscreteDistribution
  protected Scalar protected_p_equals(int n) {
    return n < pdf.length() //
        ? pdf.Get(n)
        : RealScalar.ZERO;
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return cdf_get(Ceiling.intValueExact(x) - 1);
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    return cdf_get(Floor.intValueExact(x));
  }

  // helper function
  private Scalar cdf_get(int n) {
    if (0 <= n)
      return n < cdf.length() //
          ? cdf.Get(n)
          : RealScalar.ONE;
    return RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(), Tensors.message(pdf));
  }
}
