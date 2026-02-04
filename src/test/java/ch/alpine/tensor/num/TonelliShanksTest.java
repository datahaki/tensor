// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.OptionalLong;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

class TonelliShanksTest {
  @Test
  void test() {
    long x = TonelliShanks.of(10, 13).getAsLong();
    assertTrue(x == 6 || x == 7);
  }

  @Test
  void testNo() {
    assertTrue(TonelliShanks.of(2, 5).isEmpty());
    assertTrue(TonelliShanks.of(6, 11).isEmpty());
  }

  @RepeatedTest(100)
  void testSystematisch(RepetitionInfo repetitionInfo) {
    Scalar scalar = Prime.of(100 + repetitionInfo.getCurrentRepetition());
    int prime = Scalars.intValueExact(scalar);
    for (int k = 0; k < prime; ++k) {
      OptionalLong optionalLong = TonelliShanks.of(k, prime);
      if (optionalLong.isPresent()) {
        long value = optionalLong.orElseThrow();
        assertEquals((value * value) % prime, k);
      }
    }
  }
}
