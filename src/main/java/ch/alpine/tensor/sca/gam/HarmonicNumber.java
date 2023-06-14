// code by jph
package ch.alpine.tensor.sca.gam;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.IntFunction;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.pow.Power;

public class HarmonicNumber implements IntFunction<Scalar>, Serializable {
  private static final int CACHE_SIZE = 32;
  private static final Function<Scalar, HarmonicNumber> CACHE = Cache.of(HarmonicNumber::new, CACHE_SIZE);

  public static HarmonicNumber of(Scalar exponent) {
    return CACHE.apply(exponent);
  }

  public static HarmonicNumber of(Number exponent) {
    return of(RealScalar.of(exponent));
  }

  public static HarmonicNumber unit() {
    return of(RealScalar.ONE);
  }

  // ---
  private final Tensor memo = Tensors.vector(0); // initialize value for 0!
  private final ScalarUnaryOperator power;
  private final Scalar exponent;

  private HarmonicNumber(Scalar exponent) {
    this.exponent = exponent;
    power = Power.function(exponent.negate());
  }

  @Override
  public Scalar apply(int index) {
    if (memo.length() <= Integers.requirePositiveOrZero(index))
      synchronized (this) {
        Scalar x = Last.of(memo);
        while (memo.length() <= index)
          memo.append(x = x.add(power.apply(RealScalar.of(memo.length()))));
      }
    return memo.Get(index);
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("HarmonicNumber", exponent);
  }
}
