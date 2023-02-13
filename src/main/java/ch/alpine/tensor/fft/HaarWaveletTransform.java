// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Integers;

/** Reference:
 * Matrix Computations */
public enum HaarWaveletTransform {
  ;
  public static Tensor of(Tensor x) {
    int n = x.length();
    if (n == 1)
      return x.copy();
    if (!Integers.isPowerOf2(n))
      throw new Throw(x); // vector length is not a power of two
    int m = n / 2;
    Tensor z = of(x.extract(0, m));
    Tensor value = Array.zeros(n);
    int i = -1;
    for (int j = 0; j < m; ++j) {
      value.set(z.Get(j).add(x.Get(m + j)), ++i);
      value.set(z.Get(j).subtract(x.Get(m + j)), ++i);
    }
    return value;
  }
}
