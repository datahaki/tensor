// code by jph
package ch.alpine.tensor.num;

import java.math.BigInteger;
import java.util.function.Predicate;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.IntegerQ;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.itp.FindRoot;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Floor;

/** @see FindRoot */
public enum FindInteger {
  ;
  private static final int MAX_ITERATIONS_A = 256;
  private static final int MAX_ITERATIONS_B = 256 * 3; // 3 == 2^8
  private static final BigInteger FACTOR = BigInteger.valueOf(8);

  /** @param predicate
   * @param lo strictly positive
   * @return */
  public static Scalar min(Predicate<Scalar> predicate, BigInteger lo) {
    Integers.requirePositive(lo.signum());
    BigInteger hi = lo;
    for (int iteration = 0; iteration < MAX_ITERATIONS_A; ++iteration) {
      if (predicate.test(RealScalar.of(hi)))
        return min(predicate, Clips.interval(lo, hi));
      lo = hi;
      hi = lo.multiply(FACTOR);
    }
    throw new Throw(lo);
  }

  /** @param predicate should be monotonous, i.e. for increasing values
   * switch from false to true and subsequently never switch back to false
   * @param clip with integer bounds
   * @return smallest integer inside clip that satisfies given predicate */
  public static Scalar min(Predicate<Scalar> predicate, Clip clip) {
    Scalar lo = IntegerQ.require(clip.min());
    Scalar hi = IntegerQ.require(clip.max());
    // ---
    if (!predicate.test(hi))
      throw new Throw(clip);
    if (predicate.test(lo))
      return lo;
    // ---
    for (int index = 0; index < MAX_ITERATIONS_B; ++index) {
      Scalar width = hi.subtract(lo);
      if (width.equals(RealScalar.ONE))
        return hi;
      Scalar xc = Floor.FUNCTION.apply(lo.add(width.multiply(RationalScalar.HALF)));
      boolean tc = predicate.test(xc);
      if (tc)
        hi = xc;
      else
        lo = xc;
    }
    throw new Throw(clip);
  }
}
