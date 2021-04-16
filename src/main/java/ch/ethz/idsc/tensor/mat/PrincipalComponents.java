// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.sv.SingularValueDecomposition;
import ch.ethz.idsc.tensor.red.Mean;

/** Careful: implementation is not consistent with Mathematica
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/PrincipalComponents.html">PrincipalComponents</a> */
public enum PrincipalComponents {
  ;
  /** @param matrix
   * @return */
  public static Tensor of(Tensor matrix) {
    Tensor nmean = Mean.of(matrix).negate();
    return of(SingularValueDecomposition.of(Tensor.of(matrix.stream().map(nmean::add))));
  }

  /** @param svd
   * @return */
  public static Tensor of(SingularValueDecomposition svd) {
    return Tensor.of(svd.getU().stream().map(svd.values()::pmul));
  }
}
