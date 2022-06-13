// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Serialization;

class ScalarProductTest {
  @Test
  void testGaussScalar() {
    int prime = 677;
    GaussScalar gaussScalar = GaussScalar.of(432, prime);
    GaussScalar power = gaussScalar.power(RealScalar.of(-123));
    Scalar now = GaussScalar.of(1, prime);
    ScalarProduct scalarProduct = ScalarProduct.INSTANCE;
    BinaryPower<Scalar> binaryPower = new BinaryPower<>(scalarProduct);
    for (int index = 0; index < 123; ++index)
      now = now.divide(gaussScalar);
    assertEquals(power, now);
    assertEquals(power, binaryPower.raise(gaussScalar, BigInteger.valueOf(-123)));
    String string = scalarProduct.toString();
    assertTrue(string.startsWith("ScalarProduct"));
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    BinaryPower<Scalar> binaryPower = new BinaryPower<>(ScalarProduct.INSTANCE);
    BinaryPower<Scalar> copy = Serialization.copy(binaryPower);
    assertEquals(copy.raise(RealScalar.of(2), BigInteger.valueOf(3)), RealScalar.of(8));
  }

  @Test
  void testSimple() {
    assertThrows(NullPointerException.class, () -> ScalarProduct.INSTANCE.neutral(null));
  }
}
