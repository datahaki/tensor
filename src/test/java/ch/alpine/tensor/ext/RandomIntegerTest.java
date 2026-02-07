// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.NavigableSet;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.sca.Clips;

class RandomIntegerTest {
  @RepeatedTest(18)
  void testRandom(RepetitionInfo repetitionInfo) {
    NavigableSet<BigInteger> set = new TreeSet<>();
    int n = repetitionInfo.getCurrentRepetition();
    for (int count = 0; count < 100; ++count) {
      BigInteger bigInteger = RandomInteger.random(n, ThreadLocalRandom.current());
      set.add(bigInteger);
    }
    long min = 1 << (n - 1);
    assertTrue(min <= set.last().intValue());
    long max = 1 << n;
    assertTrue(set.last().intValue() < max);
  }

  @Test
  void testRandomBig() {
    RandomInteger.random(123345, ThreadLocalRandom.current());
  }

  @Test
  void testRandomSignum1() {
    BigInteger bigInteger = new BigInteger(1, new byte[] { (byte) 0 });
    assertEquals(bigInteger, BigInteger.ZERO);
  }

  @RepeatedTest(10)
  void testRandom() {
    BigInteger bigInteger = RandomInteger.of(new BigInteger("10"), ThreadLocalRandom.current());
    Clips.interval(0, 10).requireInside(RealScalar.of(bigInteger));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4 })
  void testRandomMinMaxInclusive(int max) {
    BigInteger max_inclusive = new BigInteger("" + max);
    Tensor ott = Tensor.of(Stream.generate(() -> RealScalar.of(RandomInteger.of(max_inclusive, ThreadLocalRandom.current()))) //
        .limit(400).distinct().sorted());
    assertEquals(ott, Range.of(0, max + 1));
    final int bitLength = max_inclusive.bitLength();
    for (int k = 0; k < 100; ++k) {
      BigInteger res = RandomInteger.random(bitLength, ThreadLocalRandom.current());
      assertTrue(res.bitLength() <= bitLength);
    }
  }

  @Test
  void testRandomAll() {
    BigInteger max_inclusive = new BigInteger("123043");
    BigInteger b1 = RandomInteger.of(max_inclusive, new Random(3));
    BigInteger b2 = RandomInteger.of(max_inclusive, new Random(3));
    assertEquals(b1, b2);
  }

  @Test
  void testNegativeFail() {
    assertThrows(Exception.class, () -> RandomInteger.random(-1, ThreadLocalRandom.current()));
  }
}
