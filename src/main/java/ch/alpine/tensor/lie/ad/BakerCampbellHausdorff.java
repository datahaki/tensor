// code by jph
// adapted from code by jph 2006
package ch.alpine.tensor.lie.ad;

import java.io.Serializable;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Factorial;

/** Log[Exp[-y] Exp[-x]] = -Log[Exp[x] Exp[y]]
 * 
 * <p>The series has a linear convergence rate, that mean
 * for an increase in degree a certain number of digits
 * in the precision is achieved
 * 
 * <p>For example, for the standard so(3)
 * degree +5 gives precision of up to 3 digits
 * degree 10 gives precision of up to 6 digits
 * 
 * <p>Reference: Neeb
 * Hakenberg.de kernel.nb */
public class BakerCampbellHausdorff implements BinaryOperator<Tensor>, Serializable {
  private static final Scalar _0 = RealScalar.ZERO;
  private static final Scalar _1 = RealScalar.ONE;
  private static final int[] SIGN = { 1, -1 };

  /** @param ad tensor of rank 3 that satisfies the Jacobi identity
   * @param degree positive
   * @return */
  public static BinaryOperator<Tensor> of(Tensor ad, int degree) {
    return new BakerCampbellHausdorff( //
        JacobiIdentity.require(ad), //
        Integers.requirePositive(degree), //
        Tolerance.CHOP);
  }

  // ---
  private final Tensor ad;
  private final int degree;
  private final Chop chop;

  private BakerCampbellHausdorff(Tensor ad, int degree, Chop chop) {
    this.ad = ad;
    this.degree = degree;
    this.chop = chop;
  }

  @Override
  public Tensor apply(Tensor x, Tensor y) {
    return Total.of(series(x, y));
  }

  /** function allows to investigate the rate of convergence
   * 
   * @param x
   * @param y
   * @return list of contributions up to given degree the sum of which is the
   * result of this binary operator */
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
      Tensor pwX = IdentityMatrix.of(x.length());
      for (int m = 0; m < degree; ++m) {
        recur(pwX.dot(y).divide(Factorial.of(m)), m + 1, Tensors.empty(), Tensors.empty(), 0, true);
        pwX = adX.dot(pwX);
      }
    }

    private void recur(Tensor v, int d, Tensor p, Tensor q, int total_q, boolean incrementQ) {
      final int k = p.length();
      if (chop.allZero(v))
        return;
      Scalar fac = Stream.concat(p.stream(), q.stream()) //
          .map(Scalar.class::cast) //
          .map(Factorial.FUNCTION) //
          .reduce(Scalar::multiply) //
          .orElse(RealScalar.ONE);
      Scalar f = RealScalar.of(Math.multiplyExact(SIGN[k & 1] * (k + 1), total_q + 1)).multiply(fac);
      series.set(v.divide(f)::add, d - 1);
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
        recur(adY.dot(v), d + 1, Append.of(p, _0), Append.of(q, _1), total_q + 1, true);
        recur(adX.dot(v), d + 1, Append.of(p, _1), Append.of(q, _0), total_q, false);
      }
    }
  }
}
