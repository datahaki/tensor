// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class InfluenceMatrixSvd extends InfluenceMatrixBase implements Serializable {
  private static final long serialVersionUID = -726284537635969599L;
  private static final Scalar _0 = RealScalar.of(0.0);
  private static final Scalar _1 = RealScalar.of(1.0);
  // ---
  private final Tensor design;
  private final SingularValueDecomposition svd;
  private Tensor matrix;

  public InfluenceMatrixSvd(Tensor design) {
    this.design = design;
    svd = SingularValueDecomposition.of(design);
  }

  @Override // from InfluenceMatrix
  public synchronized Tensor matrix() {
    if (Objects.isNull(matrix))
      matrix = _matrix();
    return matrix;
  }

  private Tensor _matrix() {
    Tensor matrix = design.dot(PseudoInverse.of(svd));
    // theory guarantees that entries of diagonal are in interval [0, 1]
    // but the numerics don't always reflect that.
    for (int index = 0; index < matrix.length(); ++index)
      matrix.set(InfluenceMatrixSvd::requireUnit, index, index);
    return matrix;
  }

  /** @param scalar
   * @return clips given scalar to unit interval [0, 1]
   * @throws Exception if given scalar was outside of unit interval */
  private static Scalar requireUnit(Scalar scalar) {
    Scalar result = Clips.unit().apply(scalar);
    Tolerance.CHOP.requireClose(result, scalar);
    return result;
  }

  /***************************************************/
  @Override // from InfluenceMatrix
  public Tensor image(Tensor vector) {
    Tensor u = svd.getU();
    Tensor kron = Tensor.of(svd.values().stream() //
        .map(Scalar.class::cast) //
        .map(InfluenceMatrixSvd::unitize_chop));
    // LONGTERM could still optimize further by extracting elements from rows in u
    // Tensor U = Tensor.of(u.stream().map(kron::pmul)); // extract instead of pmul!
    // return U.dot(vector.dot(U));
    return u.dot(kron.pmul(vector.dot(u)));
  }

  private static Scalar unitize_chop(Scalar scalar) {
    return Tolerance.CHOP.isZero(scalar) ? _0 : _1;
  }

  /***************************************************/
  @Override // from InfluenceMatrixBase
  protected int length() {
    return design.length();
  }
}
