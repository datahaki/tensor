// code by jph
package ch.alpine.tensor.mat.ev;

import java.util.stream.IntStream;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.IdentityMatrix;
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
  public static void transform(Scalar[][] A, Tensor V, int p, int q, Scalar g) {
    JacobiRotation jacobiRotation = new JacobiRotation(A, V, p, q);
    jacobiRotation.new Inner(jacobiRotation.t(g)).transform();
  }

  private final Scalar[][] A;
  private final Tensor V;
  private final int p;
  private final int q;

  JacobiRotation(Scalar[][] A, Tensor V, int p, int q) {
    this.A = A;
    this.V = V;
    this.p = p;
    this.q = q;
  }

  Scalar t(Scalar g) {
    Scalar apq = A[p][q];
    Scalar h = A[q][q].subtract(A[p][p]);
    if (Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(h))))
      return apq.divide(h);
    Scalar theta = h.divide(apq.add(apq));
    Scalar t = Abs.FUNCTION.apply(theta).add(Hypot.withOne(theta)).reciprocal();
    return Sign.isPositiveOrZero(theta) ? t : t.negate();
  }

  class Inner {
    private final Scalar ci;
    private final Scalar t;

    Inner(Scalar t) {
      this.ci = Hypot.withOne(t);
      this.t = t;
    }

    void transform() {
      int n = A.length;
      { // preserve original values
        Scalar h = t.multiply(A[p][q]);
        Scalar app = A[p][p].subtract(h);
        Scalar aqq = A[q][q].add(h);
        // basis transform of symmetric matrix
        IntStream.range(0, n).forEach(j -> a(p, j, q, j));
        IntStream.range(0, n).forEach(j -> a(j, p, j, q));
        A[p][p] = app;
        A[q][q] = aqq;
      }
      { // multiplication from left
        Tensor vp = V.get(p);
        Tensor vq = V.get(q);
        V.set(vp.subtract(vq.multiply(t)).divide(ci), p);
        V.set(vq.add(vp.multiply(t)).divide(ci), q);
      }
    }

    private class Transform {
      final Scalar rij;
      final Scalar rkl;

      // TraceTest shows that division by ci is better that multiplication with ci.reciprocal()
      public Transform(Scalar aij, Scalar akl) {
        rij = aij.subtract(akl.multiply(t)).divide(ci);
        rkl = akl.add(aij.multiply(t)).divide(ci);
      }
    }

    private void a(int i, int j, int k, int l) {
      Transform jr2 = new Transform(A[i][j], A[k][l]);
      A[i][j] = jr2.rij;
      A[k][l] = jr2.rkl;
    }

    // for testing
    Tensor rotation() {
      Tensor tensor = IdentityMatrix.of(A.length);
      {
        Scalar c = ci.reciprocal();
        tensor.set(c, p, p);
        tensor.set(c, q, q);
      }
      {
        Scalar s = t.divide(ci);
        tensor.set(s, p, q);
        tensor.set(s.negate(), q, p);
      }
      return tensor;
    }
  }
}
