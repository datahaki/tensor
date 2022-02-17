// code by jph
// adapted from code by jph 2006
package ch.alpine.tensor.lie.ad;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Factorial;

/** The BakerCampbellHausdorff formula computes
 * <pre>
 * toMatrix(BCH[x, y]) := MatrixLog[MatrixExp[toMatrix(x)] . MatrixExp[toMatrix(y)]]
 * </pre>
 * where toMatrix is the function {@link MatrixAlgebra#toMatrix(Tensor)}
 * 
 * The following identity holds
 * Log[Exp[-y] Exp[-x]] = -Log[Exp[x] Exp[y]]
 * 
 * <p>The BakerCampbellHausdorff series has a linear convergence rate, that means each
 * additional term in the series improves the precision by a certain number of digits.
 * 
 * <p>The convergence is best for elements x, y close to {0, ..., 0}
 * 
 * <p>Reference: Neeb
 * Hakenberg.de kernel.nb
 * 
 * @see MatrixAlgebra */
public class BakerCampbellHausdorff extends BchSeries implements Serializable {
  private static final Scalar _0 = RealScalar.ZERO;
  private static final Scalar _1 = RealScalar.ONE;
  private static final int[] SIGN = { 1, -1 };

  /** Hint: the implementations for the degrees 6, 8, 10 are optimized
   * 
   * @param ad tensor of rank 3 that satisfies the Jacobi identity
   * @param degree strictly positive, depth of series
   * @param chop tolerance for early abort
   * @return */
  public static BinaryOperator<Tensor> of(Tensor ad, int degree, Chop chop) {
    return switch (degree) {
    case 6 -> new BchSeries06(ad);
    case 8 -> new BchSeries08(ad);
    case 10 -> new BchSeries10(ad);
    default -> new BakerCampbellHausdorff(ad, degree, chop);
    };
  }

  /** @param ad tensor of rank 3 that satisfies the Jacobi identity
   * @param degree strictly positive
   * @return */
  public static BinaryOperator<Tensor> of(Tensor ad, int degree) {
    return of(ad, degree, Chop._14);
  }

  // ---
  private final Tensor ad;
  private final int degree;
  private final Chop chop;

  public BakerCampbellHausdorff(Tensor ad, int degree, Chop chop) {
    this.ad = JacobiIdentity.require(ad);
    this.degree = Integers.requirePositive(degree);
    this.chop = Objects.requireNonNull(chop);
  }

  @Override
  public Tensor series(Tensor x, Tensor y) {
    return new Inner(x, y).series;
  }

  private class Inner {
    private final Tensor adX;
    private final Tensor adY;
    private final Tensor series;

    public Inner(Tensor x, Tensor y) {
      series = Array.zeros(degree, x.length());
      series.set(x, 0);
      adX = ad.dot(x);
      adY = ad.dot(y);
      Tensor xn_y = y;
      for (int d = 0; d < degree; ++d) {
        recur(xn_y.divide(Factorial.of(d)), d + 1, Tensors.empty(), Tensors.empty(), 0, true);
        xn_y = adX.dot(xn_y);
      }
    }

    private void recur(Tensor v, int d, Tensor p, Tensor q, int total_q, boolean incrementQ) {
      final int k = p.length();
      Scalar fac = Stream.concat(p.stream(), q.stream()) //
          .map(Scalar.class::cast) //
          .map(Factorial.FUNCTION) //
          .reduce(Scalar::multiply) //
          .orElse(RealScalar.ONE);
      Scalar f = RealScalar.of(Math.multiplyExact(SIGN[k & 1] * (k + 1), total_q + 1)).multiply(fac);
      Tensor term = v.divide(f);
      series.set(term::add, d - 1);
      if (chop.allZero(term))
        return;
      if (d < degree) {
        if (0 < k) {
          if (incrementQ) {
            Tensor cq = q.copy();
            cq.set(RealScalar.ONE::add, k - 1);
            recur(adY.dot(v), d + 1, p, cq, total_q + 1, true);
          }
          {
            Tensor cp = p.copy();
            cp.set(RealScalar.ONE::add, k - 1);
            recur(adX.dot(v), d + 1, cp, q, total_q, false);
          }
        }
        if (1 < d) // the base case d == 1 implies adY . y == 0
          recur(adY.dot(v), d + 1, Append.of(p, _0), Append.of(q, _1), total_q + 1, true);
        recur(adX.dot(v), d + 1, Append.of(p, _1), Append.of(q, _0), total_q, false);
      }
    }
  }
}
