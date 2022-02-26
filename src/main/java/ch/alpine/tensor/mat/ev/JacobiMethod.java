// code by guedelmi
// modified by jph
package ch.alpine.tensor.mat.ev;

import java.util.stream.IntStream;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.mat.IdentityMatrix;
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
// TODO generalize to hermitian matrices
// https://en.wikipedia.org/wiki/Jacobi_method_for_complex_Hermitian_matrices
/* package */ class JacobiMethod implements Eigensystem {
  private static final int MAX_ITERATIONS = 50;
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
  private final int n;
  /* A stores all elements of the square matrix, but the iteration
   * reads and modifies only the elements on and above the diagonal */
  private final Scalar[][] A;
  private final Tensor V;

  public JacobiMethod(Tensor matrix, Chop chop) {
    n = matrix.length();
    A = ScalarArray.ofMatrix(matrix);
    if (A[0].length != n) // hint whether matrix is square
      throw TensorRuntimeException.of(matrix);
    for (int p = 0; p < n; ++p)
      for (int q = p + 1; q < n; ++q) // check that matrix is symmetric
        chop.requireClose(A[p][q], A[q][p]);
    V = IdentityMatrix.of(n); // init vectors
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
          Scalar apq = A[p][q];
          Scalar Apq = Abs.FUNCTION.apply(apq);
          Scalar g = HUNDRED.multiply(Apq);
          if (phase1 < iteration && //
              Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(diag(p)))) && //
              Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(diag(q))))) {
            A[p][q] = apq.zero();
            A[q][p] = apq.zero();
          } else //
          if (Scalars.lessThan(tresh, Apq))
            JacobiRotation.one(A, V, p, q, g);
        }
    }
    throw TensorRuntimeException.of(matrix);
  }

  private Scalar diag(int p) {
    return A[p][p];
  }

  private Scalar sumAbs_offDiagonal() {
    Scalar sum = A[0][0].zero(); // preserve unit
    for (int p = 0; p < n - 1; ++p)
      for (int q = p + 1; q < n; ++q)
        sum = sum.add(Abs.FUNCTION.apply(A[p][q]));
    return sum;
  }

  @Override // from Eigensystem
  public Tensor values() {
    return Tensor.of(IntStream.range(0, n).mapToObj(this::diag));
  }

  @Override // from Eigensystem
  public Tensor vectors() {
    return V;
  }

  @PackageTestAccess
  Tensor package_A() {
    return Tensors.matrix(A);
  }
}