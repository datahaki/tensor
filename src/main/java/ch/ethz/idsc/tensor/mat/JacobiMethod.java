// code by guedelmi
// modified by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Ordering;
import ch.ethz.idsc.tensor.io.ScalarArray;
import ch.ethz.idsc.tensor.nrm.Hypot;
import ch.ethz.idsc.tensor.red.Diagonal;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;

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
 * the same unit. */
/* package */ class JacobiMethod implements Eigensystem, Serializable {
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
  private Tensor V;
  private Tensor d;

  /** @param matrix symmetric, non-empty, and real valued
   * @param chop for symmetry check
   * @throws Exception if input is not a real symmetric matrix */
  public JacobiMethod(Tensor matrix, Chop chop) {
    Scalar[][] A = ScalarArray.ofMatrix(matrix);
    n = A.length;
    int phase1 = PHASE1[Math.min(n, PHASE1.length - 1)];
    for (int ip = 0; ip < n; ++ip) {
      if (A[ip].length != n)
        throw TensorRuntimeException.of(matrix);
      for (int iq = ip + 1; iq < n; ++iq)
        chop.requireClose(A[ip][iq], A[iq][ip]);
    }
    V = IdentityMatrix.of(n);
    Tensor z = Array.zeros(n);
    Tensor b = Diagonal.of(matrix);
    d = b.copy();
    Scalar factor = DoubleScalar.of(0.2 / (n * n));
    for (int iteration = 0; iteration < MAX_ITERATIONS; ++iteration) {
      Scalar sum = A[0][0].zero();
      for (int ip = 0; ip < n - 1; ++ip)
        for (int iq = ip + 1; iq < n; ++iq)
          sum = sum.add(Abs.FUNCTION.apply(A[ip][iq]));
      if (Scalars.isZero(sum)) {
        int[] ordering = Ordering.DECREASING.of(d);
        d = Tensor.of(IntStream.of(ordering).mapToObj(d::Get));
        V = Tensor.of(IntStream.of(ordering).mapToObj(V::get));
        return;
      }
      Scalar tresh = sum.multiply(factor);
      if (phase1 <= iteration)
        tresh = tresh.zero(); // preserve unit
      for (int ip = 0; ip < n - 1; ++ip)
        for (int iq = ip + 1; iq < n; ++iq) {
          Scalar aipiq = A[ip][iq];
          Scalar Aipiq = Abs.FUNCTION.apply(aipiq);
          Scalar g = HUNDRED.multiply(Aipiq);
          if (phase1 < iteration && //
              Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(d.Get(ip)))) && //
              Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(d.Get(iq))))) {
            A[ip][iq] = aipiq.zero();
          } else //
          if (Scalars.lessThan(tresh, Aipiq)) {
            Scalar h = d.Get(iq).subtract(d.Get(ip));
            Scalar t;
            if (Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(h)))) {
              t = aipiq.divide(h);
            } else {
              Scalar theta = h.divide(aipiq.add(aipiq));
              t = Abs.FUNCTION.apply(theta).add(Hypot.of(theta, RealScalar.ONE)).reciprocal();
              if (Sign.isNegative(theta))
                t = t.negate();
            }
            Scalar c = Hypot.of(t, RealScalar.ONE).reciprocal();
            Scalar s = t.multiply(c);
            Scalar tau = s.divide(c.add(RealScalar.ONE));
            final Scalar fh = t.multiply(aipiq);
            z.set(v -> v.subtract(fh), ip);
            z.set(fh::add, iq);
            d.set(v -> v.subtract(fh), ip);
            d.set(fh::add, iq);
            A[ip][iq] = aipiq.zero();
            int fip = ip;
            int fiq = iq;
            IntStream.range(0, ip).forEach(j -> rotate(A, s, tau, j, fip, j, fiq));
            IntStream.range(ip + 1, iq).forEach(j -> rotate(A, s, tau, fip, j, j, fiq));
            IntStream.range(iq + 1, n).forEach(j -> rotate(A, s, tau, fip, j, fiq, j));
            IntStream.range(0, n).forEach(j -> rotate(V, s, tau, fip, j, fiq, j));
          }
        }
      b = b.add(z);
      z = Array.zeros(n);
      d = b.copy();
    }
    throw TensorRuntimeException.of(matrix);
  }

  @Override // from Eigensystem
  public Tensor values() {
    return d;
  }

  @Override // from Eigensystem
  public Tensor vectors() {
    return V;
  }

  private static void rotate(Scalar[][] A, Scalar s, Scalar tau, int i, int j, int k, int l) {
    Scalar g = A[i][j];
    Scalar h = A[k][l];
    A[i][j] = g.subtract(s.multiply(h.add(g.multiply(tau))));
    A[k][l] = h.add(s.multiply(g.subtract(h.multiply(tau))));
  }

  private static void rotate(Tensor A, Scalar s, Scalar tau, int i, int j, int k, int l) {
    Scalar g = A.Get(i, j);
    Scalar h = A.Get(k, l);
    A.set(g.subtract(s.multiply(h.add(g.multiply(tau)))), i, j);
    A.set(h.add(s.multiply(g.subtract(h.multiply(tau)))), k, l);
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", //
        Eigensystem.class.getSimpleName(), //
        Tensors.message(values(), vectors()));
  }
}
