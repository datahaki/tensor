// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.red.Diagonal;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** References:
 * "Biinvariant Generalized Barycentric Coordinates on Lie Groups"
 * by Jan Hakenberg, 2020
 * 
 * "Projection Matrix" and
 * "Proofs involving the Moore-Penrose inverse"
 * on Wikipedia, 2020
 * https://en.wikipedia.org/wiki/Projection_matrix
 * https://en.wikipedia.org/wiki/Proofs_involving_the_Moore%E2%80%93Penrose_inverse */
public class InfluenceMatrix implements Serializable {
  private static final long serialVersionUID = 6217090276147479246L;

  /** @param design matrix
   * @return */
  public static InfluenceMatrix of(Tensor design) {
    return new InfluenceMatrix(MatrixQ.require(design));
  }

  /***************************************************/
  private final Tensor design;
  private Tensor matrix;
  private SingularValueDecomposition svd;

  private InfluenceMatrix(Tensor design) {
    this.design = design;
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
  public Tensor matrix() {
    if (Objects.isNull(matrix))
      matrix = matrix(design);
    return matrix;
  }

  /** @return diagonal entries of influence matrix guaranteed to be in the unit interval [0, 1] */
  public Tensor leverages() {
    return Diagonal.of(matrix());
  }

  /** @return sqrt of leverages identical to Mahalanobis distance */
  public Tensor leverages_sqrt() {
    return leverages().map(Sqrt.FUNCTION);
  }

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
  public Tensor residualMaker() {
    AtomicInteger atomicInteger = new AtomicInteger();
    // I-X^+.X is projector on ker X
    return Tensor.of(matrix().stream() //
        .map(Tensor::negate) // copy
        .map(row -> {
          row.set(RealScalar.ONE::add, atomicInteger.getAndIncrement());
          return row; // by ref
        }));
  }

  private static Tensor matrix(Tensor design) {
    Tensor matrix = design.dot(PseudoInverse.of(design));
    // theory guarantees that entries of diagonal are in interval [0, 1]
    // but the numerics don't always reflect that.
    for (int index = 0; index < matrix.length(); ++index)
      matrix.set(InfluenceMatrix::requireUnit, index, index);
    return matrix;
  }

  private static Scalar requireUnit(Scalar scalar) {
    Scalar result = Clips.unit().apply(scalar);
    Tolerance.CHOP.requireClose(result, scalar);
    return result;
  }

  /***************************************************/
  /** function returns a vector vnull that satisfies
   * vnull . design == 0
   * 
   * @param vector
   * @return (IdentityMatrix - design . design^+) . vector */
  public Tensor kernel(Tensor vector) {
    return vector.subtract(image(vector));
  }

  /** function returns a vector vimage that satisfies
   * vimage . design == vector . design
   * 
   * @param vector
   * @return design . design^+ . vector */
  public Tensor image(Tensor vector) {
    if (Objects.isNull(svd))
      svd = SingularValueDecomposition.of(design);
    Tensor u = svd.getU();
    Tensor kron = Tensor.of(svd.values().stream() //
        .map(Scalar.class::cast) //
        .map(InfluenceMatrix::unitize_chop));
    // LONGTERM could still optimize further by extracting elements from rows in u
    // Tensor U = Tensor.of(u.stream().map(kron::pmul)); // extract instead of pmul!
    // return U.dot(vector.dot(U));
    return u.dot(kron.pmul(vector.dot(u)));
  }

  private static final Scalar _0 = RealScalar.of(0.0);
  private static final Scalar _1 = RealScalar.of(1.0);

  private static Scalar unitize_chop(Scalar scalar) {
    return Tolerance.CHOP.isZero(scalar) ? _0 : _1;
  }
}
