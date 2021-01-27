// code by jph
package ch.ethz.idsc.tensor.lie;

import java.util.function.Function;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.ext.Cache;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

/** Hint: implementation makes use of eigenvalue decomposition of
 * real-valued symmetric matrices for various applications.
 * 
 * @see MatrixExp
 * @see MatrixLog
 * @see MatrixPower */
/* package */ enum StaticHelper {
  ;
  public static final Function<Integer, Tensor> IDENTITY_MATRIX = Cache.of(IdentityMatrix::of, 16);
}
