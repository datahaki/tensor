// code by jph
package ch.alpine.tensor.prc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Serialization;

class BernoulliProcessTest {
  @Test
  void testRandomFunction() throws ClassNotFoundException, IOException {
    RandomProcess randomProcess = BernoulliProcess.of(RationalScalar.HALF);
    assertTrue(randomProcess.toString().startsWith("BernoulliProcess"));
    RandomFunction randomFunction = Serialization.copy(RandomFunction.of(randomProcess));
    randomFunction.apply(RealScalar.of(30));
    Tensor path = randomFunction.path();
    assertEquals(path.Get(5, 1), randomFunction.apply(RealScalar.of(5)));
    assertEquals(path.length(), 31);
  }
}
