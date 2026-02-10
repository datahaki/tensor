// code by jph
package ch.alpine.tensor.sca.ply;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.fft.FourierDCT;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.Cos;

/** https://en.wikipedia.org/wiki/Chebyshev_nodes */
public enum ChebyshevNodes {
  /** k/n */
  _0 {
    @Override
    public Tensor solve(Tensor rhs) {
      return LinearSolve.of(matrix(rhs.length()), rhs);
    }
  },
  /** (2k+1)/(2n)
   * 
   * @see FourierDCT#_3 */
  _1 {
    @Override
    public Tensor solve(Tensor rhs) {
      int n = rhs.length();
      Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(1, Integers.requirePositive(n)));
      // TODO !?
      return rhs.dot(FourierDCT._3.matrix(n)).multiply(scalar);
    }
  };

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

  /** @param n
   * @return
   * @see FourierDCT#_2
   * @see FourierDCT#_3 */
  public Tensor matrix(int n) {
    Tensor domain = of(n);
    return Tensors.vector(k -> domain.maps(ClenshawChebyshev.of(UnitVector.of(k + 1, k))), n);
  }

  public abstract Tensor solve(Tensor rhs);

  public Tensor coeffs(ScalarUnaryOperator function, int n) {
    return solve(of(n).maps(function));
  }
}
