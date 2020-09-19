// code by jph
package ch.ethz.idsc.tensor.num;

import java.io.IOException;
import java.math.BigInteger;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class ScalarProductTest extends TestCase {
  public void testGaussScalar() {
    int prime = 677;
    GaussScalar gaussScalar = GaussScalar.of(432, prime);
    GaussScalar power = gaussScalar.power(RealScalar.of(-123));
    Scalar now = GaussScalar.of(1, prime);
    BinaryPower<Scalar> binaryPower = new BinaryPower<>(new ScalarProduct(now));
    for (int index = 0; index < 123; ++index)
      now = now.divide(gaussScalar);
    assertEquals(power, now);
    assertEquals(power, binaryPower.raise(gaussScalar, BigInteger.valueOf(-123)));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    BinaryPower<Scalar> binaryPower = new BinaryPower<>(new ScalarProduct(RealScalar.ONE));
    BinaryPower<Scalar> copy = Serialization.copy(binaryPower);
    assertEquals(copy.raise(RealScalar.of(2), BigInteger.valueOf(3)), RealScalar.of(8));
  }

  public void testSimple() {
    try {
      new ScalarProduct(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
