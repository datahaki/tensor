// code by jph
package ch.alpine.tensor.lie.r2;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Cos;
import ch.alpine.tensor.sca.Mod;
import ch.alpine.tensor.sca.Sin;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/AngleVector.html">AngleVector</a>
 * 
 * @see CirclePoints */
public enum AngleVector {
  ;
  private static final Mod MOD = Mod.function(1);

  /** @param angle in radian
   * @return vector as {Cos[angle], Sin[angle]} */
  public static Tensor of(Scalar angle) {
    return Tensors.of(Cos.FUNCTION.apply(angle), Sin.FUNCTION.apply(angle));
  }

  /** For certain input the function {@link #turns(Scalar)} returns values in exact precision.
   * The function name is inspired by https://en.wikipedia.org/wiki/Turn_(geometry)
   * 
   * Examples:
   * <pre>
   * AngleVector.turns(0/2) == {+1, 0}
   * AngleVector.turns(1/2) == {-1, 0}
   * </pre>
   * 
   * @param turns of a full rotation, for instance turns == 1/2 means half rotation
   * @return AngleVector.of(turns.multiply(Pi.TWO)) */
  public static Tensor turns(Scalar turns) {
    Scalar scalar = MOD.apply(turns);
    return CirclePoint.INSTANCE.turns(scalar).orElse(of(scalar.multiply(Pi.TWO)));
  }
}
