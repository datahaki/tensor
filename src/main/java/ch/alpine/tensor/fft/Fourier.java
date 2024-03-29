// code by N. M. Brenner
// adapted by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.VandermondeMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.pow.Sqrt;

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
 * <a href="https://reference.wolfram.com/language/ref/Fourier.html">Fourier</a>
 * <a href="https://reference.wolfram.com/language/ref/InverseFourier.html">InverseFourier</a>
 * <a href="https://reference.wolfram.com/language/ref/FourierMatrix.html">FourierMatrix</a>
 * 
 * @see VandermondeMatrix */
public enum Fourier implements DiscreteFourierTransform {
  FORWARD {
    /** @param vector of length of power of 2
     * @return discrete Fourier transform of given vector */
    @Override
    public Tensor transform(Tensor vector) {
      return fft(vector, 1);
    }

    /** @param n positive
     * @return square matrix of dimensions [n x n] with complex entries
     * <code>(i, j) -> sqrt(1/n) exp(i * j * 2pi/n *I)</code> */
    @Override
    public Tensor matrix(int n) {
      Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(1, Integers.requirePositive(n)));
      return Tensors.matrix((i, j) -> //
      ComplexScalar.unit(RationalScalar.of(i * j, n).multiply(Pi.TWO)).multiply(scalar), n, n);
    }
  },
  INVERSE {
    @Override
    public Tensor transform(Tensor vector) {
      return fft(vector, -1);
    }

    @Override
    public Tensor matrix(int n) {
      return ConjugateTranspose.of(FORWARD.matrix(n));
    }
  };

  /** Hint: uses decimation-in-time or Cooley-Tukey FFT
   * 
   * @param vector of length of power of 2
   * @param b is +1 for forward, and -1 for inverse transform
   * @return discrete Fourier transform of given vector */
  private static Tensor fft(Tensor vector, int b) {
    int n = Integers.requirePowerOf2(vector.length());
    Scalar[] array = ScalarArray.ofVector(vector);
    for (int j = 0, i = 0; i < n; ++i) {
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
    for (int mmax = 1; mmax < n; mmax <<= 1) {
      int istep = mmax << 1;
      double thalf = b * Math.PI / istep;
      double wtemp = Math.sin(thalf);
      Scalar wp = ComplexScalar.of(1.0 - 2.0 * wtemp * wtemp, Math.sin(thalf + thalf));
      Scalar w = wp.one();
      for (int m = 0; m < mmax; ++m) {
        for (int i = m; i < n; i += istep) {
          int j = i + mmax;
          Scalar temp = array[j].multiply(w);
          array[j] = array[i].subtract(temp);
          array[i] = array[i].add(temp);
        }
        w = w.multiply(wp);
      }
    }
    return Tensors.of(array).divide(Sqrt.FUNCTION.apply(RealScalar.of(n)));
  }
}
