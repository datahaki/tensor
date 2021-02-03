// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;

/** References:
 * "Projection Matrix" and
 * "Proofs involving the Moore-Penrose inverse"
 * on Wikipedia, 2020
 * https://en.wikipedia.org/wiki/Projection_matrix
 * https://en.wikipedia.org/wiki/Proofs_involving_the_Moore%E2%80%93Penrose_inverse
 * 
 * "Biinvariant Generalized Barycentric Coordinates on Lie Groups"
 * by Jan Hakenberg, 2020 */
public interface InfluenceMatrix {
  /** @param design matrix
   * @return if the given matrix is in exact precision and has maximal rank,
   * then the implementation of influence matrix is also in exact precision */
  static InfluenceMatrix of(Tensor design) {
    if (ExactTensorQ.of(design))
      try {
        return new InfluenceMatrixExact(design.dot(PseudoInverse.usingCholesky(design)));
      } catch (Exception exception) {
        // design matrix does not have maximal rank
      }
    return new InfluenceMatrixSvd(design);
  }

  /** projection matrix defines a projection of a tangent vector at given point to a vector in
   * the subspace of the tangent space at given point. The subspace depends on the given sequence.
   * 
   * <p>The projection to the subspace complement is defined by the matrix Id - projection
   * 
   * <p>In the literature the projection is referred to as influence matrix, hat matrix. or
   * predicted value maker matrix.
   * 
   * @return design . design^+
   * symmetric projection matrix of size n x n with eigenvalues either 1 or 0.
   * The matrix is a point in the Grassmannian manifold Gr(n, k) where k denotes the matrix rank. */
  Tensor matrix();

  /** Remark: The trace of the influence matrix, i.e. the sum of the leverages, equals
   * the rank of the design matrix.
   * 
   * @return diagonal entries of influence matrix each of which are guaranteed to be in the
   * unit interval [0, 1] */
  Tensor leverages();

  /** @return sqrt of leverages identical to Mahalanobis distance guaranteed to be in the unit
   * interval [0, 1] */
  Tensor leverages_sqrt();

  /** projection matrix defines a projection of a tangent vector at given point to a vector in
   * the subspace of the tangent space at given point. The subspace depends on the given sequence.
   * 
   * <p>The projection to the subspace complement is defined by the matrix Id - projection
   * 
   * <p>In the literature the projection is referred to as residual maker matrix.
   * 
   * <p>Hint: function is only used during testing
   * 
   * @return IdentityMatrix - design . design^+
   * symmetric projection matrix of size n x n with eigenvalues either 1 or 0 */
  Tensor residualMaker();

  /** function returns a vector vnull that satisfies
   * vnull . design == 0
   * 
   * @param vector
   * @return (IdentityMatrix - design . design^+) . vector */
  Tensor kernel(Tensor vector);

  /** function returns a vector vimage that satisfies
   * vimage . design == vector . design
   * 
   * @param vector
   * @return design . design^+ . vector */
  Tensor image(Tensor vector);
}
