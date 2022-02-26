// code by guedelmi
// modified by jph
package ch.alpine.tensor.mat.ev;

import java.util.stream.IntStream;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.nrm.Hypot;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;

/** The Jacobi transformations of a real symmetric matrix establishes the
 * diagonal matrix D
 * 
 * D == V* . A . V,
 * 
 * where the matrix V,
 * 
 * V == P1 * P2 * P3 * ...,
 * 
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
   * reads and modifies only the elements above the diagonal */
  private final Scalar[][] A;
  private Tensor d_next;
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
    Tensor d_prev = Diagonal.of(matrix);
    d_next = d_prev.copy();
    Scalar factor = DoubleScalar.of(0.2 / (n * n));
    int phase1 = PHASE1[Math.min(n, PHASE1.length - 1)];
    for (int iteration = 0; iteration < MAX_ITERATIONS; ++iteration) {
      Scalar sum = sumAbs_offDiagonal();
      if (Scalars.isZero(sum))
        return;
      Scalar tresh = sum.multiply(factor);
      if (phase1 <= iteration)
        tresh = tresh.zero(); // preserve unit
      Tensor d_diff = d_next.map(Scalar::zero);
      for (int p = 0; p < n - 1; ++p)
        for (int q = p + 1; q < n; ++q) {
          Scalar apq = A[p][q];
          Scalar Apq = Abs.FUNCTION.apply(apq);
          Scalar g = HUNDRED.multiply(Apq);
          if (phase1 < iteration && //
              Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(d_next.Get(p)))) && //
              Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(d_next.Get(q)))))
            A[p][q] = apq.zero();
          else //
          if (Scalars.lessThan(tresh, Apq)) {
            Scalar t = getT(p, q, g);
            process(p, q, t, d_diff); // modifies d_next
          }
        }
      d_prev = d_prev.add(d_diff);
      d_next = d_prev.copy();
    }
    throw TensorRuntimeException.of(matrix);
  }

  private Scalar sumAbs_offDiagonal() {
    Scalar sum = A[0][0].zero(); // preserve unit
    for (int ip = 0; ip < n - 1; ++ip)
      for (int iq = ip + 1; iq < n; ++iq)
        sum = sum.add(Abs.FUNCTION.apply(A[ip][iq]));
    return sum;
  }

  private Scalar getT(int p, int q, Scalar g) {
    Scalar apq = A[p][q];
    Scalar h = d_next.Get(q).subtract(d_next.Get(p));
    if (Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(h))))
      return apq.divide(h);
    Scalar theta = h.divide(apq.add(apq));
    Scalar t = Abs.FUNCTION.apply(theta).add(Hypot.withOne(theta)).reciprocal();
    return Sign.isPositiveOrZero(theta) ? t : t.negate();
  }

  /** @param p
   * @param q greater than ip
   * @param t
   * @param d_diff */
  private void process(int p, int q, Scalar t, Tensor d_diff) {
    Scalar apq = A[p][q];
    { // update diagonal
      Scalar fh = t.multiply(apq);
      d_diff.set(fh::add, q);
      d_next.set(fh::add, q);
      Scalar fn = fh.negate();
      d_diff.set(fn::add, p);
      d_next.set(fn::add, p);
    }
    // TraceTest shows that division by ci is better that multiplication with ci.reciprocal()
    Scalar ci = Hypot.withOne(t);
    Scalar s = t.divide(ci);
    { // update symmetric matrix
      A[p][q] = apq.zero();
      IntStream.range(00000, p).forEach(j -> rotateA(ci, s, j, p, j, q));
      IntStream.range(p + 1, q).forEach(j -> rotateA(ci, s, p, j, j, q));
      IntStream.range(q + 1, n).forEach(j -> rotateA(ci, s, p, j, q, j));
    }
    { // update vectors
      IntStream.range(00000, n).forEach(j -> rotateV(ci, s, p, j, q, j));
    }
  }

  private void rotateA(Scalar ci, Scalar s, int i, int j, int k, int l) {
    Scalar aij = A[i][j];
    Scalar akl = A[k][l];
    A[i][j] = aij.divide(ci).subtract(akl.multiply(s));
    A[k][l] = akl.divide(ci).add(aij.multiply(s));
  }

  private void rotateV(Scalar ci, Scalar s, int i, int j, int k, int l) {
    Scalar vij = V.Get(i, j);
    Scalar vkl = V.Get(k, l);
    V.set(vij.divide(ci).subtract(vkl.multiply(s)), i, j);
    V.set(vkl.divide(ci).add(vij.multiply(s)), k, l);
  }

  @Override // from Eigensystem
  public Tensor values() {
    return d_next;
  }

  @Override // from Eigensystem
  public Tensor vectors() {
    return V;
  }
}