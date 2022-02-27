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

  /** @param H
   * @param V
   * @param p
   * @param q
   * @param g */
  public static void transform(Scalar[][] H, Tensor[] V, int p, int q, Scalar g) {
    JacobiRotation jacobiRotation = new JacobiRotation(H, V, p, q);
    jacobiRotation.new Inner(jacobiRotation.t(g)).transform();
  }

  private final Scalar[][] H;
  private final Tensor[] V;
  private final int p;
  private final int q;

  JacobiRotation(Scalar[][] H, Tensor[] V, int p, int q) {
    this.H = H;
    this.V = V;
    this.p = p;
    this.q = q;
  }

  Scalar t(Scalar g) {
    Scalar dif = H[q][q].subtract(H[p][p]);
    Scalar hpq = H[p][q];
    if (Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(dif))))
      return hpq.divide(dif);
    Scalar theta = dif.divide(hpq.add(hpq));
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
      int n = H.length;
      { // preserve original values
        Scalar h = t.multiply(H[p][q]);
        Scalar hpp = H[p][p].subtract(h);
        Scalar hqq = H[q][q].add(h);
        // basis transform of symmetric matrix
        IntStream.range(0, n).forEach(j -> a(p, j, q, j));
        IntStream.range(0, n).forEach(j -> a(j, p, j, q));
        H[p][p] = hpp;
        H[q][q] = hqq;
        // SqrtPuTest shows that setting Apq == Aqp == 0 is beneficial
        H[p][q] = H[p][q].zero();
        H[q][p] = H[q][p].zero();
      }
      { // multiplication from left
        Tensor vp = V[p];
        Tensor vq = V[q];
        V[p] = vp.subtract(vq.multiply(t)).divide(ci);
        V[q] = vq.add(vp.multiply(t)).divide(ci);
      }
    }

    private class Transform {
      final Scalar rij;
      final Scalar rkl;

      // TraceTest shows that division by ci is better that multiplication with ci.reciprocal()
      public Transform(Scalar hij, Scalar hkl) {
        rij = hij.subtract(hkl.multiply(t)).divide(ci);
        rkl = hkl.add(hij.multiply(t)).divide(ci);
      }
    }

    private void a(int i, int j, int k, int l) {
      Transform jr2 = new Transform(H[i][j], H[k][l]);
      H[i][j] = jr2.rij;
      H[k][l] = jr2.rkl;
    }

    // for testing
    Tensor rotation() {
      Tensor tensor = IdentityMatrix.of(H.length);
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
