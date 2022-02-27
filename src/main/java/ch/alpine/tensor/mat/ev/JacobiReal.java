// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.Tensor;

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
/* package */ class JacobiReal extends JacobiMethod {
  /** @param matrix symmetric */
  public JacobiReal(Tensor matrix) {
    super(matrix);
  }

  @Override // from JacobiMethod
  protected void init() {
    // ---
  }

  @Override // from JacobiMethod
  protected void run(int p, int q) {
    JacobiRotation.transform(H, V, p, q);
  }
}
