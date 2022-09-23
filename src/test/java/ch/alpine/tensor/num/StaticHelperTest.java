// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

class StaticHelperTest {
  @Test
  void testSimple() {
    BigInteger p1 = BigInteger.valueOf(7829);
    BigInteger p2 = BigInteger.valueOf(7829);
    assertNotSame(p1, p2);
    GaussScalar gs1 = GaussScalar.of(BigInteger.valueOf(3), p1);
    GaussScalar gs2 = GaussScalar.of(BigInteger.valueOf(4), p2);
    assertSame(gs1.prime(), gs2.prime());
  }
}
