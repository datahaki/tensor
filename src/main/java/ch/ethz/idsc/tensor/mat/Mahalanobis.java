// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.lie.Symmetrize;
import ch.ethz.idsc.tensor.nrm.VectorNormInterface;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** Mahalanobis is an alternative to {@link InfluenceMatrix} for the computation of
 * leverages. Mahalanobis derives the leverages from the bilinear form determined by
 * the design matrix.
 * 
 * <p>While influence matrix computes design^+, Mahalanobis builds (design^T . design)^+
 * which are connected via the Moore Penrose {@link PseudoInverse} relation
 * <pre>
 * design^+ == (design^T . design)^+ . design^T
 * </pre>
 * 
 * <p>The reference suggests to use the inverse and the biinvariant mean m as reference point.
 * For our more general purposes, we employ the pseudo-inverse of the form evaluated at an
 * arbitrary point.
 * 
 * <p>Reference:
 * "Exponential Barycenters of the Canonical Cartan Connection and Invariant Means on Lie Groups"
 * by Xavier Pennec, Vincent Arsigny, 2012, p. 39
 * 
 * @see InfluenceMatrix */
public final class Mahalanobis implements InfluenceMatrix, VectorNormInterface, Serializable {
  private static final long serialVersionUID = -5381451862439751058L;
  // ---
  private final Tensor design;
  private final Tensor design_t;
  private final Tensor sigma;
  private final Tensor sigma_inverse;

  /** @param design matrix with n rows as log_x(p_i) */
  public Mahalanobis(Tensor design) {
    this.design = design;
    design_t = ConjugateTranspose.of(design);
    Scalar factor = RealScalar.of(design.length());
    sigma = design_t.dot(design).divide(factor);
    // computation of pseudo inverse only may result in numerical deviation from true symmetric result
    sigma_inverse = Symmetrize.of(PseudoInverse.of(sigma).divide(factor));
  }

  /** @return design^H . design / Length[design] */
  public Tensor sigma_n() {
    return sigma;
  }

  /** @return (design^H . design)^+ that is symmetric positive definite if sequence contains
   * sufficient points and parameterization of tangent space is tight, for example SE(2).
   * Otherwise symmetric positive semidefinite matrix, for example S^d as embedded in R^(d+1).
   * @see PositiveDefiniteMatrixQ
   * @see PositiveSemidefiniteMatrixQ */
  public Tensor sigma_inverse() {
    return sigma_inverse;
  }

  /***************************************************/
  private Tensor matrix;

  @Override // from InfluenceMatrix
  public synchronized Tensor matrix() {
    return Objects.isNull(matrix) //
        ? matrix = design.dot(sigma_inverse).dot(design_t)
        : matrix;
  }

  @Override // from InfluenceMatrix
  public Tensor residualMaker() {
    return StaticHelper.residualMaker(matrix());
  }

  @Override // from InfluenceMatrix
  public Tensor leverages() {
    return Tensor.of(design.stream().map(this::norm_squared));
  }

  @Override // from InfluenceMatrix
  public Tensor leverages_sqrt() {
    return Tensor.of(design.stream().map(this::ofVector));
  }

  @Override // from InfluenceMatrix
  public Tensor image(Tensor vector) {
    return Objects.isNull(matrix) //
        ? vector.dot(design).dot(sigma_inverse).dot(design_t)
        : vector.dot(matrix);
  }

  @Override // from InfluenceMatrix
  public Tensor kernel(Tensor vector) {
    return vector.subtract(image(vector));
  }

  /***************************************************/
  /** @param vector
   * @return sigma_inverse . vector . vector */
  public Scalar norm_squared(Tensor vector) {
    // theory guarantees that leverage is in interval [0, 1]
    // so far the numerics did not result in values below 0 here
    return (Scalar) sigma_inverse.dot(vector).dot(vector);
  }

  /** @param vector
   * @return sqrt of sigma_inverse . vector . vector */
  @Override // from VectorNormInterface
  public Scalar ofVector(Tensor vector) {
    return Sqrt.FUNCTION.apply(norm_squared(vector));
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(), Dimensions.of(design));
  }
}
