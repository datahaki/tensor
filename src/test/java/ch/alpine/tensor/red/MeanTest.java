// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;

class MeanTest {
  @Test
  void testSome() {
    assertEquals(Mean.of(Tensors.vector(3, 5)), RealScalar.of(4));
    assertEquals(Mean.of(Tensors.vector(3., 5., 0, 0)), RealScalar.of(2));
  }

  @Test
  void testLimitTheorem() {
    Random random = ThreadLocalRandom.current();
    Tensor tensor = Array.of(_ -> RealScalar.of(100 + 100 * random.nextGaussian()), 10000);
    Scalar mean1 = Mean.ofVector(tensor);
    Scalar mean2 = Total.ofVector(tensor.multiply(RealScalar.of(tensor.length()).reciprocal()));
    // possibly use error relative to magnitude
    Chop._10.requireClose(mean1, mean2);
  }

  @Test
  void testEmpty1() {
    assertThrows(ArithmeticException.class, () -> Mean.of(Tensors.empty()));
  }

  @Test
  void testEmpty3() {
    Tensor nestedEmpty = Tensors.of(Tensors.empty());
    assertEquals(Mean.of(nestedEmpty), Tensors.empty());
  }

  @Test
  void testDistribution() {
    assertEquals(Mean.of(UniformDistribution.unit()), RationalScalar.of(1, 2));
  }
}
