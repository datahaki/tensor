// code by jph
package ch.alpine.tensor.sca.ply;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.tri.Cos;

/** https://en.wikipedia.org/wiki/Chebyshev_nodes */
public enum ChebyshevNodes {
  /** k/n */
  _0,
  /** (2k+1)/(2n) */
  _1;

  public Scalar of(int n, int k) {
    if (0 <= k && k < n)
      return Cos.FUNCTION.apply(RationalScalar.of(k + k + ordinal(), n + n).multiply(Pi.VALUE));
    throw new IllegalArgumentException();
  }

  /** @param n
   * @return vector of length n with entries as samples in the interval [-1, 1] */
  public Tensor of(int n) {
    return Tensors.vector(k -> of(n, k), n);
  }

  public Tensor matrix(int n) {
    Tensor domain = of(n);
    Tensor matrix = Tensors.vector(k -> domain.map(ClenshawChebyshev.of(UnitVector.of(k + 1, k))), n);
    return equals(_0) //
        ? matrix
        : Transpose.of(matrix);
  }
}
