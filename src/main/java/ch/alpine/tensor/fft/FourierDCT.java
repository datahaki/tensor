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
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.Cos;

/** Identity in Mathematica
 * FourierDCT[vector] == vector . FourierDCTMatrix
 * 
 * implementation of FourierDCT[x, 1] is consistent with Mathematica for real and complex input
 * 
 * Reference:
 * https://en.wikipedia.org/wiki/Discrete_cosine_transform
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FourierDCT.html">FourierDCT</a>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FourierDCTMatrix.html">FourierDCTMatrix</a> */
public enum FourierDCT implements DiscreteFourierTransform {
  /** involutory matrix */
  _1 {
    @Override
    public Tensor of(Tensor vector) {
      int n = vector.length();
      int m = n - 1;
      if (Integers.isPowerOf2(m)) {
        Tensor x = vector.extract(1, m);
        Tensor r = Join.of( //
            Tensors.of(vector.Get(0)), //
            x, //
            Tensors.of(vector.Get(m)), //
            Reverse.of(x));
        // the book Matrix Computations uses a scaling factor of 1/2 instead of just 1
        return StaticHelper.re_re(vector, Fourier.of(r).extract(0, n));
      }
      return super.of(vector);
    }

    @Override
    public Tensor matrix(int n) {
      if (Integers.requirePositive(n) == 1)
        return IdentityMatrix.of(1);
      Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(2, n - 1));
      Tensor matrix = Tensors.reserve(n);
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
      return Integers.isPowerOf2(vector.length()) //
          ? StaticHelper.re_re(vector, raw2(vector))
          : super.of(vector);
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
      return Integers.isPowerOf2(vector.length()) //
          ? StaticHelper.re_re(vector, raw3(vector))
          : super.of(vector);
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
      // TODO TENSOR
      return super.of(vector);
    }

    @Override
    public Tensor matrix(int n) {
      Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(2, Integers.requirePositive(n)));
      Scalar factor = Pi.VALUE.divide(RealScalar.of(4 * n));
      return Tensors.matrix((i, j) -> //
      Cos.FUNCTION.apply(RealScalar.of((i + i + 1) * (j + j + 1)).multiply(factor)).multiply(scalar), n, n);
    }
  };

  @Override
  public Tensor of(Tensor vector) {
    /* MATHEMATICA CONVENTION
     * FourierDCT[vector] == vector . FourierDCTMatrix */
    return VectorQ.require(vector).dot(matrix(vector.length()));
  }

  @PackageTestAccess
  static Tensor raw2(Tensor vector) {
    int n = vector.length();
    int tail = n * 4;
    Tensor tensor = Array.same(vector.Get(0).zero(), tail);
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
    Tensor tensor = Join.of(vector, Array.same(vector.Get(0).zero(), 1), Reverse.of(Drop.head(vector, 1).negate()));
    Tensor result = InverseFourier.of(Join.of(tensor, tensor.negate()));
    return Tensors.vector(i -> result.Get(i + i + 1), n);
  }
}
