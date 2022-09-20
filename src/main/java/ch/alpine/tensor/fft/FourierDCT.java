// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Drop;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.Re;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.Cos;

/** Reference:
 * https://en.wikipedia.org/wiki/Discrete_cosine_transform
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FourierDCT.html">FourierDCT</a>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FourierDCTMatrix.html">FourierDCTMatrix</a> */
public enum FourierDCT {
  /** involutory matrix */
  _1 {
    @Override
    public Tensor of(Tensor vector) {
      int n = vector.length();
      return VectorQ.require(vector).dot(matrix(n));
    }

    @Override
    public Tensor matrix(int n) {
      if (Integers.requirePositive(n) == 1)
        return IdentityMatrix.of(1);
      Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(2, n - 1));
      Tensor matrix = Tensors.empty();
      matrix.append(ConstantArray.of(RationalScalar.HALF, n));
      Scalar factor = Pi.VALUE.divide(RealScalar.of(n - 1));
      for (int i = 1; i < n - 1; ++i) {
        int fi = i;
        matrix.append(Tensors.vector(j -> Cos.FUNCTION.apply(factor.multiply(RealScalar.of(fi * j))), n));
      }
      matrix.append(Tensors.vector(i -> i % 2 == 0 ? RationalScalar.HALF : RationalScalar.HALF.negate(), n));
      return matrix.multiply(scalar);
    }
  },
  _2 {
    @Override
    public Tensor of(Tensor vector) {
      int n = vector.length();
      if (Integers.isPowerOf2(n)) {
        Tensor result = raw2(vector);
        return Chop.NONE.allZero(Im.of(vector)) //
            ? Re.of(result)
            : result;
      }
      return VectorQ.require(vector).dot(matrix(n));
    }

    @Override
    public Tensor matrix(int n) {
      Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(1, Integers.requirePositive(n)));
      Scalar factor = Pi.VALUE.divide(RealScalar.of(n + n));
      return Tensors.matrix((i, j) -> //
      Cos.FUNCTION.apply(RealScalar.of((i + i + 1) * j).multiply(factor)).multiply(scalar), n, n);
    }
  },
  _3 {
    @Override
    public Tensor of(Tensor vector) {
      int n = vector.length();
      if (Integers.isPowerOf2(n)) {
        Tensor result = raw3(vector);
        return Chop.NONE.allZero(Im.of(vector)) //
            ? Re.of(result)
            : result;
      }
      return VectorQ.require(vector).dot(matrix(n));
    }

    @Override
    public Tensor matrix(int n) {
      Scalar s1 = Sqrt.FUNCTION.apply(RationalScalar.of(1, Integers.requirePositive(n)));
      Scalar s2 = Sqrt.FUNCTION.apply(RationalScalar.of(4, n));
      Scalar factor = Pi.VALUE.divide(RealScalar.of(n + n));
      return Tensors.matrix((i, j) -> //
      i == 0 ? s1 : Cos.FUNCTION.apply(RealScalar.of(i * (j + j + 1)).multiply(factor)).multiply(s2), n, n);
    }
  },
  /** involutory matrix */
  _4 {
    @Override
    public Tensor of(Tensor vector) {
      int n = vector.length();
      return VectorQ.require(vector).dot(matrix(n));
    }

    @Override
    public Tensor matrix(int n) {
      Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(2, Integers.requirePositive(n)));
      Scalar factor = Pi.VALUE.divide(RealScalar.of(4 * n));
      return Tensors.matrix((i, j) -> //
      Cos.FUNCTION.apply(RealScalar.of((i + i + 1) * (j + j + 1)).multiply(factor)).multiply(scalar), n, n);
    }
  };

  /** function evaluates fast for input of length equal to a power of 2
   * 
   * @param vector
   * @return */
  public abstract Tensor of(Tensor vector);

  /** @param n positive
   * @return square matrix of dimensions n x n */
  public abstract Tensor matrix(int n);

  @PackageTestAccess
  static Tensor raw2(Tensor vector) {
    int n = vector.length();
    int tail = n * 4;
    Scalar zero = vector.Get(0).zero();
    Tensor tensor = Array.fill(() -> zero, tail);
    int head = -1;
    ++tail;
    for (Tensor scalar : vector) {
      tensor.set(scalar, head += 2);
      tensor.set(scalar, tail -= 2);
    }
    // x = [p 0 -reverse[drop[p,1]]]
    // Fourier[tensor] = [x -x]
    return Fourier.of(tensor).extract(0, n);
  }

  @PackageTestAccess
  static Tensor raw3(Tensor vector) {
    int n = vector.length();
    Scalar zero = vector.Get(0).zero();
    Tensor tensor = Join.of(vector, Array.fill(() -> zero, 1), Reverse.of(Drop.head(vector, 1).negate()));
    Tensor result = InverseFourier.of(Join.of(tensor, tensor.negate()));
    return Tensors.vector(i -> result.Get(i + i + 1), n);
  }
}
