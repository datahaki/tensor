// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.Cos;

/** Reference:
 * https://en.wikipedia.org/wiki/Discrete_cosine_transform
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FourierDCTMatrix.html">FourierDCTMatrix</a> */
public enum FourierDCTMatrix {
  _1 {
    @Override
    public Tensor of(int n) {
      throw new UnsupportedOperationException();
    }
  },
  _2 {
    @Override
    public Tensor of(int n) {
      Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(1, Integers.requirePositive(n)));
      Scalar factor = Pi.VALUE.divide(RealScalar.of(n + n));
      return Tensors.matrix((i, j) -> //
      Cos.FUNCTION.apply(RealScalar.of((i + i + 1) * j).multiply(factor)).multiply(scalar), n, n);
    }
  },
  _3 {
    @Override
    public Tensor of(int n) {
      Scalar s1 = Sqrt.FUNCTION.apply(RationalScalar.of(1, Integers.requirePositive(n)));
      Scalar s2 = Sqrt.FUNCTION.apply(RationalScalar.of(4, Integers.requirePositive(n)));
      Scalar factor = Pi.VALUE.divide(RealScalar.of(n + n));
      return Tensors.matrix((i, j) -> //
      i == 0 ? s1 : Cos.FUNCTION.apply(RealScalar.of(i * (j + j + 1)).multiply(factor)).multiply(s2), n, n);
    }
  },
  _4 {
    @Override
    public Tensor of(int n) {
      Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(2, Integers.requirePositive(n)));
      Scalar factor = Pi.VALUE.divide(RealScalar.of(4 * n));
      return Tensors.matrix((i, j) -> //
      Cos.FUNCTION.apply(RealScalar.of((i + i + 1) * (j + j + 1)).multiply(factor)).multiply(scalar), n, n);
    }
  };

  /** @param n positive
   * @return square matrix of dimensions n x n */
  public abstract Tensor of(int n);
}
