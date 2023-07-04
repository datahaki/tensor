// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.exp.Exp;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/GaussianMatrix.html">GaussianMatrix</a> */
public enum GaussianMatrix {
  ;
  /** only approximately consistent with Mathematica
   * 
   * @param r positive
   * @return m x m matrix where m == 2 * r + 1
   * @throws Exception if r is zero or negative */
  public static Tensor of(int r) {
    Scalar sigmas = AbsSquared.FUNCTION.apply(RationalScalar.of(r, 2));
    Scalar factor = sigmas.add(sigmas).negate();
    int m = 2 * r + 1;
    Tensor offset = Tensors.vector(-r, -r);
    Tensor matrix = Array.of(list -> Vector2NormSquared.of(Tensors.vector(list).add(offset)), m, m) //
        .divide(factor).map(Exp.FUNCTION);
    return matrix.divide((Scalar) Flatten.stream(matrix, 1).reduce(Tensor::add).orElseThrow());
  }
}
