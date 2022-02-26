// code by edo
// adapted by jph
package ch.alpine.tensor.sca;

import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;

/** original implementation was named <em>Deadzone</em> with the purpose
 * to remove offset from joystick axis
 * 
 * "soft thresholding is the proximity operator of the 1-norm."
 * 
 * Reference:
 * "Distributed Optimization and Statistical Learning via the
 * Alternating Direction Method of Multipliers"
 * 4.4.3 Soft Thresholding
 * by Stephen Boyd, Neal Parikh, Eric Chu, Borja Peleato, and Jonathan Eckstein, 2011
 * 
 * @see CoordinateBoundingBox */
public class SoftThreshold implements ScalarUnaryOperator {
  /** @param clip
   * @return */
  public static ScalarUnaryOperator of(Clip clip) {
    return new SoftThreshold(Objects.requireNonNull(clip));
  }

  // ---
  private final Clip clip;

  private SoftThreshold(Clip clip) {
    this.clip = clip;
  }

  @Override // from ScalarUnaryOperator
  public Scalar apply(Scalar scalar) {
    return scalar.subtract(clip.apply(scalar));
  }
}
