// code by jph
package ch.alpine.tensor.img;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Rescale;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.EmpiricalDistribution;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/HistogramTransform.html">HistogramTransform</a> */
public enum HistogramTransform {
  ;
  private static final Scalar _255 = RealScalar.of(255);

  /** @param tensor with entries in the interval [0, 1]
   * @return */
  public static Tensor of(Tensor tensor) {
    Dimensions dimensions = new Dimensions(tensor);
    switch (dimensions.list().size()) {
    case 2:
      return rescale(tensor).multiply(_255);
    default:
      throw TensorRuntimeException.of(tensor);
    }
  }

  private static Tensor rescale(Tensor tensor) {
    int[] values = new int[256];
    tensor.flatten(1) //
        .map(Scalar.class::cast) //
        .map(Scalar::number) //
        .forEach(number -> ++values[number.intValue()]);
    CDF cdf = CDF.of(EmpiricalDistribution.fromUnscaledPDF(Tensors.vectorInt(values)));
    return Rescale.of(tensor.map(cdf::p_lessThan));
  }
}
