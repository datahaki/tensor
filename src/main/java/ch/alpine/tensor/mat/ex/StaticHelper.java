// code by jph
package ch.alpine.tensor.mat.ex;

import java.util.function.Function;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.mat.IdentityMatrix;

/** Hint: implementation makes use of eigenvalue decomposition of
 * real-valued symmetric matrices for various applications.
 * 
 * @see MatrixExp
 * @see MatrixLog
 * @see MatrixPower */
/* package */ enum StaticHelper {
  ;
  public static final Function<Integer, Tensor> IDENTITY_MATRIX = Cache.of(IdentityMatrix::of, 16);
  // ---
}
