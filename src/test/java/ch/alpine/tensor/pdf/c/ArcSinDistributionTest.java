package ch.alpine.tensor.pdf.c;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import test.wrap.SerializableQ;

class ArcSinDistributionTest {
  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(ArcSinDistribution.INSTANCE);
  }

  @Test
  void testSerializable() {
    SerializableQ.require(ArcSinDistribution.INSTANCE);
  }
}
