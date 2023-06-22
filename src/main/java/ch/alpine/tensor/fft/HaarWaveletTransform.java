// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.lie.KroneckerProduct;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.re.Inverse;

/** Reference:
 * "The Haar Wavelet Transform"
 * in "Matrix Computations", pp.40 */
public enum HaarWaveletTransform implements DiscreteFourierTransform {
  FORWARD {
    /** @param vector with length equals to a power of two
     * @return */
    @Override
    public Tensor transform(Tensor vector) {
      int n = vector.length();
      if (n == 1)
        return vector.copy();
      if (0 < n && n % 2 == 0) {
        int m = n / 2;
        Tensor x = transform(vector.extract(0, m));
        Tensor value = Tensors.reserve(n);
        for (int j = 0; j < m; ++j) {
          Tensor a = x.get(j); // recursion
          Tensor b = vector.get(m + j);
          value.append(a.add(b));
          value.append(a.subtract(b));
        }
        return value;
      }
      throw new Throw(vector);
    }

    private final Tensor PP = Tensors.of(Tensors.of(RealScalar.ONE), Tensors.of(RealScalar.ONE));
    private final Tensor PN = Tensors.of(Tensors.of(RealScalar.ONE), Tensors.of(RealScalar.of(-1)));

    @Override
    public Tensor matrix(int n) {
      if (n == 1)
        return Tensors.of(Tensors.of(RealScalar.ONE)); // {{1}}
      if (0 < n && n % 2 == 0) {
        int m = n / 2;
        return Join.of(1, //
            KroneckerProduct.of(matrix(m), PP), // recursion
            KroneckerProduct.of(IdentityMatrix.of(m), PN));
      }
      throw new IllegalArgumentException(Integer.toString(n));
    }
  },
  INVERSE {
    /** @param vector with length equals to a power of two
     * @return */
    @Override
    public Tensor transform(Tensor vector) {
      int n = vector.length();
      if (n == 1)
        return vector.copy();
      if (0 < n && n % 2 == 0) {
        int m = n / 2;
        Tensor xt = Tensors.reserve(m);
        Tensor xb = Tensors.reserve(m);
        int index = -1;
        for (int j = 0; j < m; ++j) {
          xt.append(vector.get(++index).multiply(RationalScalar.HALF));
          xb.append(vector.get(++index).multiply(RationalScalar.HALF));
        }
        return Join.of(transform(xt.add(xb)), xt.subtract(xb));
      }
      throw new Throw(vector);
    }

    @Override
    public Tensor matrix(int n) {
      return Inverse.of(FORWARD.matrix(n));
    }
  }
}
