// code by jph
package ch.ethz.idsc.tensor.lie;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
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
  private static final long serialVersionUID = 1942630575114283261L;
  // ---
  private final int n;
  private final Pivot pivot;

  public MatrixProduct(int n, Pivot pivot) {
    this.n = n;
    this.pivot = pivot;
  }

  @Override // from GroupInterface
  public Tensor identity(Tensor matrix) {
    return DiagonalMatrix.of(n, matrix.Get(0, 0).one());
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
    return String.format("%s[n=%d, %s]", getClass().getSimpleName(), n, pivot);
  }
}
