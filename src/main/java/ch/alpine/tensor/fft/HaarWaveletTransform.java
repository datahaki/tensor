// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;

/** Reference:
 * Matrix Computations */
public enum HaarWaveletTransform {
  ;
  /** @param tensor with length equals to a power of two
   * @return */
  public static Tensor of(Tensor tensor) {
    int n = tensor.length();
    if (n == 1)
      return tensor.copy();
    if (0 < n && n % 2 == 0) {
      Tensor value = Tensors.reserve(n);
      int m = n / 2;
      Tensor z = of(tensor.extract(0, m));
      for (int j = 0; j < m; ++j) {
        Tensor zj = z.get(j);
        Tensor xj = tensor.get(m + j);
        value.append(zj.add(xj));
        value.append(zj.subtract(xj));
      }
      return value;
    }
    throw new Throw(tensor);
  }
}
