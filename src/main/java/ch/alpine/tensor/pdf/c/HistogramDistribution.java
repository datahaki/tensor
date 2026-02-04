// code by jph, gjoel
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.itp.LinearBinaryAverage;
import ch.alpine.tensor.pdf.BinCounts;
import ch.alpine.tensor.pdf.BinningMethods;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.Expectation;
import ch.alpine.tensor.pdf.d.CategoricalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Floor;

/** A histogram distribution approximates an unknown continuous distribution using
 * a collection of observed samples from the distribution.
 * 
 * <p>The current implementation is characterized by the following properties
 * <ul>
 * <li>the probability density for value x in of histogram distribution is a piecewise constant function, and
 * <li>the user-specified, constant width of each bin.
 * </ul>
 * 
 * <p>The implementation combines
 * {@link CategoricalDistribution}, {@link BinCounts}, and {@link UniformDistribution}.
 * 
 * <p>Other approximation methods may be implemented in the future.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/HistogramDistribution.html">HistogramDistribution</a> */
// TODO TENSOR in theory this could also work for DateTime
public class HistogramDistribution extends AbstractContinuousDistribution implements Serializable {
  /** Example:
   * HistogramDistribution[{10.2, -1.6, 3.2, -0.4, 11.5, 7.3, 3.8, 9.8}, 2]
   * 
   * <p>The implementation also supports input of type {@link Quantity}.
   * 
   * @param samples vector
   * @param width of bins over which to assume uniform distribution, i.e. constant PDF
   * @return
   * @throws Exception if width is zero or negative */
  public static Distribution of(Tensor samples, Scalar width) {
    return new HistogramDistribution(samples, width);
  }

  /** @param samples vector
   * @param binningMethod
   * @return histogram distribution with bin width computed from given binning method
   * @see BinningMethods */
  public static Distribution of(Tensor samples, TensorScalarFunction binningMethod) {
    return of(samples, binningMethod.apply(samples));
  }

  /** @param samples
   * @return histogram distribution with bin width computed from Freedman-Diaconis rule */
  public static Distribution of(Tensor samples) {
    return of(samples, BinningMethods.IQR);
  }

  // ---
  private final ScalarUnaryOperator discrete;
  private final ScalarUnaryOperator original;
  private final CategoricalDistribution categoricalDistribution;
  private final Scalar width;
  private final Scalar width_half;
  private final Clip clip;

  /** @param samples
   * @param width does not have to be of exact precision */
  private HistogramDistribution(Tensor samples, Scalar width) {
    // establish left endpoint of domain
    Scalar min = Floor.toMultipleOf(width).apply((Scalar) samples.stream().reduce(Min::of).orElseThrow());
    discrete = scalar -> scalar.subtract(min).divide(width);
    original = scalar -> scalar.multiply(width).add(min);
    Tensor unscaledPDF = BinCounts.of(samples.map(discrete));
    categoricalDistribution = CategoricalDistribution.fromUnscaledPDF(unscaledPDF);
    this.width = width;
    width_half = width.multiply(RationalScalar.HALF);
    clip = Clips.interval(min, min.add(width.multiply(RealScalar.of(unscaledPDF.length()))));
  }

  @Override
  public Clip support() {
    return clip;
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    Scalar dix = discrete.apply(x);
    Scalar xlo = Floor.FUNCTION.apply(dix);
    return categoricalDistribution.at(xlo).divide(width); // scale probability based on interval length
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    Scalar dix = discrete.apply(x);
    Scalar xlo = Floor.FUNCTION.apply(dix);
    Scalar ofs = Clips.interval(xlo, RealScalar.ONE.add(xlo)).rescale(dix);
    return (Scalar) LinearBinaryAverage.INSTANCE.split( //
        categoricalDistribution.p_lessThan(xlo), //
        categoricalDistribution.p_lessEquals(xlo), ofs);
  }

  @Override // from AbstractContinuousDistribution
  public Scalar protected_quantile(Scalar p) {
    Scalar xlo = categoricalDistribution.quantile(p);
    return original.apply(xlo.add(Clips.interval( //
        categoricalDistribution.p_lessThan(xlo), //
        categoricalDistribution.p_lessEquals(xlo)).rescale(p)));
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return original.apply(categoricalDistribution.mean()).add(width_half);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return Expectation.variance(categoricalDistribution).add(RationalScalar.of(1, 12)) //
        .multiply(width).multiply(width);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("HistogramDistribution", clip, width);
  }
}
