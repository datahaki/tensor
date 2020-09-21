// code by jph
package ch.ethz.idsc.tensor.qty;

import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;

public enum RandomQuaternion {
  ;
  private static final Distribution DISTRIBUTION = DiscreteUniformDistribution.of(-30, 31);

  public static Quaternion get() {
    return Quaternion.of(RandomVariate.of(DISTRIBUTION), RandomVariate.of(DISTRIBUTION, 3));
  }

  public static boolean nonCommute(Quaternion q1, Quaternion q2) {
    return !q1.multiply(q2).equals(q2.multiply(q1));
  }
}
