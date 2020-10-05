// code by jph
package ch.ethz.idsc.tensor.lie;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
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
  private static final long serialVersionUID = -6435838935271759299L;
  private final int n;

  public MatrixProduct(int n) {
    this.n = n;
  }

  @Override // from BinaryPower
  public Tensor identity() {
    return IdentityMatrix.of(n);
  }

  @Override // from BinaryPower
  public Tensor invert(Tensor matrix) {
    return Inverse.of(matrix);
  }

  @Override // from BinaryPower
  public Tensor combine(Tensor matrix1, Tensor matrix2) {
    return matrix1.dot(matrix2);
  }
}
