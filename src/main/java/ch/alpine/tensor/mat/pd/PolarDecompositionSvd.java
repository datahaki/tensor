// code by jph
package ch.alpine.tensor.mat.pd;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.lie.TensorProduct;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Sign;

/** Reference:
 * "Linear Algebra Learning from Data"
 * by G. Strang, 2019 */
/* package */ class PolarDecompositionSvd extends PolarDecompositionBase implements Serializable {
  /** @param matrix
   * @return */
  public static PolarDecompositionSvd up(Tensor matrix) {
    SingularValueDecomposition svd = SingularValueDecomposition.of(matrix);
    return new PolarDecompositionSvd(svd, svd.getV());
  }

  /** @param matrix
   * @return */
  public static PolarDecompositionSvd pu(Tensor matrix) {
    SingularValueDecomposition svd = SingularValueDecomposition.of(matrix);
    return new PolarDecompositionSvd(svd, svd.getU());
  }

  // ---
  private final SingularValueDecomposition svd;
  private final Tensor basis;

  private PolarDecompositionSvd(SingularValueDecomposition svd, Tensor basis) {
    this.svd = svd;
    this.basis = basis;
  }

  @Override // from PolarDecomposition
  public Tensor getUnitary() {
    return MatrixDotTranspose.of(svd.getU(), svd.getV());
  }

  @Override // from PolarDecomposition
  public Tensor getPositiveSemidefinite() {
    return MatrixDotTranspose.of(Tensor.of(basis.stream().map(Times.operator(svd.values()))), basis);
  }

  /** @return */
  public Tensor getConjugateTransposeUnitary() {
    return MatrixDotTranspose.of(svd.getV(), svd.getU());
  }

  /** @return
   * @throws Exception if input matrix is not square */
  public Tensor getUnitaryWithDetOne() {
    Tensor tensor = getUnitary();
    if (Sign.isPositiveOrZero(Det.of(tensor)))
      return tensor;
    int n = svd.getU().length();
    Tensor ve = svd.getV().get(Tensor.ALL, n - 1).negate();
    return tensor.add(TensorProduct.of(svd.getU().get(Tensor.ALL, n - 1), ve.add(ve)));
  }

  /** EXPERIMENTAL TENSOR
   * 
   * for input of square matrix, the function returns a matrix with determinant +1
   * 
   * @return matrix of size n x m of which the transpose satisfies {@link OrthogonalMatrixQ} */
  @PackageTestAccess
  Tensor getUnitaryWithDetOne2() {
    Tensor tensor = getUnitary();
    if (Sign.isPositiveOrZero(Det.of(tensor)))
      return tensor;
    int n = tensor.length();
    Tensor ue = svd.getU().get(Tensor.ALL, n - 1).negate();
    return tensor.add(TensorProduct.of(ue.add(ue), svd.getV().get(Tensor.ALL, n - 1)));
  }
}
