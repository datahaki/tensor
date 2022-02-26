// code by jph
package ch.alpine.tensor.mat.ev;

import java.util.stream.IntStream;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Hypot;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Sign;

/** Jacobi Rotation modifies A and V but leaves
 * Dot.of(Transpose[V], A, V) == matrix invariant
 * 
 * ci = sqrt(1 + t^2)
 * s == t / ci
 * then
 * 1/ci ^ 2 + s ^ 2 == 1 */
/* package */ class JacobiRotation {
  private static final Scalar EPS = DoubleScalar.of(Math.ulp(1));

  /** @param A
   * @param V
   * @param p
   * @param q
   * @param g */
  public static void one(Scalar[][] A, Tensor V, int p, int q, Scalar g) {
    JacobiRotation jacobiRotation = new JacobiRotation(A, V, p, q);
    Scalar t = jacobiRotation.t(g);
    jacobiRotation.new Inner(Hypot.withOne(t), t).one();
  }

  private final Scalar[][] A;
  private final Tensor V;
  private final int p;
  private final int q;

  private JacobiRotation(Scalar[][] A, Tensor V, int p, int q) {
    this.A = A;
    this.V = V;
    this.p = p;
    this.q = q;
  }

  private Scalar t(Scalar g) {
    Scalar apq = A[p][q];
    Scalar h = A[q][q].subtract(A[p][p]);
    if (Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(h))))
      return apq.divide(h);
    Scalar theta = h.divide(apq.add(apq));
    Scalar t = Abs.FUNCTION.apply(theta).add(Hypot.withOne(theta)).reciprocal();
    return Sign.isPositiveOrZero(theta) ? t : t.negate();
  }

  private class Inner {
    final Scalar ci;
    final Scalar t;

    private Inner(Scalar ci, Scalar t) {
      this.ci = ci;
      this.t = t;
    }

    private void one() {
      Scalar apq = A[p][q];
      { // update diagonal
        Scalar h = t.multiply(apq);
        A[p][p] = A[p][p].subtract(h);
        A[q][q] = A[q][q].add(h);
      }
      int n = A.length;
      { // update symmetric matrix
        A[p][q] = apq.zero();
        A[q][p] = apq.zero();
        IntStream.range(00000, p).forEach(j -> a(j, p, j, q));
        IntStream.range(p + 1, q).forEach(j -> a(p, j, j, q));
        IntStream.range(q + 1, n).forEach(j -> a(p, j, q, j));
      }
      { // update vectors
        IntStream.range(00000, n).forEach(j -> v(p, j, q, j));
      }
    }

    private class Jr2 {
      final Scalar rij;
      final Scalar rkl;

      // TraceTest shows that division by ci is better that multiplication with ci.reciprocal()
      public Jr2(Scalar aij, Scalar akl) {
        rij = aij.subtract(akl.multiply(t)).divide(ci);
        rkl = akl.add(aij.multiply(t)).divide(ci);
      }
    }

    private void a(int i, int j, int k, int l) {
      Jr2 jr2 = new Jr2(A[i][j], A[k][l]);
      A[i][j] = jr2.rij;
      A[k][l] = jr2.rkl;
      // TODO comment out again
      // maintain symmetric for dev checks
      A[j][i] = jr2.rij;
      A[l][k] = jr2.rkl;
    }

    private void v(int i, int j, int k, int l) {
      Jr2 jr2 = new Jr2(V.Get(i, j), V.Get(k, l));
      V.set(jr2.rij, i, j);
      V.set(jr2.rkl, k, l);
    }
  }
}
