// code by jph
// adapted from code by jph 2006
package ch.alpine.tensor.lie.ad;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.sca.Factorial;

public class BchTrace implements Serializable {
  private static final Scalar _0 = RealScalar.ZERO;
  private static final Scalar _1 = RealScalar.ONE;
  private static final int[] SIGN = { 1, -1 };
  final Map<String, Scalar> navigableMap = new TreeMap<>();
  private final int degree;

  public BchTrace(int degree) {
    this.degree = degree;
    register("x", RealScalar.ONE);
    String pwX = "";
    for (int d = 0; d < degree; ++d) {
      recur(pwX + "y", Factorial.of(d).reciprocal(), d + 1, Tensors.empty(), Tensors.empty(), 0, true);
      pwX = "X" + pwX;
    }
  }

  private void register(String string, Scalar factor) {
    // follows from XYZw + YXWz + ZWXy + WZYx == 0
    if (string.endsWith("YXXy"))
      string = string.substring(0, string.length() - 4) + "XYXy";
    navigableMap.merge(string, factor, Scalar::add);
  }

  private void recur(String v, Scalar factor, int d, Tensor p, Tensor q, int total_q, boolean incrementQ) {
    final int k = p.length();
    Scalar fac = Stream.concat(p.stream(), q.stream()) //
        .map(Scalar.class::cast) //
        .map(Factorial.FUNCTION) //
        .reduce(Scalar::multiply) //
        .orElse(RealScalar.ONE);
    Scalar f = RealScalar.of(Math.multiplyExact(SIGN[k & 1] * (k + 1), total_q + 1)).multiply(fac);
    register(v, factor.divide(f));
    if (d < degree) {
      if (0 < k) {
        if (incrementQ) {
          Tensor cq = q.copy();
          cq.set(RealScalar.ONE::add, k - 1);
          recur("Y" + v, factor, d + 1, p, cq, total_q + 1, true);
        }
        {
          Tensor cp = p.copy();
          cp.set(RealScalar.ONE::add, k - 1);
          recur("X" + v, factor, d + 1, cp, q, total_q, false);
        }
      }
      if (1 < d)
        recur("Y" + v, factor, d + 1, Append.of(p, _0), Append.of(q, _1), total_q + 1, true);
      recur("X" + v, factor, d + 1, Append.of(p, _1), Append.of(q, _0), total_q, false);
    }
  }
}
