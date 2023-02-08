// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.TreeSet;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

class BigIntegerMathTest {
  @Test
  void testZeroOne() {
    assertEquals(BigIntegerMath.sqrt(BigInteger.ZERO).get(), BigInteger.ZERO);
    assertEquals(BigIntegerMath.sqrt(BigInteger.ONE).get(), BigInteger.ONE);
  }

  @Test
  void testBigInteger() {
    Optional<BigInteger> sqrt = BigIntegerMath.sqrt(new BigInteger("21065681101554527729739161805139578084"));
    assertEquals(sqrt.get(), new BigInteger("4589736495873649578"));
  }

  @Test
  void testBigIntegerFail() {
    Optional<BigInteger> optional = BigIntegerMath.sqrt(new BigInteger("21065681101554527729739161805139578083"));
    assertFalse(optional.isPresent());
  }

  @Test
  void testNegativeFail() {
    Optional<BigInteger> optional = BigIntegerMath.sqrt(new BigInteger("-16"));
    assertFalse(optional.isPresent());
  }

  @RepeatedTest(18)
  void testRandom(RepetitionInfo repetitionInfo) {
    RandomGenerator randomGenerator = new SecureRandom();
    NavigableSet<BigInteger> set = new TreeSet<>();
    int n = repetitionInfo.getCurrentRepetition();
    for (int count = 0; count < 100; ++count) {
      BigInteger bigInteger = BigIntegerMath.random(n, randomGenerator);
      set.add(bigInteger);
    }
    long min = 1 << (n - 1);
    assertTrue(min <= set.last().intValue());
    long max = 1 << n;
    assertTrue(set.last().intValue() < max);
  }

  @Test
  void testRandomBig() {
    RandomGenerator randomGenerator = new SecureRandom();
    BigIntegerMath.random(123345, randomGenerator);
  }

  @Test
  void testRandomSignum1() {
    BigInteger bigInteger = new BigInteger(1, new byte[] { (byte) 0 });
    assertEquals(bigInteger, BigInteger.ZERO);
  }
}
