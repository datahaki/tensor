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
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.ext.RomanNumeral;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.red.EqualsReduce;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.Cos;

/** Identity in Mathematica
 * FourierDCT[vector] == vector . FourierDCTMatrix
 * 
 * implementation of FourierDCT[x, 1] is consistent with Mathematica for real and complex input
 * 
 * Quote from https://en.wikipedia.org/wiki/Discrete_cosine_transform
 * "A DST-III or DST-IV can be computed from a DCT-III or DCT-IV (see discrete cosine transform),
 * respectively, by reversing the order of the inputs and flipping the sign of every other output,
 * and vice versa for DST-II from DCT-II."
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
    public Tensor transform(Tensor vector) {
      int n = vector.length();
      int m = n - 1;
      return Integers.isPowerOf2(m) //
          // the book Matrix Computations uses a scaling factor of 1/2 instead of just 1
          ? StaticHelper.re_re(vector, Fourier.FORWARD.transform(Join.of(vector, Reverse.of(vector.extract(1, m)))).extract(0, n))
          : super.transform(vector);
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
      Scalar neg_half = RationalScalar.HALF.negate();
      matrix.append(Tensors.vector(i -> i % 2 == 0 ? RationalScalar.HALF : neg_half, n));
      return Transpose.of(matrix.multiply(scalar));
    }

    @Override
    public DiscreteFourierTransform inverse() {
      return this;
    }
  },
  _2 {
    @Override
    public Tensor transform(Tensor vector) {
      return Integers.isPowerOf2(vector.length()) //
          ? StaticHelper.re_re(vector, raw2(vector))
          : super.transform(vector);
    }

    @Override
    public Tensor matrix(int n) {
      Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(1, Integers.requirePositive(n)));
      Scalar factor = Pi.VALUE.divide(RealScalar.of(n + n));
      return Tensors.matrix((i, j) -> //
      Cos.FUNCTION.apply(RealScalar.of((j + j + 1) * i).multiply(factor)).multiply(scalar), n, n);
    }

    @Override
    public DiscreteFourierTransform inverse() {
      return _3;
    }
  },
  _3 {
    @Override
    public Tensor transform(Tensor vector) {
      return Integers.isPowerOf2(vector.length()) //
          ? StaticHelper.re_re(vector, raw3(vector))
          : super.transform(vector);
    }

    @Override
    public Tensor matrix(int n) {
      Scalar s1 = Sqrt.FUNCTION.apply(RationalScalar.of(1, Integers.requirePositive(n)));
      Scalar s2 = Sqrt.FUNCTION.apply(RationalScalar.of(4, n));
      Scalar factor = Pi.VALUE.divide(RealScalar.of(n + n));
      return Tensors.matrix((i, j) -> //
      j == 0 ? s1 : Cos.FUNCTION.apply(RealScalar.of(j * (i + i + 1)).multiply(factor)).multiply(s2), n, n);
    }

    @Override
    public DiscreteFourierTransform inverse() {
      return _2;
    }
  },
  /** involutory matrix */
  _4 {
    @Override
    public Tensor matrix(int n) {
      Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(2, Integers.requirePositive(n)));
      Scalar factor = Pi.VALUE.divide(RealScalar.of(4 * n));
      return Tensors.matrix((i, j) -> //
      Cos.FUNCTION.apply(RealScalar.of((i + i + 1) * (j + j + 1)).multiply(factor)).multiply(scalar), n, n);
    }

    @Override
    public DiscreteFourierTransform inverse() {
      return this;
    }
  };

  @PackageTestAccess
  static Tensor raw2(Tensor vector) {
    int n = vector.length();
    int tail = n * 4;
    Tensor tensor = Array.same(EqualsReduce.zero(vector), tail);
    int head = -1;
    ++tail;
    for (Tensor scalar : vector) {
      tensor.set(scalar, head += 2);
      tensor.set(scalar, tail -= 2);
    }
    // x = [p 0 -reverse[drop[p,1]]]
    // Fourier[tensor] = [x -x]
    return Fourier.FORWARD.transform(tensor).extract(0, n);
  }

  @PackageTestAccess
  static Tensor raw3(Tensor vector) {
    int n = vector.length();
    Tensor tensor = Join.of(vector, Array.same(EqualsReduce.zero(vector), 1), Reverse.of(Drop.head(vector, 1).negate()));
    Tensor result = Fourier.INVERSE.transform(Join.of(tensor, tensor.negate()));
    return Tensors.vector(i -> result.Get(i + i + 1), n);
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("FourierDCT", RomanNumeral.of(ordinal() + 1));
  }
}
