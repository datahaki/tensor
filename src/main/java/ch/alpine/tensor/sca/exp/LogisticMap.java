// code by jph
package ch.alpine.tensor.sca.exp;

import java.util.function.Supplier;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.chq.FiniteScalarQ;

public class LogisticMap implements Supplier<Scalar> {
  /** @param r usually not greater than 4
   * @param seed usually in the unit interval
   * @return */
  public static Supplier<Scalar> of(Scalar r, Scalar seed) {
    return new LogisticMap(r, seed);
  }

  /** @param r usually not greater than 4
   * @param seed usually in the unit interval
   * @return */
  public static Supplier<Scalar> of(Number r, Number seed) {
    return of(RealScalar.of(r), RealScalar.of(seed));
  }

  // ---
  private final Scalar r;
  private final Scalar one;
  private Scalar x;

  private LogisticMap(Scalar r, Scalar seed) {
    this.r = r;
    one = seed.one();
    this.x = seed;
  }

  @Override
  public Scalar get() {
    Scalar y = one.subtract(x).multiply(x).multiply(r);
    x = y;
    return FiniteScalarQ.require(y);
  }
}
