// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class StaticHelperTest {
  @Test
  public void testSimple() {
    BigInteger p1 = BigInteger.valueOf(7829);
    BigInteger p2 = BigInteger.valueOf(7829);
    assertFalse(p1 == p2);
    GaussScalar gs1 = GaussScalar.of(BigInteger.valueOf(3), p1);
    GaussScalar gs2 = GaussScalar.of(BigInteger.valueOf(4), p2);
    assertTrue(gs1.prime() == gs2.prime());
  }
}
