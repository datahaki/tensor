// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class InfluenceMatrixSvd extends InfluenceMatrixBase implements Serializable {
  private static final long serialVersionUID = -8832681227786208221L;
  // ---
  private final Tensor design;
  private final SingularValueDecomposition svd;
  /* matrix is computed on demand */
  private Tensor matrix;

  public InfluenceMatrixSvd(Tensor design) {
    this.design = design;
    svd = SingularValueDecomposition.of(design);
  }

  @Override // from InfluenceMatrix
  public synchronized Tensor matrix() {
    return Objects.isNull(matrix) //
        ? matrix = _matrix()
        : matrix;
  }

  private Tensor _matrix() {
    Tensor matrix = design.dot(PseudoInverse.of(svd));
    // theory guarantees that entries of diagonal are in interval [0, 1]
    // but the numerics don't always reflect that.
    for (int index = 0; index < matrix.length(); ++index)
      matrix.set(StaticHelper::requireUnit, index, index);
    return matrix;
  }

  @Override // from InfluenceMatrix
  public Tensor image(Tensor vector) {
    Tensor u = svd.getU();
    Tensor kron = Tensor.of(svd.values().stream() //
        .map(Scalar.class::cast) //
        .map(StaticHelper::unitize_chop));
    // LONGTERM could still optimize further by extracting elements from rows in u
    // Tensor U = Tensor.of(u.stream().map(kron::pmul)); // extract instead of pmul!
    // return U.dot(vector.dot(U));
    return u.dot(kron.pmul(vector.dot(u)));
  }

  @Override // from InfluenceMatrixBase
  protected int length() {
    return design.length();
  }
}
