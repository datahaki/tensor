// code by jph
package ch.alpine.tensor.mat;

import java.util.Collections;
import java.util.List;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.tri.Cos;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/GaborMatrix.html">GaborMatrix</a> */
public enum GaborMatrix {
  ;
  /** only approximately consistent with Mathematica
   * 
   * @param r
   * @param k vector
   * @param phi
   * @return */
  public static Tensor of(int r, Tensor k, Scalar phi) {
    Scalar sigmas = AbsSquared.FUNCTION.apply(RationalScalar.of(r, 2));
    Scalar factor = sigmas.add(sigmas).negate();
    int m = 2 * r + 1;
    Scalar center = RealScalar.of(-r);
    Tensor offset = k.map(_ -> center);
    List<Integer> dimensions = Collections.nCopies(k.length(), m);
    Tensor matrix = Array.of(list -> Vector2NormSquared.of(Tensors.vector(list).add(offset)), dimensions) //
        .divide(factor).map(Exp.FUNCTION);
    Tensor weight = Array.of(list -> k.dot(Tensors.vector(list).add(offset)).subtract(phi), dimensions) //
        .map(Cos.FUNCTION);
    return Times.of(weight, matrix);
  }
}
