// code by jph
package ch.ethz.idsc.tensor.lie;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.mat.Pivot;
import ch.ethz.idsc.tensor.num.GroupInterface;

/** Implementation is consistent with Mathematica.
 * 
 * For non-square matrix input:
 * <pre>
 * MatrixPower[{{1, 2}}, 0] => Exception
 * MatrixPower[{{1, 2}}, 1] => Exception
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MatrixPower.html">MatrixPower</a> */
/* package */ class MatrixProduct implements GroupInterface<Tensor>, Serializable {
  private static final long serialVersionUID = 8545011851929703398L;
  // ---
  private final Pivot pivot;

  /** @param pivot */
  public MatrixProduct(Pivot pivot) {
    this.pivot = pivot;
  }

  @Override // from GroupInterface
  public Tensor identity(Tensor matrix) {
    return IdentityMatrix.of(matrix);
  }

  @Override // from GroupInterface
  public Tensor invert(Tensor matrix) {
    return Inverse.of(matrix, pivot);
  }

  @Override // from GroupInterface
  public Tensor combine(Tensor matrix1, Tensor matrix2) {
    return matrix1.dot(matrix2);
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(), pivot);
  }
}
