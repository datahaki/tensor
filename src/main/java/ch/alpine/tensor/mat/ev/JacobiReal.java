// code by guedelmi
// modified by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Chop;

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
  // higher phase 1 count increases numerical precision
  private static final int[] PHASE1 = { //
      0, 0, 0, // n==0,1,2
      4, // n==3
      5, 5, // n==4,5
      6, 6, 6, 6, // n==6,...,9
      7 };
  private static final Scalar HUNDRED = DoubleScalar.of(100);
  private static final Scalar EPS = DoubleScalar.of(Math.ulp(1));
  // ---

  public JacobiReal(Tensor matrix, Chop chop) {
    super(matrix);
    if (H[0].length != n) // hint whether matrix is square
      throw TensorRuntimeException.of(matrix);
    for (int p = 0; p < n; ++p)
      for (int q = p + 1; q < n; ++q) // check that matrix is symmetric
        chop.requireClose(H[p][q], H[q][p]);
    Scalar factor = DoubleScalar.of(0.2 / (n * n));
    int phase1 = PHASE1[Math.min(n, PHASE1.length - 1)];
    for (int iteration = 0; iteration < MAX_ITERATIONS; ++iteration) {
      Scalar sum = sumAbs_offDiagonal();
      if (Scalars.isZero(sum))
        return;
      Scalar tresh = phase1 <= iteration //
          ? sum.zero()
          : sum.multiply(factor);
      for (int p = 0; p < n - 1; ++p)
        for (int q = p + 1; q < n; ++q) {
          Scalar apq = H[p][q];
          Scalar Apq = Abs.FUNCTION.apply(apq);
          Scalar g = HUNDRED.multiply(Apq);
          if (phase1 < iteration && //
              Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(diag(p)))) && //
              Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(diag(q))))) {
            H[p][q] = apq.zero();
            H[q][p] = apq.zero();
          } else //
          if (Scalars.lessThan(tresh, Apq))
            JacobiRotation.transform(H, V, p, q, g);
        }
    }
    throw TensorRuntimeException.of(matrix);
  }
}
