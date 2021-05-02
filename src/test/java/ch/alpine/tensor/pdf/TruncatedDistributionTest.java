// code by jph
package ch.alpine.tensor.pdf;

import java.io.IOException;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class TruncatedDistributionTest extends TestCase {
  public void testSimple() {
    Clip clip = Clips.interval(10, 11);
    Distribution distribution = TruncatedDistribution.of(NormalDistribution.of(10, 2), clip);
    Scalar scalar = RandomVariate.of(distribution);
    assertTrue(clip.isInside(scalar));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    Clip clip = Clips.interval(10, 11);
    Distribution distribution = TruncatedDistribution.of(BinomialDistribution.of(20, DoubleScalar.of(0.5)), clip);
    Scalar scalar = RandomVariate.of(distribution);
    ExactScalarQ.require(scalar);
    assertTrue(clip.isInside(scalar));
    Serialization.copy(distribution);
    Serialization.copy(TruncatedDistribution.of(NormalDistribution.of(10, 2), clip));
  }

  public void testFail() {
    Clip clip = Clips.interval(10, 11);
    Distribution distribution = TruncatedDistribution.of(NormalDistribution.of(-100, 0.2), clip);
    AssertFail.of(() -> RandomVariate.of(distribution));
  }

  public void testNullFail() {
    AssertFail.of(() -> TruncatedDistribution.of(NormalDistribution.of(-100, 0.2), null));
    AssertFail.of(() -> TruncatedDistribution.of(null, Clips.interval(10, 11)));
  }
}
