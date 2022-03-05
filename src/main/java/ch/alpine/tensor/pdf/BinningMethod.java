// code by gjoel and jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.InterquartileRange;
import ch.alpine.tensor.red.ScalarSummaryStatistics;
import ch.alpine.tensor.red.StandardDeviation;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.pow.CubeRoot;
import ch.alpine.tensor.sca.pow.Sqrt;

/** BinningMethod maps samples to recommended width of bins.
 * 
 * <p>The bin size computations are inspired by Wikipedia:
 * <a href="https://en.wikipedia.org/wiki/Histogram">Histogram</a>
 * 
 * <p>The bin size computation works on samples of type {@link Quantity}. */
public enum BinningMethod implements TensorScalarFunction {
  /** Scott's normal reference rule:
   * chooses width based on {@link StandardDeviation}
   * Outliers have more influence on result than with Freedman-Diaconis.
   * The method typically yields a width larger than that determined by
   * IQR or SQRT. */
  VARIANCE {
    @Override
    public Scalar apply(Tensor tensor) {
      return RationalScalar.of(7, 2).multiply(StandardDeviation.ofVector(tensor)).divide(crt_length(tensor));
    }
  },
  /** Freedman-Diaconis' choice:
   * chooses width based on {@link InterquartileRange} */
  IQR {
    @Override
    public Scalar apply(Tensor tensor) {
      Scalar iqr = InterquartileRange.of(tensor);
      return iqr.add(iqr).divide(crt_length(tensor));
    }
  },
  /** Rice Rule */
  RICE {
    @Override
    public Scalar apply(Tensor tensor) {
      Scalar crl = crt_length(tensor);
      return width(tensor).divide(Ceiling.FUNCTION.apply(crl.add(crl)));
    }
  },
  /** Square-root choice:
   * data interval width divided by square root of the number of data points.
   * method is used by Excel histograms and many others.
   * The method typically yields a width smaller than determined by Scott's,
   * or Freedman-Diaconis formula */
  SQRT {
    @Override
    public Scalar apply(Tensor tensor) {
      return width(tensor).divide(Sqrt.FUNCTION.apply(RealScalar.of(tensor.length())));
    }
  };

  /** @param tensor
   * @return tensor.length() ^ (1/3) */
  private static Scalar crt_length(Tensor tensor) {
    return CubeRoot.FUNCTION.apply(RealScalar.of(tensor.length()));
  }

  // helper function
  private static Scalar width(Tensor tensor) {
    return tensor.stream() //
        .map(Scalar.class::cast) //
        .collect(ScalarSummaryStatistics.collector()) //
        .getClip() //
        .width();
  }
}
