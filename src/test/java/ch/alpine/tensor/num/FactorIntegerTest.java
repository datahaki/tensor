// code by jph
package ch.alpine.tensor.num;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class FactorIntegerTest extends TestCase {
  private static void _check(BigInteger n) {
    Map<BigInteger, Integer> map = FactorInteger.of(n);
    BigInteger p = BigInteger.ONE;
    for (Entry<BigInteger, Integer> entry : map.entrySet())
      p = p.multiply(entry.getKey().pow(entry.getValue()));
    assertEquals(p, n);
  }

  public void testZero() {
    Map<BigInteger, Integer> map = FactorInteger.of(BigInteger.valueOf(0));
    // System.out.println(map);
    assertEquals(map, Collections.singletonMap(BigInteger.ZERO, 1));
  }

  public void testOne() {
    Map<BigInteger, Integer> map = FactorInteger.of(BigInteger.ONE);
    assertEquals(map, Collections.singletonMap(BigInteger.ONE, 1));
  }

  public void testTwo() {
    Map<BigInteger, Integer> map = FactorInteger.of(BigInteger.valueOf(2));
    assertEquals(map, Collections.singletonMap(BigInteger.valueOf(2), 1));
  }

  public void testSimple() {
    Map<BigInteger, Integer> map = FactorInteger.of(BigInteger.valueOf(144002631));
    assertEquals(map.size(), 4);
    assertEquals(map.get(BigInteger.valueOf(3)), (Integer) 1);
    assertEquals(map.get(BigInteger.valueOf(17)), (Integer) 2);
    assertEquals(map.get(BigInteger.valueOf(37)), (Integer) 1);
    assertEquals(map.get(BigInteger.valueOf(67)), (Integer) 2);
  }

  public void testRandom() {
    Random random = new Random();
    for (int count = 0; count < 10; ++count) {
      BigInteger n = new BigInteger(32 + count, random);
      _check(n);
    }
  }

  public void testSmall() {
    for (int count = 0; count < 100; ++count) {
      BigInteger n = BigInteger.valueOf(count);
      _check(n);
    }
  }

  public void testNegativeFail() {
    AssertFail.of(() -> FactorInteger.of(BigInteger.valueOf(-3)));
  }
}
