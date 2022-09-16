// code by jph
package ch.alpine.tensor.sca.ply;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.sca.pow.Sqrt;

/** implementation permits coefficients of type {@link GaussScalar} */
/* package */ enum RootsDegree2 {
  ;
  /** @param coeffs {a, b, c} representing a + b*x + c*x^2 == 0
   * @return vector of length 2 with the roots as entries
   * if the two roots are real, then the smaller root is the first entry */
  public static Tensor of(Tensor coeffs) {
    Scalar c = coeffs.Get(2);
    Scalar p = coeffs.Get(1).divide(c.add(c)).negate();
    Scalar p2 = p.multiply(p);
    Scalar q = coeffs.Get(0).divide(c);
    Scalar d = Sqrt.FUNCTION.apply(p2.subtract(q));
    return Tensors.of(p.subtract(d), p.add(d));
  }
}
