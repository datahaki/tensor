// code by N. M. Brenner
// adapted by jph
package ch.ethz.idsc.tensor.fft;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ScalarArray;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** Discrete Fourier transform of a vector.
 * Functionality works also for vectors with entries of type {@link Quantity}.
 *
 * <p>In the tensor library, the Fourier transform is restricted to vectors with
 * length of power of 2.
 * 
 * <p>Consistent with Mathematica:
 * Mathematica::Fourier[{}] throws an Exception
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Fourier.html">Fourier</a> */
public enum Fourier {
  ;
  /** @param vector of length of power of 2
   * @return discrete Fourier transform of given vector */
  public static Tensor of(Tensor vector) {
    return of(vector, 1);
  }

  /** Hint: uses decimation-in-time or Cooley-Tukey FFT
   * 
   * @param vector of length of power of 2
   * @param b is +1 for forward, and -1 for inverse transform
   * @return discrete Fourier transform of given vector */
  public static Tensor of(Tensor vector, int b) {
    final int n = vector.length();
    if (n == 0 || 0 != (n & (n - 1)))
      throw TensorRuntimeException.of(vector); // vector length is not a power of two
    Scalar[] array = ScalarArray.ofVector(vector);
    {
      int j = 0;
      for (int i = 0; i < n; ++i) {
        if (j > i) {
          Scalar val = array[i];
          array[i] = array[j];
          array[j] = val;
        }
        int m = n >> 1;
        while (m > 0 && j >= m) {
          j -= m;
          m >>= 1;
        }
        j += m;
      }
    }
    int mmax = 1;
    while (n > mmax) {
      int istep = mmax << 1;
      double thalf = b * Math.PI / istep;
      double wtemp = Math.sin(thalf);
      Scalar wp = ComplexScalar.of(-2 * wtemp * wtemp, Math.sin(thalf + thalf));
      Scalar w = RealScalar.ONE;
      for (int m = 0; m < mmax; ++m) {
        for (int i = m; i < n; i += istep) {
          int j = i + mmax;
          Scalar temp = array[j].multiply(w);
          array[j] = array[i].subtract(temp);
          array[i] = array[i].add(temp);
        }
        w = w.add(w.multiply(wp));
      }
      mmax = istep;
    }
    return Tensors.of(array).divide(Sqrt.FUNCTION.apply(RealScalar.of(n)));
  }
}
