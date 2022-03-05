// code by jph
package ch.alpine.tensor.mat.ev;

import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Hypot;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Sign;

/** The Jacobi transformations of a real symmetric matrix establishes the
 * eigenvectors and eigenvalues of given matrix.
 * 
 * Careful: {@link #vectors()} gives V* D == DiagonalMatrix[{@link #values()}]
 * 
 * Then the following identities hold
 * 
 * <pre>
 * A == V . D . V*
 * D == V* . A . V
 * </pre>
 * 
 * The matrix V == P1 * P2 * P3 * ..., is the product of the successive Jacobi
 * rotation matrices Pi. The diagonal entries of D are the eigenvalues of A and
 * the columns of V are the eigenvectors of A.
 * 
 * Implementation also works for matrices with entries of type Quantity of the
 * same unit.
 * 
 * Quote: "You cannot find eigenvectors (or eigenvalues) in a finite number of
 * exact "arithmetic" steps for matrices of size n > 4." */
/* package */ class JacobiReal extends JacobiMethod {
  /** @param matrix symmetric */
  public JacobiReal(Tensor matrix) {
    super(matrix);
  }

  @Override // from JacobiMethod
  protected void eliminate(int p, int q) {
    Scalar t = t(H[q][q].subtract(H[p][p]), H[p][q]);
    GivensRotation givensRotation = new GivensReal(t);
    givensRotation.transform(p, q);
    givensRotation.dot(p, q);
  }

  public static Scalar t(Scalar dif, Scalar hpq) {
    return Scalars.lessEquals( //
        Abs.FUNCTION.apply(hpq).multiply(RealScalar.of(100.0)), //
        Abs.FUNCTION.apply(dif).multiply(DBL_EPSILON)) //
            ? hpq.divide(dif)
            : t_exact(dif, hpq);
  }

  private static Scalar t_exact(Scalar dif, Scalar hpq) {
    Scalar theta = dif.divide(hpq.add(hpq));
    Scalar t = Abs.FUNCTION.apply(theta).add(Hypot.withOne(theta)).reciprocal();
    return Sign.isPositiveOrZero(theta) //
        ? t
        : t.negate();
  }

  /** Jacobi Rotation modifies H and V but leaves
   * Dot.of(Transpose[V], H, V) == matrix invariant
   * 
   * ci = sqrt(1 + t^2)
   * s == t / ci
   * then
   * 1/ci ^ 2 + s ^ 2 == 1 */
  /* package */ class GivensReal implements GivensRotation {
    private final Scalar ci;
    private final Scalar t;

    GivensReal(Scalar t) {
      this.ci = Hypot.withOne(t);
      this.t = t;
    }

    @Override // from GivensRotation
    public void transform(int p, int q) {
      int n = H.length;
      // preserve original values
      Scalar h = t.multiply(H[p][q]);
      Scalar hpp = H[p][p].subtract(h);
      Scalar hqq = H[q][q].add(h);
      // basis transform of symmetric matrix
      IntStream.range(0, n).forEach(j -> a(p, j, q, j));
      IntStream.range(0, n).forEach(j -> a(j, p, j, q));
      H[p][p] = hpp;
      H[q][q] = hqq;
      // SqrtPuTest shows that setting Apq == Aqp == 0 is beneficial
      // Tolerance.CHOP.requireZero(H[p][q]); // dev check
      // Tolerance.CHOP.requireZero(H[q][p]); // dev check
      H[p][q] = H[p][q].zero();
      H[q][p] = H[q][p].zero();
    }

    private void a(int i, int j, int k, int l) {
      Scalar hij = H[i][j];
      Scalar hkl = H[k][l];
      // TraceTest shows that division by ci is better that multiplication with
      // ci.reciprocal()
      H[i][j] = hij.subtract(hkl.multiply(t)).divide(ci);
      H[k][l] = hkl.add(hij.multiply(t)).divide(ci);
    }

    @Override // from GivensRotation
    public void dot(int p, int q) {
      // multiplication from left
      Tensor vp = V[p];
      Tensor vq = V[q];
      V[p] = vp.subtract(vq.multiply(t)).divide(ci);
      V[q] = vq.add(vp.multiply(t)).divide(ci);
    }
  }
}
