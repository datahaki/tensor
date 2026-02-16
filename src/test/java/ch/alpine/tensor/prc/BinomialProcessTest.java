// code by jph
package ch.alpine.tensor.prc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Serialization;

class BinomialProcessTest {
  @Test
  void testRandomFunction() throws ClassNotFoundException, IOException {
    RandomProcess randomProcess = BinomialProcess.of(Rational.of(1, 4));
    assertTrue(randomProcess.toString().startsWith("RenewalProcess"));
    RandomFunction randomFunction = Serialization.copy(RandomFunction.of(randomProcess));
    randomFunction.evaluate(RealScalar.of(30));
    Tensor path = randomFunction.path();
    randomFunction.evaluate(RealScalar.of(30));
    assertEquals(path.length(), randomFunction.path().length());
  }

  @Test
  void testMax() throws ClassNotFoundException, IOException {
    RandomProcess randomProcess = BinomialProcess.of(0.25);
    assertTrue(randomProcess.toString().startsWith("RenewalProcess"));
    RandomFunction randomFunction = Serialization.copy(RandomFunction.of(randomProcess));
    randomFunction.evaluate(RealScalar.of(30));
    Tensor path = randomFunction.path();
    randomFunction.evaluate(RealScalar.of(30));
    assertEquals(path.length(), randomFunction.path().length());
  }
}
