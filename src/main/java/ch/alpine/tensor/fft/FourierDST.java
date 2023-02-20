// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.Sin;

/** Identity in Mathematica
 * FourierDST[vector] == vector . FourierDSTMatrix
 * 
 * implementation of FourierDCT[x, 1] is consistent with Mathematica for real and complex input
 * 
 * Reference:
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
      int m = n + 1;
      if (Integers.isPowerOf2(m)) {
        Tensor zero = Array.same(vector.Get(0).zero(), 1);
        // the book Matrix Computations uses a scaling factor of I/2 instead of just I
        return Fourier.FORWARD.of(Join.of( //
            zero, //
            vector, //
            zero, //
            Reverse.of(vector.negate()))).extract(1, m).divide(ComplexScalar.I).map(Tolerance.CHOP);
      }
      return VectorQ.require(vector).dot(matrix(vector.length()));
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
    public Tensor of(Tensor vector) {
      Tensor result = vector.copy();
      for (int i = 1; i < result.length(); i += 2)
        result.set(Scalar::negate, i);
      return Reverse.of(FourierDCT._2.of(result));
    }

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
    public Tensor of(Tensor vector) {
      Tensor result = FourierDCT._3.of(Reverse.of(vector));
      for (int i = 1; i < result.length(); i += 2)
        result.set(Scalar::negate, i);
      return result;
    }

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
    public Tensor of(Tensor vector) {
      Tensor result = FourierDCT._4.of(Reverse.of(vector));
      for (int i = 1; i < result.length(); i += 2)
        result.set(Scalar::negate, i);
      return result;
    }

    @Override
    public Tensor matrix(int n) {
      Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(2, Integers.requirePositive(n)));
      Scalar factor = Pi.VALUE.divide(RealScalar.of(4 * n));
      return Tensors.matrix((i, j) -> //
      Sin.FUNCTION.apply(RealScalar.of((i + i + 1) * (j + j + 1)).multiply(factor)).multiply(scalar), n, n);
    }
  };
}
