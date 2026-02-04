// code by jph
package ch.alpine.tensor.opt.fnd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

class FactorIntegerTest {
  private static void _check(BigInteger n) {
    Map<BigInteger, Integer> map = FactorInteger.of(n);
    BigInteger p = BigInteger.ONE;
    for (Entry<BigInteger, Integer> entry : map.entrySet())
      p = p.multiply(entry.getKey().pow(entry.getValue()));
    assertEquals(p, n);
  }

  @Test
  void testZero() {
    Map<BigInteger, Integer> map = FactorInteger.of(BigInteger.ZERO);
    assertEquals(map, Collections.singletonMap(BigInteger.ZERO, 1));
  }

  @Test
  void testOne() {
    Map<BigInteger, Integer> map = FactorInteger.of(BigInteger.ONE);
    assertEquals(map, Collections.singletonMap(BigInteger.ONE, 1));
  }

  @Test
  void testTwo() {
    Map<BigInteger, Integer> map = FactorInteger.of(BigInteger.valueOf(2));
    assertEquals(map, Collections.singletonMap(BigInteger.valueOf(2), 1));
  }

  @Test
  void testSimple() {
    Map<BigInteger, Integer> map = FactorInteger.of(BigInteger.valueOf(144002631));
    assertEquals(map.size(), 4);
    assertEquals(map.get(BigInteger.valueOf(3)), (Integer) 1);
    assertEquals(map.get(BigInteger.valueOf(17)), (Integer) 2);
    assertEquals(map.get(BigInteger.valueOf(37)), (Integer) 1);
    assertEquals(map.get(BigInteger.valueOf(67)), (Integer) 2);
  }

  @Test
  void testLarge() {
    Map<Scalar, Integer> map = FactorInteger.of(Scalars.fromString("1234567891011127777333337711177"));
    assertEquals(map.get(RealScalar.of(587)), 1);
    assertEquals(map.get(Scalars.fromString("12776643967")), 1);
    assertEquals(map.get(Scalars.fromString("164611466246931013")), 1);
  }

  @RepeatedTest(10)
  void testRandom(RepetitionInfo repetitionInfo) {
    BigInteger n = new BigInteger(32 + repetitionInfo.getCurrentRepetition(), ThreadLocalRandom.current());
    _check(n);
  }

  @Test
  void testPrime() {
    BigInteger bigInteger = FactorInteger.factor(BigInteger.valueOf(13));
    assertEquals(bigInteger, BigInteger.valueOf(13));
  }

  @RepeatedTest(100)
  void testSmall(RepetitionInfo repetitionInfo) {
    _check(BigInteger.valueOf(repetitionInfo.getCurrentRepetition()));
  }

  @Test
  void testNegativeFail() {
    assertThrows(IllegalArgumentException.class, () -> FactorInteger.of(BigInteger.valueOf(-3)));
  }
}
