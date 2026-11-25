// code by jph
package ch.alpine.tensor.jet;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Drop;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.num.Binomial;

/** contains routines for JetScalar */
enum JetScalars {
  ;
  private static final JetScalar EMPTY = new JetScalar(Tensors.empty());
  public static final Cache<Integer, JetScalar> CACHE_ONE = Cache.of(JetScalars::build_one, 16);

  private static JetScalar build_one(int n) {
    return new JetScalar(UnitVector.of(n, 0));
  }

  public static JetScalar chain(Tensor vector, ScalarUnaryOperator f, ScalarUnaryOperator df) {
    if (Tensors.isEmpty(vector))
      return EMPTY;
    return new JetScalar(Join.of( //
        Tensors.of(f.apply(vector.Get(0))), //
        product(((JetScalar) df.apply(new JetScalar(opF(vector)))).jet(), opD(vector))));
  }

  /** drop function, promote derivatives, and decrease order by 1
   * 
   * @param vector
   * @return */
  private static Tensor opD(Tensor vector) {
    return Drop.head(vector, 1);
  }

  /** keep function and derivatives, and decrease order by 1
   * 
   * @param vector
   * @return */
  private static Tensor opF(Tensor vector) {
    return Drop.tail(vector, 1);
  }

  /** implementation by gemini
   * 
   * @param f
   * @param g
   * @return */
  public static Tensor product(Tensor f, Tensor g) {
    int n = Math.min(f.length(), g.length());
    if (n == 0)
      return Tensors.empty();
    Tensor result = Tensors.reserve(n);
    for (int k = 0; k < n; ++k) {
      Scalar sum = f.Get(0).multiply(g.Get(k));
      for (int j = 1; j <= k; ++j) {
        Scalar term = f.Get(j).multiply(g.Get(k - j));
        sum = sum.add(term.multiply(Binomial.of(k, j)));
      }
      result.append(sum);
    }
    return result;
  }
}
