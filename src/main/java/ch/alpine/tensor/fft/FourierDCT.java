// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.Real;

/** Reference:
 * https://en.wikipedia.org/wiki/Discrete_cosine_transform
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FourierDCT.html">FourierDCT</a> */
public enum FourierDCT {
  ;
  /** function evaluates fast for input of length equal to a power of 2
   * 
   * @param vector
   * @return */
  public static Tensor of(Tensor vector) {
    int n = vector.length();
    if (Integers.isPowerOf2(n)) {
      Tensor result = raw2(vector);
      return Tolerance.CHOP.allZero(Imag.of(vector)) //
          ? Real.of(result)
          : result;
    }
    return VectorQ.require(vector).dot(FourierDCTMatrix._2.of(vector.length()));
  }

  public static Tensor of(Tensor vector, int n) {
    switch (n) {
    case 2:
      return of(vector);
    case 3:
      return inverse(vector);
    default:
    }
    throw new IllegalArgumentException(Integer.toString(n));
  }

  static Tensor inverse(Tensor vector) {
    int n = vector.length();
    if (Integers.isPowerOf2(n)) {
      Tensor result = raw3(vector);
      return Tolerance.CHOP.allZero(Imag.of(vector)) //
          ? Real.of(result)
          : result;
    }
    return VectorQ.require(vector).dot(FourierDCTMatrix._3.of(vector.length()));
  }

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
    // x = [p 0 reverse[drop[p,1]]]
    // Fourier[tensor] = [x -x]
    return Fourier.of(tensor).extract(0, n);
  }

  @PackageTestAccess
  static Tensor raw3(Tensor vector) {
    int n = vector.length();
    Scalar zero = vector.Get(0).zero();
    Tensor tensor = Join.of(vector, Array.fill(() -> zero, 1), Reverse.of(vector.extract(1, n).negate()));
    Tensor result = InverseFourier.of(Join.of(tensor, tensor.negate()));
    return Tensors.vector(i -> result.Get(i + i + 1), n);
  }
}
