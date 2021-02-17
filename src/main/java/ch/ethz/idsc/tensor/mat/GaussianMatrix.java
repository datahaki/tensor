// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.nrm.Vector2NormSquared;
import ch.ethz.idsc.tensor.sca.AbsSquared;
import ch.ethz.idsc.tensor.sca.Exp;

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
    return matrix.divide((Scalar) matrix.flatten(2).reduce(Tensor::add).get());
  }
}
