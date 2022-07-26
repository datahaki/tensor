// code by jph
package ch.alpine.tensor.qty;

import java.util.function.BinaryOperator;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.cd.CholeskyDecomposition;
import ch.alpine.tensor.mat.re.GaussianElimination;
import ch.alpine.tensor.mat.re.RowReduce;
import ch.alpine.tensor.opt.lp.LinearProgram;
import ch.alpine.tensor.red.Inner;

/** Lenient Add implements the rules
 * <pre>
 * 3[m]+0[s] == 3[m]
 * 0[m]+0[s] == 0
 * </pre>
 * that are required for some algorithms:
 * 
 * {@link CholeskyDecomposition}
 * {@link GaussianElimination}
 * {@link RowReduce}
 * {@link LinearProgram}
 * 
 * Remark: The computations throw an exception by {@link Quantity#add(Tensor)}. */
public enum LenientAdd {
  ;
  private static final BinaryOperator<Tensor> INNER = Inner.with((p, q) -> of(p, (Scalar) q));

  /** @param p
   * @param q
   * @return p + q */
  public static Scalar of(Scalar p, Scalar q) {
    final Scalar mpv;
    final Unit mpu;
    if (p instanceof Quantity) {
      Quantity qp = (Quantity) p;
      mpv = qp.value();
      mpu = qp.unit();
    } else {
      mpv = p;
      mpu = Unit.ONE;
    }
    final Scalar mqv;
    final Unit mqu;
    if (q instanceof Quantity) {
      Quantity qq = (Quantity) q;
      mqv = qq.value();
      mqu = qq.unit();
    } else {
      mqv = q;
      mqu = Unit.ONE;
    }
    if (mpu.equals(mqu))
      return p.add(q);
    boolean p_zero = Scalars.isZero(p);
    boolean q_zero = Scalars.isZero(q);
    Scalar sum = mpv.add(mqv);
    if (p_zero)
      return q_zero //
          ? sum // drop both units 0[m] + 0[s] == 0; 0[m] + 0 == 0
          : Quantity.of(sum, mqu); // 0[m] + 3[s] == 3[s]; 0 + 3[s] == 3[s]; 0[m] + 3 == 3
    if (q_zero)
      return Quantity.of(sum, mpu); // 3[m] + 0[s] == 3[m]; 3 + 0[s] == 3; 3[m] + 0 == 3[m]
    throw new Throw(p, q);
  }

  /** @param p
   * @param q with dimensions of p
   * @return sum of tensors p + q, i.e. entrywise addition */
  public static Tensor of(Tensor p, Tensor q) {
    return INNER.apply(p, q);
  }
}
