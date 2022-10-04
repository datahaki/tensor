// code by jph
package ch.alpine.tensor.prc;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Sqrt;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BrownianBridgeProcess.html">BrownianBridgeProcess</a> */
public class BrownianBridgeProcess implements Serializable {
  /** @param sigma volatility positive
   * @return */
  public static BrownianBridgeProcess of(Scalar sigma) {
    return new BrownianBridgeProcess(Sign.requirePositive(sigma));
  }

  /** @param sigma volatility positive
   * @return */
  public static BrownianBridgeProcess of(Number sigma) {
    return of(RealScalar.of(sigma));
  }

  // ---
  private final Scalar sigma;

  private BrownianBridgeProcess(Scalar sigma) {
    this.sigma = sigma;
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
        Sqrt.FUNCTION.apply(clip.max().subtract(t).multiply(ratio)).multiply(sigma));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("BrownianBridgeProcess", sigma);
  }
}
