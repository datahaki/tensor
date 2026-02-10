// code by jph
package ch.alpine.tensor.sca.ply;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.sca.pow.Sqrt;

/* package */ enum RootsExplicit implements TensorUnaryOperator {
  /** implementation permits coefficients of type {@link GaussScalar}
   * 
   * @param coeffs {a, b} representing a + b * x == 0
   * @return vector of length 1 */
  DEGREE_1 {
    @Override
    public Tensor apply(Tensor coeffs) {
      // return Tensors.of(coeffs.Get(0).divide(coeffs.Get(1)).negate());
      return Tensors.of(coeffs.Get(1).under(coeffs.Get(0)).negate());
    }
  },
  /** implementation permits coefficients of type {@link GaussScalar}
   * 
   * Reference:
   * "Matrix Computations" p.97
   * 
   * @param coeffs {a, b, c} representing a + b*x + c*x^2 == 0
   * @return vector of length 2 with the roots as entries */
  DEGREE_2 {
    @Override
    public Tensor apply(Tensor coeffs) {
      Scalar c = coeffs.Get(2);
      Scalar p = coeffs.Get(1).divide(c.add(c)).negate();
      Scalar q = coeffs.Get(0).divide(c).negate();
      Scalar d = Sqrt.FUNCTION.apply(p.multiply(p).add(q));
      Scalar r = d.add(p);
      return Tensors.of(q.divide(r).negate(), r);
    }
  },
  /** finds the roots of the polynomial
   * <pre>
   * d + c*x + b*x^2 + a*x^3 == 0
   * </pre>
   * 
   * @param coeffs vector of the form {d, c, b, a} with last entry non-zero
   * @return vector of length 3 containing the roots of the polynomial */
  DEGREE_3 {
    @Override
    public Tensor apply(Tensor coeffs) {
      return new RootsDegree3(coeffs).roots();
    }
  };
}
