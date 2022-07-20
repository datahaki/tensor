// code by jph
package ch.alpine.tensor.prc;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Sqrt;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BrownianBridgeProcess.html">BrownianBridgeProcess</a> */
public class BrownianBridgeProcess implements Serializable {
  /** @param volatility positive
   * @return */
  public static BrownianBridgeProcess of(Scalar volatility) {
    return new BrownianBridgeProcess(Sign.requirePositive(volatility));
  }

  // ---
  private final Scalar volatility;
  private final Scalar volatility_squared;

  private BrownianBridgeProcess(Scalar volatility) {
    this.volatility = volatility;
    volatility_squared = volatility.multiply(volatility);
  }

  /** @param clip
   * @param y0 value of process at clip.min
   * @param y1 value of process at clip.max
   * @param t inside clip
   * @return distribution at given parameter t
   * @throws Exception if t is outside clip */
  public Distribution at(Clip clip, Scalar y0, Scalar y1, Scalar t) {
    Scalar ratio = t.subtract(clip.min()).divide(clip.width());
    return NormalDistribution.of( //
        LinearInterpolation.of(Tensors.of(y0, y1)).At(ratio), //
        Sqrt.FUNCTION.apply(Times.of(volatility_squared, ratio, clip.max().subtract(t))));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.of("BrownianBridgeProcess", volatility);
  }
}
