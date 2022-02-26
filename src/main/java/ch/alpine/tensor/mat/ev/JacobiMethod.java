// code by guedelmi
// modified by jph
package ch.alpine.tensor.mat.ev;

import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.sca.Abs;

/** The Jacobi transformations of a real symmetric matrix establishes the
 * eigenvectors and eigenvalues of given matrix.
 * 
 * Careful:
 * {@link #vectors()} gives V*
 * D == DiagonalMatrix[{@link #values()}]
 * 
 * Then the following identities hold
 * <pre>
 * A == V . D . V*
 * D == V* . A . V
 * </pre>
 * 
 * The matrix V == P1 * P2 * P3 * ...,
 * is the product of the successive Jacobi rotation matrices Pi. The diagonal
 * entries of D are the eigenvalues of A and the columns of V are the
 * eigenvectors of A.
 * 
 * Implementation also works for matrices with entries of type Quantity of
 * the same unit.
 * 
 * Quote: "You cannot find eigenvectors (or eigenvalues) in a finite number
 * of exact "arithmetic" steps for matrices of size n > 4." */
/* package */ class JacobiMethod implements Eigensystem {
  static final int MAX_ITERATIONS = 50;
  // ---
  protected final int n;
  protected final Scalar[][] H;
  protected final Tensor V;

  public JacobiMethod(Tensor matrix) {
    n = matrix.length();
    H = ScalarArray.ofMatrix(matrix);
    V = IdentityMatrix.of(n); // init vectors
  }

  protected final Scalar diag(int p) {
    return H[p][p];
  }

  protected final Scalar sumAbs_offDiagonal() {
    Scalar sum = H[0][0].zero(); // preserve unit
    for (int p = 0; p < n - 1; ++p)
      for (int q = p + 1; q < n; ++q)
        sum = sum.add(Abs.FUNCTION.apply(H[p][q]));
    return sum;
  }

  @Override // from Eigensystem
  public final Tensor values() {
    return Tensor.of(IntStream.range(0, n).mapToObj(this::diag));
  }

  @Override // from Eigensystem
  public final Tensor vectors() {
    return V;
  }

  @PackageTestAccess
  final Tensor package_H() {
    return Tensors.matrix(H);
  }
}
