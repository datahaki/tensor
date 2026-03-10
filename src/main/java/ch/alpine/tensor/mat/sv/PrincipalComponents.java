// code by jph
package ch.alpine.tensor.mat.sv;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Times;

/** Careful: implementation is not consistent with Mathematica
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/PrincipalComponents.html">PrincipalComponents</a> */
public record PrincipalComponents(SingularValueDecomposition svd, Tensor mean) implements Serializable {
  /** @param matrix
   * @return */
  public static PrincipalComponents of(Tensor matrix) {
    Tensor mean = Mean.of(matrix);
    Tensor nmean = mean.negate();
    return new PrincipalComponents( //
        SingularValueDecomposition.of(Tensor.of(matrix.stream().map(nmean::add))).decreasing(), //
        mean);
  }

  /** @return unscaled principal components */
  public Tensor unscaled() {
    return Times.operator(svd.values()).slash(svd.getU());
  }

  /** @return principal component directions scaled by singular values */
  public Tensor scaled_directions() {
    return Times.of(svd.values(), Transpose.of(svd.getV()));
  }
}
