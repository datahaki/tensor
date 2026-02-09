// code by jph
package ch.alpine.tensor.img;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.Rescale;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.d.CategoricalDistribution;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/HistogramTransform.html">HistogramTransform</a> */
public enum HistogramTransform {
  ;
  private static final Scalar _255 = RealScalar.of(255);

  /** @param matrix with entries in the semi-open interval [0, 256)
   * @return */
  public static Tensor of(Tensor matrix) {
    int[] values = new int[256];
    Flatten.stream(MatrixQ.require(matrix), 1) //
        .map(Scalar.class::cast) //
        .mapToInt(Scalars::intValueExact) //
        .forEach(index -> ++values[index]);
    CDF cdf = CDF.of(CategoricalDistribution.fromUnscaledPDF(Tensors.vectorInt(values)));
    return Rescale.of(matrix.maps(cdf::p_lessThan)).multiply(_255);
  }
}
