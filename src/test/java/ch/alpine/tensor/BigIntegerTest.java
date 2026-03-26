// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

class BigIntegerTest {
  @Test
  void testPow() {
    assertEquals(BigInteger.ONE.pow(3), BigInteger.ONE);
    assertEquals(BigInteger.ONE.negate().pow(0), BigInteger.ONE);
    assertEquals(BigInteger.ZERO.pow(0), BigInteger.ONE);
    assertEquals(BigInteger.ONE.pow(0), BigInteger.ONE);
    assertEquals(BigInteger.TWO.pow(0), BigInteger.ONE);
  }
}
