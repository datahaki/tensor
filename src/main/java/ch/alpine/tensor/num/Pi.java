// https://en.wikipedia.org/wiki/Chudnovsky_algorithm
// adapted by jph
package ch.alpine.tensor.num;

import java.math.BigDecimal;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Power;
import ch.alpine.tensor.sca.Sqrt;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Pi.html">Pi</a> */
public enum Pi {
  ;
  /** 3.14159265358979323846 */
  public static final Scalar VALUE = DoubleScalar.of(Math.PI);
  public static final Scalar HALF = DoubleScalar.of(Math.PI / 2);
  public static final Scalar TWO = DoubleScalar.of(Math.PI + Math.PI);
  /***************************************************/
  private static final Scalar _6 = RealScalar.of(6);
  private static final Scalar _13591409 = RealScalar.of(13591409);
  private static final Scalar _545140134 = RealScalar.of(545140134);
  private static final Scalar _262537412640768000 = RealScalar.of(-262537412640768000L);
  private static final Scalar _12 = RealScalar.of(12);
  private static final Scalar _426880 = RealScalar.of(426880);
  private static final Scalar _16 = RealScalar.of(16);
  private static final ScalarUnaryOperator CUBE = Power.function(3);
  private static final BigDecimal _10005 = new BigDecimal("10005");

  /** @param precision is approximately the number of correct digits in the decimal encoding
   * @return */
  public static Scalar in(int precision) {
    Scalar K = _6;
    Scalar M = RealScalar.ONE;
    Scalar L = _13591409;
    Scalar X = RealScalar.ONE;
    Scalar S = _13591409;
    int k = 0;
    Scalar root = Sqrt.FUNCTION.apply(DecimalScalar.of(_10005, precision));
    Scalar prev = RealScalar.ZERO;
    while (true) {
      ++k;
      M = CUBE.apply(K).subtract(_16.multiply(K)) //
          .multiply(M) //
          .divide(CUBE.apply(RealScalar.of(k)));
      L = L.add(_545140134);
      X = X.multiply(_262537412640768000);
      S = S.add(M.multiply(L).divide(X));
      K = K.add(_12);
      Scalar next = _426880.divide(S).multiply(root);
      if (next.equals(prev))
        return next;
      prev = next;
    }
  }
}
