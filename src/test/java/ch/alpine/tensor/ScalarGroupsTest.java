// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.BinaryPower;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;

class ScalarGroupsTest {
  @Test
  void testGaussScalar() {
    int prime = 677;
    GaussScalar gaussScalar = GaussScalar.of(432, prime);
    GaussScalar power = gaussScalar.power(RealScalar.of(-123));
    Scalar now = GaussScalar.of(1, prime);
    BinaryPower<Scalar> binaryPower = Scalars.mul();
    for (int index = 0; index < 123; ++index)
      now = now.divide(gaussScalar);
    assertEquals(power, now);
    assertEquals(power, binaryPower.raise(gaussScalar, BigInteger.valueOf(-123)));
    String string = binaryPower.toString();
    assertEquals(string, "BinaryPower[ScalarProduct]");
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    BinaryPower<Scalar> copy = Serialization.copy(Scalars.mul());
    assertEquals(copy.raise(RealScalar.of(2), BigInteger.valueOf(3)), RealScalar.of(8));
  }

  @Test
  void testSimple() {
    assertThrows(NullPointerException.class, () -> Scalars.mul().raise(null, 0));
  }

  @Test
  void testBasics() {
    assertEquals(Scalars.add().toString(), "BinaryPower[ScalarAddition]");
    assertEquals(Scalars.add().raise(Quantity.of(3, "m"), 0), Quantity.of(0, "m"));
  }

  @Test
  void testSerializable1() throws ClassNotFoundException, IOException {
    BinaryPower<Scalar> copy = Serialization.copy(Scalars.add());
    assertEquals(copy.raise(RealScalar.of(2), BigInteger.valueOf(3)), RealScalar.of(6));
  }
}
