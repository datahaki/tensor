// code by jph
package ch.alpine.tensor.jet;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Drop;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Cache;

/* package */ enum StaticHelper {
  ;
  private static final JetScalar EMPTY = new JetScalar(Tensors.empty());
  public static final Cache<Integer, JetScalar> CACHE_ONE = Cache.of(StaticHelper::build_one, 16);

  private static JetScalar build_one(int n) {
    return new JetScalar(UnitVector.of(n, 0));
  }

  /** drop function, promote derivatives, and decrease order by 1
   * 
   * @param vector
   * @return */
  public static Tensor opD(Tensor vector) {
    return Drop.head(vector, 1);
  }

  /** keep function and derivatives, and decrease order by 1
   * 
   * @param vector
   * @return */
  public static Tensor opF(Tensor vector) {
    return Drop.tail(vector, 1);
  }

  public static Tensor product(Tensor f, Tensor g) {
    return Tensors.isEmpty(f) && Tensors.isEmpty(g) //
        ? Tensors.empty()
        : Join.of( //
            Tensors.of(f.Get(0).multiply(g.Get(0))), //
            product(opF(f), opD(g)).add(product(opD(f), opF(g))));
  }

  public static Tensor reciprocal(Tensor g) {
    if (Tensors.isEmpty(g))
      return Tensors.empty();
    Tensor opF = opF(g);
    return Join.of( //
        Tensors.of(g.Get(0).reciprocal()), //
        product(opD(g).negate(), reciprocal(product(opF, opF))));
  }

  public static Tensor power(Tensor vector, int n) {
    return n == 0 //
        ? UnitVector.of(vector.length(), 0)
        : product(power(vector, n - 1), vector);
  }

  public static JetScalar chain(Tensor vector, ScalarUnaryOperator f, ScalarUnaryOperator df) {
    if (Tensors.isEmpty(vector))
      return EMPTY;
    return new JetScalar(Join.of( //
        Tensors.of(f.apply(vector.Get(0))), //
        product(((JetScalar) df.apply(new JetScalar(opF(vector)))).vector(), opD(vector))));
  }
}
