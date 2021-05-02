// code by gjoel and jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.InterquartileRange;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.red.StandardDeviation;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.CubeRoot;
import ch.alpine.tensor.sca.Sqrt;

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
      return division(tensor, Ceiling.FUNCTION.apply(crl.add(crl)));
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
      return division(tensor, Sqrt.FUNCTION.apply(RealScalar.of(tensor.length())));
    }
  };

  /** @param tensor
   * @return tensor.length() ^ (1/3) */
  private static Scalar crt_length(Tensor tensor) {
    return CubeRoot.FUNCTION.apply(RealScalar.of(tensor.length()));
  }

  private static Scalar division(Tensor tensor, Scalar k) {
    return Clips.interval( //
        (Scalar) tensor.stream().reduce(Min::of).get(), //
        (Scalar) tensor.stream().reduce(Max::of).get()).width().divide(k);
  }
}
