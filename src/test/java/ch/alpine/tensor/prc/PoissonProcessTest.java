// code by jph
package ch.alpine.tensor.prc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Quantity;

class PoissonProcessTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    RandomProcess randomProcess = PoissonProcess.of(2);
    RandomFunction randomFunction = Serialization.copy(RandomFunction.of(randomProcess));
    randomFunction.apply(RealScalar.of(30));
    int length = randomFunction.path().length();
    randomFunction.apply(RealScalar.of(20));
    assertEquals(length, randomFunction.path().length());
    assertTrue(randomProcess.toString().startsWith("RenewalProcess"));
  }

  @Test
  void testQuantity() throws ClassNotFoundException, IOException {
    RandomProcess randomProcess = PoissonProcess.of(Quantity.of(0.2, "s^-1"));
    RandomFunction randomFunction = Serialization.copy(RandomFunction.of(randomProcess));
    randomFunction.apply(Quantity.of(30, "s"));
    int length = randomFunction.path().length();
    assertThrows(Exception.class, () -> randomFunction.apply(RealScalar.of(20)));
    randomFunction.apply(Quantity.of(30, "s"));
    assertEquals(length, randomFunction.path().length());
  }
}
