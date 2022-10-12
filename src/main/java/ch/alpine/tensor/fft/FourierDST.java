// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.Sin;

/** Reference:
 * https://en.wikipedia.org/wiki/Discrete_sine_transform
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FourierDST.html">FourierDST</a>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FourierDSTMatrix.html">FourierDSTMatrix</a> */
public enum FourierDST implements DiscreteFourierTransform {
  _1 {
    @Override
    public Tensor of(Tensor vector) {
      int n = vector.length();
      if (Integers.isPowerOf2(1 + n)) {
        Tensor r = Join.of( //
            Tensors.vector(0), //
            vector, //
            Tensors.vector(0), //
            Reverse.of(vector.negate()));
        return Fourier.of(r).extract(1, 1 + n) //
            .divide(ComplexScalar.I).map(Tolerance.CHOP); // FIXME TENSOR ALG math incorrect
      }
      return VectorQ.require(vector).dot(matrix(n)); // FIXME TENSOR ALG math incorrect for complex?
    }

    @Override
    public Tensor matrix(int n) {
      Integers.requirePositive(n);
      Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(2, n + 1));
      Scalar factor = Pi.VALUE.divide(RealScalar.of(n + 1));
      return Tensors.matrix((i, j) -> //
      Sin.FUNCTION.apply(RealScalar.of((i + 1) * (j + 1)).multiply(factor)).multiply(scalar), n, n);
    }
  },
  _2 {
    @Override
    public Tensor matrix(int n) {
      Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(1, Integers.requirePositive(n)));
      Scalar factor = Pi.VALUE.divide(RealScalar.of(n + n));
      return Tensors.matrix((i, j) -> //
      Sin.FUNCTION.apply(RealScalar.of((i + i + 1) * (j + 1)).multiply(factor)).multiply(scalar), n, n);
    }
  },
  _3 {
    @Override
    public Tensor matrix(int n) {
      Scalar s1 = Sqrt.FUNCTION.apply(RationalScalar.of(1, Integers.requirePositive(n)));
      Scalar s2 = s1.add(s1);
      Scalar factor = Pi.VALUE.divide(RealScalar.of(n + n));
      Tensor matrix = Tensors.matrix((i, j) -> //
      Sin.FUNCTION.apply(RealScalar.of((i + 1) * (j + j + 1)).multiply(factor)).multiply(s2), n - 1, n);
      matrix.append(Tensors.vector(i -> i % 2 == 0 ? s1 : s1.negate(), n));
      return matrix;
    }
  },
  _4 {
    @Override
    public Tensor matrix(int n) {
      Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(2, Integers.requirePositive(n)));
      Scalar factor = Pi.VALUE.divide(RealScalar.of(4 * n));
      return Tensors.matrix((i, j) -> //
      Sin.FUNCTION.apply(RealScalar.of((i + i + 1) * (j + j + 1)).multiply(factor)).multiply(scalar), n, n);
    }
  };

  /** function evaluates fast for input of length equal to a power of 2
   * 
   * @param vector
   * @return */
  @Override
  public Tensor of(Tensor vector) {
    return matrix(vector.length()).dot(vector);
  }
}
