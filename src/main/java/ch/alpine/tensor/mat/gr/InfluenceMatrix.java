// code by jph
package ch.alpine.tensor.mat.gr;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Int;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.mat.qr.GramSchmidt;
import ch.alpine.tensor.mat.qr.QRDecomposition;

/** {@link InfluenceMatrix#of(Tensor)} is faster than {@link Mahalanobis} when computing
 * {@link InfluenceMatrix#matrix()}
 * 
 * References:
 * "Projection Matrix" and
 * "Proofs involving the Moore-Penrose inverse"
 * on Wikipedia, 2020
 * https://en.wikipedia.org/wiki/Projection_matrix
 * https://en.wikipedia.org/wiki/Proofs_involving_the_Moore%E2%80%93Penrose_inverse
 * 
 * "Biinvariant Generalized Barycentric Coordinates on Lie Groups"
 * by Jan Hakenberg, 2020
 * 
 * @see Mahalanobis
 * @see InfluenceMatrixQ */
public interface InfluenceMatrix {
  /** @param design matrix
   * @return if the given matrix is in exact precision and has maximal rank,
   * then the implementation of influence matrix is also in exact precision */
  static InfluenceMatrix of(Tensor design) {
    int n = design.length();
    if (ExactTensorQ.of(design) || //
        !Unprotect.isUnitUnique(design))
      try {
        return new InfluenceMatrixImpl(design, PseudoInverse.usingCholesky(design));
      } catch (Exception exception) {
        // design matrix does not have maximal rank
      }
    QRDecomposition qrDecomposition = GramSchmidt.of(design);
    Tensor qInv = qrDecomposition.getQConjugateTranspose();
    return Tensors.nonEmpty(qInv) //
        ? new InfluenceMatrixImpl(qrDecomposition.getQ(), qInv)
        : new InfluenceMatrixImpl(Array.sparse(n, 1), Array.sparse(1, n));
  }

  static InfluenceMatrix of(Tensor design, Tensor d_pinv) {
    return new InfluenceMatrixImpl(design, d_pinv);
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
  default Tensor residualMaker() {
    Int i = new Int();
    // I-X^+.X is projector on ker X
    return Tensor.of(matrix().stream() //
        .map(Tensor::negate) // copy
        .peek(row -> {
          int index = i.getAndIncrement();
          row.set(scalar -> scalar.add(((Scalar) scalar).one()), index);
        }));
  }

  /** Remark: The sum of the leverages equals the rank of the design matrix.
   * 
   * @return vector with diagonal entries of influence matrix each of which are guaranteed
   * to be in the unit interval [0, 1] */
  Tensor leverages();

  /** @return sqrt of leverages identical to Mahalanobis distance guaranteed to be in the unit
   * interval [0, 1] */
  Tensor leverages_sqrt();

  /** function returns a vector vimage that satisfies
   * vimage . design == vector . design
   * 
   * @param vector
   * @return vector . design . design^+ */
  Tensor image(Tensor vector);

  /** function returns a vector vnull that satisfies
   * vnull . design == 0
   * 
   * @param vector
   * @return vector . (IdentityMatrix - design . design^+) */
  Tensor kernel(Tensor vector);
}
