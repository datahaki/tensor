// code by jph
package ch.alpine.tensor.sca.ply;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.sca.bes.BesselI;
import ch.alpine.tensor.sca.exp.Exp;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/LaguerreL.html">LaguerreL</a> */
public enum LaguerreL {
  ;
  private static final List<Polynomial> LIST = new ArrayList<>();
  static {
    LIST.add(Polynomial.of(Tensors.vector(1)));
    LIST.add(Polynomial.of(Tensors.vector(1, -1)));
  }

  /** @param index non-negative
   * @return Laguerre polynomial */
  public static Polynomial of(int index) {
    if (LIST.size() <= Integers.requirePositiveOrZero(index))
      synchronized (LIST) {
        Polynomial prev = LIST.get(LIST.size() - 2);
        Polynomial next = LIST.get(LIST.size() - 1);
        while (LIST.size() <= index) {
          int k = LIST.size() - 1;
          Polynomial f1 = Polynomial.of(Tensors.vector(2 * k + 1, -1));
          Polynomial push = next.times(f1).minus(prev.times(RealScalar.of(k))).divide(RealScalar.of(k + 1));
          LIST.add(push);
          prev = next;
          next = push;
        }
      }
    return LIST.get(index);
  }

  /** @param n
   * @param x
   * @return
   * @throws Exception if given parameters are not supported */
  public static Scalar of(Scalar n, Scalar x) {
    if (n.equals(RationalScalar.HALF)) {
      Scalar x2 = x.multiply(RationalScalar.HALF);
      Scalar s1 = x.multiply(BesselI._1(x2));
      Scalar s2 = x.subtract(RealScalar.ONE).multiply(BesselI._0(x2));
      return s1.subtract(s2).multiply(Exp.FUNCTION.apply(x2));
    }
    OptionalInt optionalInt = Scalars.optionalInt(n);
    if (optionalInt.isPresent()) {
      int _n = optionalInt.getAsInt();
      if (0 <= _n)
        return of(_n).apply(x);
    }
    throw new Throw(n, x);
  }
}
