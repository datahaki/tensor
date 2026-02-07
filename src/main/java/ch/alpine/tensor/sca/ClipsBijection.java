// code by jph
package ch.alpine.tensor.sca;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.itp.LinearInterpolation;

public class ClipsBijection implements Serializable {
  private final Clip head;
  private final Clip tail;
  private final ScalarUnaryOperator tailmap;
  private final ScalarUnaryOperator headmap;

  public ClipsBijection(Clip head, Clip tail) {
    this.head = head;
    this.tail = tail;
    tailmap = LinearInterpolation.of(tail);
    headmap = LinearInterpolation.of(head);
  }

  public Scalar forward(Scalar s) {
    return tailmap.apply(head.rescale(s));
  }

  public Scalar reverse(Scalar s) {
    return headmap.apply(tail.rescale(s));
  }

  public ScalarUnaryOperator forward() {
    return this::forward;
  }

  public ScalarUnaryOperator reverse() {
    return this::reverse;
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("ClipsBijection", head, tail);
  }
}
